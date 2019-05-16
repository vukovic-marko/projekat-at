package restclient;

import model.*;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ejb.Stateless;
import javax.print.attribute.standard.Media;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void notifyAgentStarted(AID aid, List<AgentsCenter> toNotify) {
        for (AgentsCenter ac : toNotify) {
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(ac.getAddress() + "/agents/running");

            Response response = target.request(MediaType.APPLICATION_JSON).
                    post(Entity.entity(Arrays.asList(aid), MediaType.APPLICATION_JSON));

        }
    }

    @Override
    public void stopAgent(AID aid) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(aid.getHost().getAddress() +
                "/agents/running/" + aid.getName() + "." + aid.getType().getName());

        // Delete poziz ne prihvata payload
        // U okviru putanje se salju naziv tipa
        // i naziv agenta razdvojeni tackom
        Response response = target.request(MediaType.APPLICATION_JSON).delete();


    }

    @Override
    public void notifyAgentStopped(AID aid, List<AgentsCenter> toNotify) {
        // TODO Proveriti da li se moze client izbaciti van petlje
        for (AgentsCenter ac : toNotify) {
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(ac.getAddress() + "/agents/running");

            Response response = target.request(MediaType.APPLICATION_JSON).
                    post(Entity.entity(Arrays.asList(aid), MediaType.APPLICATION_JSON));

        }
    }

    @Override
    public void sendMessageToCenter(ACLMessage message, AgentsCenter center) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(center.getAddress() + "/messages");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(message, MediaType.APPLICATION_JSON));



    }

}

