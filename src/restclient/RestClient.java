package restclient;

import model.AgentI;
import model.AgentType;
import model.AgentsCenter;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
public class RestClient implements IRestClient {

    @Override
    public void runRemoteAgent(AgentsCenter center, String type, String name) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.
                target(center.getAddress() + "/agents/running/" +
                        name + "/" + type);

        Response response = target.request().put(null);

        List<AgentType> retList = response.readEntity(List.class);

        response.close();
        client.close();

    }

    @Override
    public void notifyAgentStarted(AgentI agent, List<AgentsCenter> toNotify) {
        for (AgentsCenter ac : toNotify) {
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(ac.getAddress() + "/agents/run");

            Response response = target.request(MediaType.APPLICATION_JSON).
                    put(Entity.entity(agent, MediaType.APPLICATION_JSON));

        }
    }
}

