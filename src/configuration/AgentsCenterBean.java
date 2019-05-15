package configuration;

import agents.Ping;
import application.App;
import model.*;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.lang.model.type.ArrayType;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Entity;
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
    private List<AgentI> runningAgents;

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
                    System.out.println("master");
                    masterNode = true;
                } else {
                    System.out.println("slave");
                    masterNode = false;
                    System.out.println("master_address: " + masterAddress);

                    ResteasyClient client = new ResteasyClientBuilder().build();
                    ResteasyWebTarget target = client.target(masterAddress + "/node");

                    AgentsCenter a = new AgentsCenter();
                    a.setAddress(nodeAddress);

                    Response response = target.request(MediaType.APPLICATION_JSON)
                            .post(Entity.entity(a, MediaType.APPLICATION_JSON));

                    List<AgentsCenter> retList = response.readEntity(List.class);

                    if (retList != null) {
                        registeredCenters.addAll(retList);
                    }

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

    public void sendMessage(ACLMessage message) {

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
    public List<AgentI> getRunningAgents() {
        return runningAgents;
    }

    @Override
    public Map<AgentType, AgentsCenter> getTypesMap() {
        return typesMap;
    }

    @Override
    public List<String> traverse() {

        List<String> ret = new ArrayList<String>();

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

        runningAgents.add(agent);

        return agent;

    }
}
