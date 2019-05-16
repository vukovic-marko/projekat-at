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

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/agents")
public class AgentsController {

    @EJB
    IAgentsCenterBean center;

    @EJB
    IRestClient restClient;

    @GET
    @Path("/classes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAgentTypes() {

        List<AgentType> types = center.getTypes();

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

    // TODO : Prodiskutvati sa asistentom
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
            restClient.notifyAgentStopped(aid, center.getRegisteredCenters());
        } else {
            restClient.stopAgent(aid);
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("/running/{aid}")
    public Response stopAgent(@PathParam("aid") String aid) {

        String[] parts = aid.split("\\.");

        String aidName = parts[0];
        String typeName = parts[1];

        center.stopAgent(aidName, typeName);

        return Response.ok().build();
    }

}
