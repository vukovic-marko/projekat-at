package controllers;

import configuration.AgentsCenterBean;
import model.AgentsCenter;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
public class AgentsCenterController {

    @EJB
    private AgentsCenterBean agentsCenterBean;

    @POST
    @Path("/node")
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(List<AgentsCenter> agentsCenter) {
        if (agentsCenterBean.getMasterNode() == true) {
            List<AgentsCenter> nodes1 = agentsCenterBean.getRegisteredCenters();

            agentsCenterBean.sendAgentsCenters(agentsCenter, nodes1);

//            agentsCenterBean.sendAgentsCenters(nodes1, agentsCenter);

//            ResteasyClient client = new ResteasyClientBuilder().build();
//            System.out.println(agentsCenter.get(0).getAddress() + "/node");
//            ResteasyWebTarget target = client.target(agentsCenter.get(0).getAddress() + "/node");
//            Response response = target.request(MediaType.APPLICATION_JSON)
//                    .post(Entity.entity(nodes1, MediaType.APPLICATION_JSON));
//            response.close();
//            client.close();

            agentsCenterBean.addToRegisteredCenters(agentsCenter);
        } else {
            if (agentsCenter != null)
                if (agentsCenter.size() > 0) {
                    agentsCenterBean.addToRegisteredCenters(agentsCenter);
                }
        }

    }


    @GET
    @Path("/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AgentsCenter> getNodes() {
        return agentsCenterBean.getRegisteredCenters();
    }
}
