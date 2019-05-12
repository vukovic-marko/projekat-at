package controllers;

import configuration.AgentsCenterBean;
import configuration.IAgentsCenterBean;
import model.Agent;
import model.AgentType;

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/agents")
public class AgentsController {

    @EJB
    IAgentsCenterBean center;

    @GET
    @Path("/classes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAgentTypes() {

        List<AgentType> types = center.getTypes();

        return Response.ok(types).build();
    }

    @GET
    @Path("/running")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRunningAgents() {

        List<Agent> agents = new ArrayList<>();



        return Response.ok(agents).build();
    }

    @PUT
    @Path("/running/{type}/{name}")
    public Response runAgent(@PathParam("type") String type, @PathParam("name") String name) {



        return Response.ok().build();
    }

    @DELETE
    @Path("/running/{aid}")
    public Response stopAgent(@PathParam("aid") String aid) {



        return Response.ok().build();
    }

    @GET
    @Path("/jndi")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testjndi() {

        List<AgentType> ret = new ArrayList<>();

        try {
            ret = center.getAvaliableAgentTypes();
        } catch (NamingException e) {
            e.getMessage();
            return Response.serverError().build();
        }


        return Response.ok(ret).build();
    }

}
