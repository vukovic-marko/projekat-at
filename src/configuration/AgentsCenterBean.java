package configuration;

import agents.test.pingpong.Ping;
import application.App;
import messaging.IMessenger;
import model.*;
import model.dto.AggregatorMessage;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import restclient.IRestClient;
import websocket.ConsoleEndpoint;
import websocket.MessageType;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.naming.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
@Startup
@Lock(LockType.READ)
//@AccessTimeout(value = 60, unit = TimeUnit.SECONDS)
public class AgentsCenterBean implements IAgentsCenterBean {

    private AgentsCenter agentsCenter;
    private List<AgentType> hostTypes;
    private Map<AID, AgentI> hostRunningAgents;

    private Boolean masterNode;
    private String mastersAddress; // za ne-master cvorove

    private Set<AgentsCenter> registeredCenters;

    private Map<String, List<AgentType>> clusterTypesMap;
    private List<AID> runningAgents;

    //private ResultsDTO results;
    //private Boolean ready;

    @EJB
    private ConsoleEndpoint ws;

    @EJB
    private IRestClient restClient;

    @EJB
    private IMessenger messenger;

    @PostConstruct
    private void init() {

        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            agentsCenter = new AgentsCenter();
            registeredCenters = new HashSet<>();
            runningAgents = new ArrayList<>();
            hostRunningAgents = new HashMap<>();
            clusterTypesMap = new HashMap<>();
            hostTypes = new ArrayList<>();

            if (input != null) {
                prop.load(input);

                String masterAddress = prop.getProperty("master_address", null);
                String nodeAddress = prop.getProperty("node_address");
                String alias = prop.getProperty("alias").trim();
                String agentsConfig = prop.getProperty("ignore_agents", "");
                List<String> agentsConfigList = new ArrayList<>();

                if (!agentsConfig.equals("")) {
                    agentsConfigList = Arrays.asList(agentsConfig.split(","));
                    agentsConfigList = agentsConfigList.stream().map(a -> a.trim()).collect(Collectors.toList());
                }

                agentsCenter.setAddress(nodeAddress);
                agentsCenter.setAlias(alias);

                try {
                    addHostCenterTypes(AGENTS_LOOKUP, agentsConfigList);
                } catch (NamingException e) {
                    e.getMessage();
                }

                if (masterAddress == null) {
                    masterNode = true;
                    clusterTypesMap.put(alias + "@" + nodeAddress, getHostTypes());
                } else {
                    masterNode = false;

                    mastersAddress = masterAddress;

                    // slanje zahtava master cvoru za registraciju
                    //
                    // masterNodeAddress/node
                    //
                    // sa parametrom objekta tipa agentscenter koji odgovara agentskom centru koji salje zahtev

                    ResteasyClient client = new ResteasyClientBuilder().socketTimeout(5, TimeUnit.SECONDS).build();
                    ResteasyWebTarget target;
                    Response response;

                    Boolean retry = false;

                    while(true) {
                        try {
                            target = client.target(masterAddress + "/node");

                            response = target.request(MediaType.APPLICATION_JSON)
                                    .post(Entity.entity(agentsCenter, MediaType.APPLICATION_JSON));

                            if (response.getStatus() == 400) {
                                throw new Exception("AgentsCenter with the same alias already registered!");
                            }

                            List<AgentsCenter> retList = response.readEntity(new GenericType<List<AgentsCenter>>() {
                            });

                            if (retList != null) {
                                registeredCenters = new HashSet<>(retList);
                            }

                            response.close();

                            break;
                        } catch (Exception e) {

                            if (retry) {
                                removeSelfFromOtherAgents();

                                System.err.println("Unsuccessful registration on 2nd try, caused by: ");
                                //throw e;
                                e.printStackTrace();
                                //throw new EJBException();
                                destroy();
                                return;
                            }

                            System.err.println("Unsuccessful registration, retrying...");

                            retry = true;
                        }

                    }

                    System.out.println("Established connection with master node at " + masterAddress);
                    ws.sendMessage("Established connection with master node at " + masterAddress);

                    // slanje zahteva master cvoru za dobijanje mape svih agentskih cvorova
                    // sa tipovima agenata koje oni podrzavaju
                    //
                    // masterNodeAddress/agents/classes
                    //
                    // sa parametrom mape, ciji je jedini par:
                    //      kljuc       - string koji sadrzi alias@address
                    //      vrednost    - lista podrzanih tipova agenata

                    while (true) {
                        try {
                            target = client.target(masterAddress + "/agents/classes");

                            Map<String, List<AgentType>> temp = new HashMap<>();
                            temp.put(alias + "@" + nodeAddress, getHostTypes());

                            response = target.request(MediaType.APPLICATION_JSON)
                                    .post(Entity.entity(temp, MediaType.APPLICATION_JSON));

                            System.out.println("Host agent types sent");
                            ws.sendMessage("Host agent types sent");

                            clusterTypesMap = response.readEntity(new GenericType<Map<String, List<AgentType>>>() {
                            });

                            response.close();
                            break;
                        } catch (Exception e) {

                            if (retry) {
                                removeSelfFromOtherAgents();

                                System.err.println("Did not receive supported agent types in cluster on 2nd try, caused by: ");
                                e.printStackTrace();
                                destroy();
                                return;
                            }

                            System.err.println("Did not receive supported agent types in cluster, retrying...");

                            retry = true;
                        }
                    }

                    System.out.println("Received nodes list and agent types from master node");
                    ws.sendMessage("Received nodes list and agent types from master node");

                    // slanje zahteva master cvoru za dobijanje liste svih pokrenutih agenata u mreze
                    //
                    // master/agents/running

                    while(true) {
                        try {
                            target = client.target(masterAddress + "/agents/running");

                            response = target.request(MediaType.APPLICATION_JSON).get();

                            runningAgents = response.readEntity(new GenericType<List<AID>>() {});

                            response.close();

                            client.close();
                            break;
                        } catch (Exception e) {

                            if (retry) {
                                removeSelfFromOtherAgents();

                                System.err.println("Did not receive running agents in cluster on 2nd try, caused by: ");
                                e.printStackTrace();
                                destroy();
                                return;
                            }

                            System.err.println("Did not receive running agents in cluster, retrying...");

                            retry = true;
                        }
                    }

                    System.out.println("Received running agents list from master node");
                    ws.sendMessage("Received running agents list from master node");
                }
            } else {
                System.err.println("Nije pronadjena config.properties datoteka!");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @PreDestroy
    public void onDestroy() {
    }

    public void sendAgentsCenters(AgentsCenter center, List<AgentsCenter> receivers) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        for (AgentsCenter c : receivers) {
            if (!c.equals(agentsCenter) && !c.equals(center)) {
                ResteasyWebTarget target = client.target(c.getAddress() + "/node");
                Response response = target.request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(center, MediaType.APPLICATION_JSON));
                response.close();
            }
        }
        client.close();
    }

    @Override
    public AgentsCenter getAgentsCenter() {
        return agentsCenter;
    }

    public void setAgentsCenter(AgentsCenter agentsCenter) {
        this.agentsCenter = agentsCenter;
    }

    @Override
    public Set<AgentsCenter> getRegisteredCenters() {
        return registeredCenters;
    }

    public Boolean isMasterNode() {
        return masterNode;
    }

    public void setMasterNode(Boolean masterNode) {
        this.masterNode = masterNode;
    }

    /*public void addToRegisteredCenters(List<AgentsCenter> list) {
        this.registeredCenters.addAll(list);
    }*/

    private Context createContext() throws NamingException {
        Hashtable<String, Object> jndiProps = new Hashtable<>();
        jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        return new InitialContext(jndiProps);
    }

    private void addHostCenterTypes(String path, List<String> ignoredAgents) throws NamingException {

        Context context = createContext();

        NamingEnumeration<NameClassPair> list = context.list(path);

        while (list.hasMore()) {

            NameClassPair pair = list.next();

            String name = pair.getName();
            String className = pair.getClassName();
            className = className.substring(className.lastIndexOf('.') + 1);

            if (className.equals(CONTEXT_CLASS_NAME)) {
                addHostCenterTypes(path + name + "/", ignoredAgents);
            } else {
                if (name != null && name.endsWith(AGENT_NAME_END)) {

                    name = name.substring(0, name.indexOf('!'));

                    // Da se ne bi uzeli svi agenti koji postoje, radi lakseg testiranja
                    if (!ignoredAgents.contains(name)) {
                        AgentType type = new AgentType();
                        String module = path.substring(AGENTS_LOOKUP.length());
                        module = module.replace('/', '.');
                        module = module.substring(0, module.lastIndexOf('.'));
                        type.setModule(module);
                        type.setName(name);

                        hostTypes.add(type);
                    }
                }
            }
        }
    }

    @Override
    public List<AgentType> getHostTypes() {

        return hostTypes;

    }

    @Override
    public List<AgentType> getAvaliableAgentTypes() throws NamingException {

        List<AgentType> result = new ArrayList<>();

        Context context = createContext();

        NamingEnumeration<NameClassPair> moduleList = context.list(AGENTS_LOOKUP);

        while (moduleList.hasMore()) {

            String module = moduleList.next().getName();

            NamingEnumeration<NameClassPair> agentList = context.list(AGENTS_LOOKUP + "/" + module);

            while (agentList.hasMore()) {
                String name = agentList.next().getName();

                if (name != null && name.endsWith(AGENT_NAME_END)) {
                    AgentType type = new AgentType();
                    type.setModule(module);
                    type.setName(name.substring(0, name.indexOf('!')));

                    result.add(type);
                }
            }
        }

        return  result;

    }

    @Override
    public List<AID> getRunningAgents() {
        return runningAgents;
    }

    @Override
    public void setRunningAgents(List<AID> runningAgents) {
        this.runningAgents = runningAgents;
    }

    @Override
    public List<String> traverse() {

        List<String> ret;
        ret = new ArrayList<>();

        Context ctx;
        AgentI ag;
        try {
            ctx = new InitialContext();

            Object o = ctx.lookup("java:app/web/Ping");

            ag = (AgentI) o;

            ag.handleMessage(new ACLMessage());

            if (ag instanceof Ping) {
                ag.handleMessage(new ACLMessage());
            }

            ag = (AgentI) ctx.lookup("java:app/web/Pong");

            ag.handleMessage(new ACLMessage());

        } catch (NamingException e) {
            return null;
        }

        return ret;

    }

    public AgentI runAgent(AgentType type, String name) throws NamingException {

        // Provera da li postoji agent sa kreiranim aid-om
        AID aid = new AID(name, agentsCenter, type);

        for (AID id : hostRunningAgents.keySet()) {
            if (id.equals(aid)) {
                return null;
            }
        }

        Context ctx = new InitialContext();

        String module = type.getModule().replaceAll("\\.", "/");

        String lookupStr = AGENTS_LOOKUP + module + "/"
                + type.getName() + AGENT_NAME_END;

        AgentI agent = (AgentI) ctx.lookup(lookupStr);

        agent.init(aid);

        runningAgents.add(aid);
        hostRunningAgents.put(aid, agent);

        ws.agentStarted(agent.getAid().getName(), agent.getAid().getType().getName(),
                agent.getAid().getHost().getAlias());

        return agent;

    }

    public void addRunningAgents(List<AID> running) {

        runningAgents.addAll(running);

    }

    @Override
    public Map<AID, AgentI> getHostRunningAgents() {
        return hostRunningAgents;
    }

    @Override
    public boolean stopHostAgent(AID aid) {

        if (!runningAgents.remove(aid)) {
            return false;
        }

        AgentI removed = hostRunningAgents.remove(aid);

        if (removed!=null) {
            removed.stop();
            ws.agentStopped(aid.getName(), aid.getType().getName(), agentsCenter.getAlias());
            return true;
        }

        return false;
    }

    public Map<String, List<AgentType>> getClusterTypesMap() {
        return clusterTypesMap;
    }

    @Override
    public void setClusterTypesMap(Map<String, List<AgentType>> clusterTypesMap) {
        this.clusterTypesMap = clusterTypesMap;
    }

    @Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
    public void heartbeat() {

//        if (!masterNode) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target;
        Response response;

        List<AgentsCenter> toRemove = new ArrayList<>();

        if (registeredCenters != null && registeredCenters.size() > 0)
        for (AgentsCenter center : registeredCenters) {
            int i = 0;
            while(true) {
//                if (i != 0) {
//                    System.out.println("sleeping for 2s");
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                try {
                    target = client.target(center.getAddress() + "/node");
                    response = target.request(MediaType.APPLICATION_JSON).get();
                    System.out.println(center.getAlias() + "@" + response.getStatus());
                    response.close();
                    break;
                } catch (Exception e) {
                    System.out.println("not found in try " + i);
                    if (i == 1) {
                        System.out.println(center.getAlias() + "@" + center.getAddress() + " izasao iz mreze");

                        if (masterNode) {
                            toRemove.add(center);
                        } else {
                            target = client.target(mastersAddress + "/node/" + center.getAlias());

                            for (AgentsCenter c : registeredCenters) {
                                if (c.getAlias().equals(center.getAlias())) {
                                    toRemove.add(center);
                                    break;
                                }
                            }

                            response = target.request(MediaType.APPLICATION_JSON).header("sender", agentsCenter.getAddress()).delete();
                            response.close();
                        }
                        break;
                    }
                }
                i++;
            }
        }

        toRemove.forEach(item -> {
            String key = item.getAlias() + "@" + item.getAddress();
            registeredCenters.remove(item);
            clusterTypesMap.remove(key);
            ws.sendMessage("Node '" + key + "' left the cluster", MessageType.UPDATE_ALL);

            // Izbacivanje agenata hosta koji je napustio klaster
            runningAgents = runningAgents.stream().filter(aid -> !aid.getHost().getAddress().equals(item.getAddress()) &&
                    !aid.getHost().getAlias().equals(item.getAlias())).collect(Collectors.toList());

            // Suvisno je jer ce svaki centar prepoznati da mu se ne javlja onaj centar koji je napustio klaster
            // restClient.notifyCenterLeft(key, registeredCenters);

        });

        client.close();
//        }

    }

    @Override
    public Set<AgentType> getAllTypes() {

        Set<AgentType> types = new HashSet<>();

        for (List<AgentType> nodeTypes : clusterTypesMap.values()) {
            types.addAll(nodeTypes);
        }

        return types;

    }

    @Override
    public void broadcastMessage(String wsMessage) {

        restClient.broadcastMessage(wsMessage, getRegisteredCenters());

    }

    @Override
    public AgentsCenter deleteByAlias(String alias) {

        for (AgentsCenter ac : registeredCenters) {
            if (ac.getAlias().equals(alias)) {
                registeredCenters.remove(ac);
                return ac;
            }
        }

        return null;
    }

    /*public String getDBName() {
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                Properties props = new Properties();
                props.load(input);

                return props.getProperty("db", "default");

            } else{
                System.err.println("Nije pronadjena config.properties datoteka!");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }*/

    /*
    @Override
    public ResultsDTO waitResults() {

        ready = false;
        while (!ready) {
            try {
                ready.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return results;

    }

    @Override
    public void setResults(ResultsDTO results) {
        this.results = results;
    }

    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
    }*/

    private static final String AGGREGAOR_TYPE = "projekat_at_ear_exploded.web.Aggregator";

    @Override
    public  void notifyAggregator(AggregatorMessage aMsg) {

        ACLMessage msg = new ACLMessage(Performative.REQUEST);

        msg.setContentObj(aMsg);

        // Pronadji agregatora
        List<AID> agents = null;
        try {

            agents = runningAgents.stream().filter(aid -> aid.getType().getName().equals("Aggregator")).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
        }

        AID receiver = null;
        if (agents==null || agents.size()==0) {
            // Napravi novog agregatora
            AgentI aggregator = generateAgent(AGGREGAOR_TYPE, "GENERATED_0");
            receiver = aggregator.getAid();
        } else {
            // Proveri da li je agregator na hostu
            // i da li je slobodan
            for (AID aid : agents) {
                if (aid.getHost().equals(agentsCenter)) {

                    AgentI ag = hostRunningAgents.get(aid);

                    if (!ag.isBusy()) {
                        receiver = aid;
                        break;
                    }
                }
            }

            if (receiver==null) {
                // Napravi novog agregatora
                AgentI aggregator = generateAgent(AGGREGAOR_TYPE, "GENERATED_" + agents.size());
                receiver = aggregator.getAid();
            }

        }

        msg.addReceiver(receiver);

        messenger.sendMessage(msg);

    }

    private AgentI generateAgent(String type, String name) {

        AgentType agentType = new AgentType(AGGREGAOR_TYPE);

        AgentI aggregator = null;
        try {
            aggregator = runAgent(agentType, name);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return aggregator;

    }

    // radi blokiranja heartbeat() funkcije
    private void destroy() {

        agentsCenter = null;
        hostTypes = null;
        hostRunningAgents = null;

        masterNode = null;
        mastersAddress = null;

        registeredCenters = null;

        clusterTypesMap = null;
        runningAgents = null;
    }

    private void removeSelfFromOtherAgents() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target;
        Response response;
        if (registeredCenters != null && registeredCenters.size() > 0) {
            for (AgentsCenter center : registeredCenters) {
                target = client.target(center.getAddress() + "/node/" + agentsCenter.getAlias());
                response = target.request(MediaType.APPLICATION_JSON).header("sender", agentsCenter.getAddress())
                        .delete();
                response.close();
            }
        }
    }



    //    /**
//     * Funkcija koja pronalazi agenta zapisanog u notaciji alias@address, i vraca ga kao objekat tipa AgentsCenter.
//     *
//     * @param str
//     * @return
//     */
//    public AgentsCenter getAgentsCenterFromString(String str) {
//        AgentsCenter temp = new AgentsCenter();
//
//        String[] parts = str.split("@");
//        if (parts.length == 1)
//            if (mastersAddress.equals(parts[0])) {
//                temp.setAddress(mastersAddress);
//                return temp;
//            }
//
//        if (agentsCenter.getAlias().equals(parts[0]) && agentsCenter.getAddress().equals(parts[1])) {
//            temp = agentsCenter;
//            return temp;
//        }
//
//        for (AgentsCenter center : registeredCenters) {
//            if (center.getAlias().equals(parts[0]) && center.getAddress().equals(parts[1])) {
//                temp = center;
//                return temp;
//            }
//        }
//
//        return null;
//    }
}
