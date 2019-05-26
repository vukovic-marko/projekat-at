package controllers;

import configuration.IAgentsCenterBean;
import model.AID;
import model.AgentI;
import model.AgentType;
import model.AgentsCenter;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import restclient.IRestClient;
import websocket.ConsoleEndpoint;
import websocket.MessageType;

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/agents")
public class AgentsController {

    @EJB
    IAgentsCenterBean center;

    @EJB
    IRestClient restClient;

    @EJB
    private ConsoleEndpoint ws;

    @GET
    @Path("/classes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAgentTypes() {

        Set<AgentType> types = center.getAllTypes();

        return Response.ok(types).build();
    }

    @POST
    @Path("/classes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setAgentTypes(Map<String, List<AgentType>> types) {
        if (center.isMasterNode()) {
            String sender = "";
            types.forEach((k,v) -> center.getClusterTypesMap().put(k,v));

            ws.sendMessage("Received agent types from slave node", MessageType.UPDATE_TYPES);

            for (String key : types.keySet()) {
                sender = key;
            }

            String[] parts = sender.split("@");

            Map<String, List<AgentType>> temp = center.getClusterTypesMap();

            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target;
            Response response;
            for (AgentsCenter c : center.getRegisteredCenters()) {
                if (c.getAddress().equals(parts[1]) && c.getAlias().equals(parts[0]))
                    continue;
                target = client.target(c.getAddress() + "/agents/classes");
                response = target.request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(temp, MediaType.APPLICATION_JSON));
                response.close();
            }
            client.close();

            return Response.ok(temp).build();
        } else {
            center.setClusterTypesMap(types);

            return Response.ok(null).build();
        }
    }

    @GET
    @Path("/classes/cluster")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClusterTypes() {
        return Response.ok(center.getClusterTypesMap()).build();
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

        running.forEach(aid -> ws.agentStarted(aid.getName(), aid.getType().getName(), aid.getHost().getAlias()));

        return Response.ok().build();
    }

    @PUT
    @Path("/running/{type}/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response runAgent(@PathParam("type") String type, @PathParam("name") String name) {

        Map<String, List<AgentType>> clusterTypesMap = center.getClusterTypesMap();

        AgentType agentType = new AgentType(type);

        List<AgentType> hostTypes = center.getHostTypes();

        String originCenterAddress= null;
        AgentI agent = null;

        if (hostTypes.contains(agentType)) {
            try {

                agent = center.runAgent(agentType, name);

                if (agent == null) {
                    return Response.status(Response.Status.CONFLICT).entity("Agent with same AID already exists").build();
                }

                // Nadji centre koji nisu host agenta
                Set<AgentsCenter> toNotify = center.getRegisteredCenters();

                restClient.notifyAgentStarted(agent.getAid(), toNotify);

            } catch (NamingException e) {
                e.getMessage();
                return Response.serverError().build();
            }
        } else {
            // Trazeni tip agenta ne postoji na hostu

            for (Map.Entry<String, List<AgentType>> pair : clusterTypesMap.entrySet()) {
                if (pair.getValue().contains(agentType)) {
                    originCenterAddress = (pair.getKey().split("@"))[1].trim();
                    break;
                }
            }

            if (originCenterAddress == null) {
                return Response.serverError().build();
            }

            Response response = restClient.runRemoteAgent(originCenterAddress, type, name);

            if (response.getStatus() == 409) {
                return Response.status(Response.Status.CONFLICT).entity("Agent with same AID already exists").build();
            }

            // return Response.status(Response.Status.OK).build();

            // agent = response.readEntity(AgentI.class);

        }

        return Response.status(Response.Status.OK).entity(agent).build();
    }

    @DELETE
    // @Path("/running/{aid}")
    // public Response stopAgent(@PathParam("aid") String aid) {
    @Path("/running")
    public Response stopHostAgent(AID aid) {

        // Da li se zastavlja agent na hostu
        if (aid.getHost().equals(center.getAgentsCenter())) {
            boolean deleted = center.stopHostAgent(aid);
            if (!deleted) {
                // Ne postoji trazeni agent
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            restClient.notifyAgentStopped(Arrays.asList(aid), center.getRegisteredCenters());
        } else {
            restClient.stopAgent(aid);
        }

        return Response.ok(aid).build();
    }

    @DELETE
    @Path("/running/{aid}")
    public Response stopAgent(@PathParam("aid") String aidStr) {

        String[] parts = aidStr.split("\\.");

        String aidName = parts[0];
        String typeName = parts[1];
        String moduleName = parts[2];
        moduleName += "." + parts[3];

        AID aid = new AID(aidName, typeName, moduleName, center.getAgentsCenter());

        boolean deleted = center.stopHostAgent(aid);
        if (!deleted) {
            // Ne postoji trazeni agent
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        restClient.notifyAgentStopped(Arrays.asList(aid), center.getRegisteredCenters());

        return Response.ok().build();
    }

    @POST
    @Path("/stopped")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response stoppedAgents (List<AID> stopped){

        center.getRunningAgents().removeAll(stopped);

        stopped.forEach(aid -> ws.agentStopped(aid.getName(), aid.getType().getName(), aid.getHost().getAlias()));

        return Response.ok().build();
    }


}
