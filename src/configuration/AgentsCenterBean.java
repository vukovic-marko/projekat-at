package configuration;

import application.App;
import model.*;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.annotation.PostConstruct;
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

    //private final String AGENTS_LOOKUP = "java:jboss/exported/projekat_at_war_exploded/";
    private final String AGENTS_LOOKUP = "java:jboss/exported/";
    private final String NAME_END = "!" + AgentI.class.getName();

    private AgentsCenter agentsCenter;

    private List<AgentsCenter> registeredCenters;
    private Boolean masterNode;

    private Map<AgentType, AgentsCenter> typesMap;
    private List<Agent> runningAgents;

    private Context context;

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

            try {
                initContext();
            } catch (NamingException e) {
                e.getMessage();
            }

            if (input != null) {
                prop.load(input);

                masterAddress = prop.getProperty("master_address", null);
                nodeAddress = prop.getProperty("node_address");
                alias = prop.getProperty("alias").trim();
                String agentsProp = prop.getProperty("agents", null);

                agentsCenter.setAddress(nodeAddress);
                agentsCenter.setAlias(alias);

                if (agentsProp != null) {
                    addAgentTypes(agentsProp);
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

    private void addAgentTypes(String agentsProp) {
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

    public AgentsCenter getAgentsCenter() {
        return agentsCenter;
    }

    public void setAgentsCenter(AgentsCenter agentsCenter) {
        this.agentsCenter = agentsCenter;
    }

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

    private void initContext() throws NamingException {
        Hashtable<String, Object> jndiProps = new Hashtable<>();
        jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        context = new InitialContext(jndiProps);
    }

    @Override
    public List<AgentType> getTypes() {

        return new ArrayList(typesMap.keySet());
    }

    @Override
    public List<AgentType> getAvaliableAgentTypes() throws NamingException {

        List<AgentType> result = new ArrayList<>();

        NamingEnumeration<NameClassPair> moduleList = context.list(AGENTS_LOOKUP);

        while (moduleList.hasMore()) {

            String module = moduleList.next().getName();

            NamingEnumeration<NameClassPair> agentList = context.list(AGENTS_LOOKUP + "/" + module);

            while (agentList.hasMore()) {
                String name = agentList.next().getName();
                if (name != null && name.endsWith(NAME_END)) {
                    AgentType type = new AgentType();
                    type.setModule(module);
                    type.setName(name.substring(0, name.indexOf('!')));

                    result.add(type);
                }
            }
        }

        return  result;

    }
}
