package controllers;

import configuration.IAgentsCenterBean;
import model.AgentsCenter;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class AgentsCenterController {

    @EJB
    private IAgentsCenterBean agentsCenterBean;

    @POST
    @Path("/node")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AgentsCenter> register(AgentsCenter agentsCenter) {
        if (agentsCenterBean.isMasterNode()) {
            List<AgentsCenter> nodes1 = new ArrayList<>(agentsCenterBean.getRegisteredCenters());

            agentsCenterBean.sendAgentsCenters(agentsCenter, nodes1);
            agentsCenterBean.getRegisteredCenters().add(agentsCenter);

            return nodes1;

        } else {
            agentsCenterBean.getRegisteredCenters().add(agentsCenter);

            return null;

        }

    }


    @GET
    @Path("/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AgentsCenter> getNodes() {
        return agentsCenterBean.getRegisteredCenters();
    }
}
