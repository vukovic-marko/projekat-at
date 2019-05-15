package controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import configuration.AgentsCenterBean;
import configuration.IAgentsCenterBean;
import messaging.IMessenger;
import model.*;
import restclient.IRestClient;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/agents")
public class AgentsController {

    @EJB
    IAgentsCenterBean center;

    @EJB
    IMessenger messenger;

    @EJB
    IRestClient restClient;

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

        List<AID> agents = center.getRunningAgents();

        return Response.ok(agents).build();
    }

    @POST
    @Path("/running")
    @Produces(MediaType.APPLICATION_JSON)
    public Response agentsRunning(List<AID> running) {

        center.addRunningAgents(running);

        return Response.ok().build();
    }

    @PUT
    @Path("/running/{type}/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response runAgent(@PathParam("type") String type, @PathParam("name") String name) {

        Map<AgentType, AgentsCenter> typesMap = center.getTypesMap();

        AgentType agentType = new AgentType(type);

        AgentsCenter originCenter = typesMap.get(agentType);

        for (AgentType at : typesMap.keySet()) {
            if (at.equals(agentType)) {
                originCenter = typesMap.get(at);
                break;
            }
        }

        if (originCenter==null) {
            return Response.serverError().build();
        }

        AgentI agent = null;

        if (originCenter.equals(center.getAgentsCenter())) {
            try {
                agent = center.runAgent(agentType, name);
                // Nadji centre koje nisu host agenta
                List<AgentsCenter> toNotify = center.getRegisteredCenters();

                restClient.notifyAgentStarted(agent.getAid(), toNotify);

            } catch (NamingException e) {
                e.getMessage();
                return Response.serverError().build();
            }
        } else {
            restClient.runRemoteAgent(originCenter, type, name);
        }

        return Response.ok(agent).build();
    }

    @DELETE
    @Path("/running/{aid}")
    public Response stopAgent(@PathParam("aid") String aid) {


        return Response.ok().build();
    }

}
