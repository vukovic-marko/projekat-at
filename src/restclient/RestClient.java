package restclient;

import model.ACLMessage;
import model.AID;
import model.AgentI;
import model.AgentsCenter;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Stateless
public class RestClient implements IRestClient {

    @Override
    public Response runRemoteAgent(String address, String type, String name) {

        Response retVal;

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.
                target(address + "/agents/running/" +
                        type + "/" + name);

        Response response = target.request().put(null);

        AgentI agent = null;

        try {
            agent = response.readEntity(AgentI.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        }

        retVal = Response.status(response.getStatus()).entity(agent).build();

        response.close();
        client.close();

        return retVal;

    }

    @Override
    public void notifyAgentStarted(AID aid, Set<AgentsCenter> toNotify) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        for (AgentsCenter ac : toNotify) {
            ResteasyWebTarget target = client.target(ac.getAddress() + "/agents/running");

            Response response = target.request(MediaType.APPLICATION_JSON).
                    post(Entity.entity(Arrays.asList(aid), MediaType.APPLICATION_JSON));
            response.close();
        }
        client.close();
    }

    @Override
    public void stopAgent(AID aid) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(aid.getHost().getAddress() +
                "/agents/running/" + aid.getName() + "." + aid.getType().getName() + "." +
                aid.getType().getModule());

        // Delete poziz ne prihvata payload
        // U okviru putanje se salju naziv tipa
        // i naziv agenta razdvojeni tackom
        Response response = target.request(MediaType.APPLICATION_JSON).delete();

        response.close();
        client.close();
    }

    @Override
    public void notifyAgentStopped(List<AID> ids, Set<AgentsCenter> toNotify) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        for (AgentsCenter ac : toNotify) {
            ResteasyWebTarget target = client.target(ac.getAddress() + "/agents/stopped");

            Response response = target.request(MediaType.APPLICATION_JSON).
                    post(Entity.entity(ids, MediaType.APPLICATION_JSON));
            response.close();
        }
        client.close();
    }

    @Override
    public void sendMessageToCenter(ACLMessage message, AgentsCenter center) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(center.getAddress() + "/messages");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(message, MediaType.APPLICATION_JSON));
        response.close();
        client.close();

    }

}

