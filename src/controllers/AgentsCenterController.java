package controllers;

import configuration.AgentsCenterBean;
import model.AgentsCenter;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class AgentsCenterController {

    @EJB
    private AgentsCenterBean agentsCenterBean;

    @POST
    @Path("/node")
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(AgentsCenter agentsCenter) {
        System.out.println("kontaktirao me je :" + agentsCenter.getAddress());
        agentsCenterBean.getRegisteredCenters().add(agentsCenter);

        System.out.println("Registrovani agentski centri:");
        agentsCenterBean.getRegisteredCenters().forEach(center-> System.out.println(center.getAddress()));
        System.out.println("----------------------------");
    }
}
