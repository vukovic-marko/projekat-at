package controllers;

import configuration.IAgentsCenterBean;
import model.AgentsCenter;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import websocket.ConsoleEndpoint;
import websocket.MessageType;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/")
public class AgentsCenterController {

    @EJB
    private ConsoleEndpoint ws;

    @EJB
    private IAgentsCenterBean center;

    @GET
    @Path("/node")
    @Produces(MediaType.APPLICATION_JSON)
    public Response heartbeat() {
        return Response.ok().build();
    }

    @POST
    @Path("/node")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(AgentsCenter agentsCenter) {
        if (center.isMasterNode()) {
            List<AgentsCenter> nodes1 = new ArrayList<>(center.getRegisteredCenters());

            // Dodati sam master node
            nodes1.add(center.getAgentsCenter());

            center.sendAgentsCenters(agentsCenter, nodes1);

            if (center.getRegisteredCenters().add(agentsCenter)) {
                ws.sendMessage("New node '" + agentsCenter.getAlias()
                        + "@" + agentsCenter.getAddress() + "' joined the cluster");
            }

            return Response.ok(nodes1).build();

        } else {
            center.getRegisteredCenters().add(agentsCenter);

            ws.sendMessage("Added center '" + agentsCenter.getAlias() + "@" +
                    agentsCenter.getAddress() + "'");

            return Response.ok().build();

        }

    }

    @DELETE
    @Path("/node/{alias}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("alias") String alias, @HeaderParam("sender") String sender) {
        System.out.println("node with name '" + alias + "' to be deleted, discovered by: " + sender);
        for (AgentsCenter center : center.getRegisteredCenters()) {
            if (center.getAlias().equals(alias)) {

                if (this.center.isMasterNode()) {
                    ResteasyClient client = new ResteasyClientBuilder().build();
                    ResteasyWebTarget target;
                    Response response;

                    for (AgentsCenter c : this.center.getRegisteredCenters()) {
                        if (center.getAddress().equals(sender))
                            continue;
                        if (center.getAlias().equals(alias))
                            continue;

                        System.out.println("telling: " + center.getAddress() + " to delete: " + alias);

                        target = client.target(center.getAddress() + "/node/" + alias);
                        response = target.request(MediaType.APPLICATION_JSON).delete();
                        response.close();
                    }
                    client.close();
                }
                this.center.getRegisteredCenters().remove(center);
                this.center.getClusterTypesMap().remove(center.getAlias()+ "@" + center.getAddress());
                break;
            }
        }
        return Response.ok().build();
    }

    // Suvisno je jer ce svaki centar prepoznati da mu se ne javlja onaj centar koji je napustio klaster
    @DELETE
    @Path("center/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCenter(@PathParam("key") String centerKey) {

        String[] parts = centerKey.split("@");

        String alias = parts[0];
        String address = parts[1];

        AgentsCenter tempCenter = new AgentsCenter(address, alias);

        center.getRegisteredCenters().remove(tempCenter);

        center.getClusterTypesMap().remove(centerKey);

        center.setRunningAgents(center.getRunningAgents().stream()
                .filter(aid -> !aid.getHost().equals(tempCenter))
                .collect(Collectors.toList()));

        ws.sendMessage("Node '" + centerKey + "' left the cluster", MessageType.UPDATE_ALL);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNodes() {
        return Response.ok(center.getRegisteredCenters()).build();
    }
}
