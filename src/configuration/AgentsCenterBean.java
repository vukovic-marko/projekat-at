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
public class AgentsCenterBean {

    private AgentsCenter agentsCenter;
    private List<AgentsCenter> registeredCenters;

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

                if (masterAddress == null) {
                    System.out.println("master");
                    registeredCenters = new ArrayList<AgentsCenter>();
                } else {
                    System.out.println("slave");
                    System.out.println("master_address: " + masterAddress);

                    ResteasyClient client = new ResteasyClientBuilder().build();
                    String url = masterAddress + "/node";
                    System.out.println(url);
                    ResteasyWebTarget target = client.target(url);
                    Response response = target.request()
                            .post(Entity.entity(agentsCenter, MediaType.APPLICATION_JSON));

                    System.out.println(response.getStatus());
                }
            } else {
                System.err.println("Nije pronadjena config.properties datoteka!");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

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
}
