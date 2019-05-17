package configuration;

import agents.Ping;
import application.App;
import model.*;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Startup
public class AgentsCenterBean implements IAgentsCenterBean {

    private AgentsCenter agentsCenter;

    private List<AgentsCenter> registeredCenters;
    private Boolean masterNode;

    private Map<AgentType, AgentsCenter> typesMap;
    private List<AID> runningAgents;
    private Map<AID, AgentI> hostRunningAgents;

    private Map<String, List<AgentType>> clusterTypesMap;
    private String mastersAddress; // za ne-master cvorove

    @PostConstruct
    public void init() {
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            String masterAddress = null;
            String nodeAddress = null;
            String alias = null;

            agentsCenter = new AgentsCenter();
            registeredCenters = new ArrayList<AgentsCenter>();
            typesMap = new HashMap<>();
            runningAgents = new ArrayList<>();
            hostRunningAgents = new HashMap<>();
            clusterTypesMap = new HashMap<>();

            if (input != null) {
                prop.load(input);

                masterAddress = prop.getProperty("master_address", null);
                nodeAddress = prop.getProperty("node_address");
                alias = prop.getProperty("alias").trim();
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
                    clusterTypesMap.put(alias + "@" + nodeAddress, getTypes());
                } else {
                    masterNode = false;

                    mastersAddress = masterAddress;

                    // slanje zahtava master cvoru za registraciju
                    //
                    // masterNodeAddress/node
                    //
                    // sa parametrom objekta tipa agentscenter koji odgovara agentskom centru koji salje zahtev

                    ResteasyClient client = new ResteasyClientBuilder().build();
                    ResteasyWebTarget target = client.target(masterAddress + "/node");

                    Response response = target.request(MediaType.APPLICATION_JSON)
                            .post(Entity.entity(agentsCenter, MediaType.APPLICATION_JSON));

                    List<AgentsCenter> retList = response.readEntity(new GenericType<List<AgentsCenter>>(){});

                    if (retList != null) {
                        registeredCenters = new ArrayList<>(retList);
                    }

                    response.close();

                    // slanje zahteva master cvoru za dobijanje mape svih agentskih cvorova
                    // sa tipovima agenata koje oni podrzavaju
                    //
                    // masterNodeAddress/agents/classes
                    //
                    // sa parametrom mape, ciji je jedini par:
                    //      kljuc       - string koji sadrzi alias@address
                    //      vrednost    - lista podrzanih tipova agenata

                    target = client.target(masterAddress + "/agents/classes");

                    Map<String, List<AgentType>> temp = new HashMap<>();
                    temp.put(alias + "@" + nodeAddress, getTypes());

                    response = target.request(MediaType.APPLICATION_JSON)
                            .post(Entity.entity(temp, MediaType.APPLICATION_JSON));

                    clusterTypesMap = response.readEntity(new GenericType<Map<String, List<AgentType>>>(){});

                    response.close();

                    // slanje zahteva master cvoru za dobijanje liste svih pokrenutih agenata u mreze
                    //
                    // master/agents/running

                    target = client.target(masterAddress + "/agents/running");

                    response = target.request(MediaType.APPLICATION_JSON).get();

                    runningAgents = response.readEntity(new GenericType<List<AID>>() {});

                    response.close();

                    client.close();
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

    private void addAgentTypesFromConfig(String agentsProp) {
        String agents[] = agentsProp.split(",");
        for (String agent : agents) {
            String parts[] = agent.split("\\.");
            AgentType agentType = new AgentType(parts[1], parts[0]);
            typesMap.put(agentType, agentsCenter);
        }
    }

    public void sendAgentsCenters(AgentsCenter center, List<AgentsCenter> receivers) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        for (AgentsCenter c : receivers) {
            ResteasyWebTarget target = client.target(c.getAddress() + "/node");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(center, MediaType.APPLICATION_JSON));
            response.close();
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
    public List<AgentsCenter> getRegisteredCenters() {
        return registeredCenters;
    }

    public void setRegisteredCenters(List<AgentsCenter> registeredCenters) {
        this.registeredCenters = registeredCenters;
    }

    public Boolean isMasterNode() {
        return masterNode;
    }

    public void setMasterNode(Boolean masterNode) {
        this.masterNode = masterNode;
    }

    public void addToRegisteredCenters(List<AgentsCenter> list) {
        this.registeredCenters.addAll(list);
    }

    private Context createContext() throws NamingException {
        Hashtable<String, Object> jndiProps = new Hashtable<>();
        jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        return new InitialContext(jndiProps);
    }

    @Override
    public List<AgentType> getTypes() {

        return new ArrayList(typesMap.keySet());
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

                        typesMap.put(type, agentsCenter);
                    }
                }
            }
        }
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
    public Map<AgentType, AgentsCenter> getTypesMap() {
        return typesMap;
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

        Context ctx = new InitialContext();

        String module = type.getModule().replaceAll("\\.", "/");

        String lookupStr = AGENTS_LOOKUP + module + "/"
                + type.getName() + AGENT_NAME_END;

        AgentI agent = (AgentI) ctx.lookup(lookupStr);

        AID aid = new AID(name, agentsCenter, type);

        agent.init(aid);

        runningAgents.add(aid);
        hostRunningAgents.put(aid, agent);

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
    public void stopAgent(String aidName, String typeName) {

        for (AID aid : runningAgents) {

            if (aid.getName().equals(aidName) &&
                    aid.getType().getName().equals(typeName)) {

                runningAgents.remove(aid);

            }

        }

    }

    @Override
    public boolean stopHostAgent(AID aid) {

        if (!runningAgents.remove(aid)) {
            return false;
        }

        List<AID> ids = null;
        for (AID i : hostRunningAgents.keySet()) {
            if (i.equals(aid)) {
                hostRunningAgents.remove(i);
                // Posto je aid kljuc moguce ce biti obrisati samo jedan element
                break;
            }
        }

        return true;
    }

    @Override
    public void deliverMessageToAgent(ACLMessage message, AID aid) {

        AgentI agent = null;

        for (AID i : hostRunningAgents.keySet()) {
            if (aid.equals(i)) {
                agent = hostRunningAgents.get(i);
                break;
            }
        }

        if (agent == null) {
            return;
        }

        agent.handleMessage(message);

    }

    public Map<String, List<AgentType>> getClusterTypesMap() {
        return clusterTypesMap;
    }

    @Override
    public void setClusterTypesMap(Map<String, List<AgentType>> clusterTypesMap) {
        this.clusterTypesMap = clusterTypesMap;
    }

    @Schedule(hour = "*", minute = "*", second = "*/10")
    public void heartbeat() {

        if (!masterNode) {
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target;
            Response response;

            List<AgentsCenter> toRemove = new ArrayList<>();

            for (AgentsCenter center : registeredCenters) {
                try {
                    target = client.target(center.getAddress() + "/node");
                    response = target.request(MediaType.APPLICATION_JSON).get();
                    System.out.println(center.getAlias() + "@" + response.getStatus());
                    response.close();
                } catch (Exception e) {
                    System.out.println(center.getAlias() + "@" + center.getAddress() + " izasao iz mreze");

                    target = client.target(mastersAddress + "/node/" + center.getAlias());

                    for (AgentsCenter c : registeredCenters) {
                        if (c.getAlias().equals(center.getAlias())) {
                            toRemove.add(c);
                            break;
                        }
                    }

                    response = target.request(MediaType.APPLICATION_JSON).header("sender", agentsCenter.getAddress()).delete();
                    response.close();
                }
            }

            toRemove.forEach(item -> {
                registeredCenters.remove(item);
                clusterTypesMap.remove(item.getAlias() + "@" + item.getAddress());
            });

            client.close();
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
