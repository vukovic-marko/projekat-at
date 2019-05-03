package configuration;

import application.App;
import model.AgentsCenter;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Singleton
@Startup
public class AgentsCenterBean implements IAgentsCenterBean {

    private AgentsCenter agentsCenter;
    private List<AgentsCenter> registeredCenters;
    private Boolean masterNode;

    @PostConstruct
    public void init() {
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            String masterAddress = null;
            String nodeAddress = null;

            if (input != null) {
                prop.load(input);

                masterAddress = prop.getProperty("master_address", null);
                nodeAddress = prop.getProperty("node_address");

                agentsCenter = new AgentsCenter();
                agentsCenter.setAddress(nodeAddress);
                registeredCenters = new ArrayList<AgentsCenter>();

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
}
