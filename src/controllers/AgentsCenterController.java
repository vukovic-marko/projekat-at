package controllers;

import configuration.IAgentsCenterBean;
import model.AgentsCenter;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import restclient.IRestClient;
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

    @EJB
    private IRestClient restClient;

    @GET
    @Path("/node")
    @Produces(MediaType.APPLICATION_JSON)
    public Response heartbeat() {
        return Response.ok().build();
    }

    @POST
    @Path("/node")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(AgentsCenter agentsCenter) {
        if (center.isMasterNode()) {
            List<AgentsCenter> nodes1 = new ArrayList<>(center.getRegisteredCenters());

            for (AgentsCenter node : nodes1) {
                if (node.getAlias().equals(agentsCenter.getAlias())) {
                    System.out.println("AgentsCenter with the same name already registered!!");

                    return Response.status(Response.Status.BAD_REQUEST).entity(null).build();
                }
            }

            // Dodati sam master node
            nodes1.add(center.getAgentsCenter());

            center.sendAgentsCenters(agentsCenter, nodes1);

            if (center.getRegisteredCenters().add(agentsCenter)) {
                ws.sendMessage("New node '" + agentsCenter.getAlias()
                        + "@" + agentsCenter.getAddress() + "' joined the cluster");
            }

            return Response.status(Response.Status.OK).entity(nodes1).build();

        } else {
            center.getRegisteredCenters().add(agentsCenter);

            ws.sendMessage("Added center '" + agentsCenter.getAlias() + "@" +
                    agentsCenter.getAddress() + "'");

            return Response.status(Response.Status.OK).entity(null).build();

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
    @Path("center/{alias}")
    public Response removeCenter(@PathParam("alias") String alias) {

        AgentsCenter tempCenter = center.deleteByAlias(alias);

        String centerKey = tempCenter.getAlias() + "@" + tempCenter.getAddress();

        center.getClusterTypesMap().remove(centerKey);

        center.setRunningAgents(center.getRunningAgents().stream()
                .filter(aid -> !aid.getHost().equals(tempCenter))
                .collect(Collectors.toList()));

        ws.sendMessage("Node '" + center.getAgentsCenter().getAlias() +
                "@" + center.getAgentsCenter().getAddress() + "' removed '" + centerKey + "'",
                MessageType.UPDATE_ALL);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNodes() {
        return Response.ok(center.getRegisteredCenters()).build();
    }

    @DELETE
    @Path("")
    public void stopApp() {

        ws.sendMessage("Node " + center.getAgentsCenter().getAlias() + "@" +
                center.getAgentsCenter().getAddress() + " shutting down in 5 seconds");
        restClient.broadcastMessage("Node " + center.getAgentsCenter().getAlias() + "@" +
                center.getAgentsCenter().getAddress() + " shutting down in 5 seconds", center.getRegisteredCenters());

        restClient.centerShuttingDown(center.getAgentsCenter(), center.getRegisteredCenters());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ws.sendMessage("Node " + center.getAgentsCenter().getAlias() + "@" +
                center.getAgentsCenter().getAddress() + " left the cluster");
        restClient.broadcastMessage("Node " + center.getAgentsCenter().getAlias() + "@" +
                center.getAgentsCenter().getAddress() + " left the cluster", center.getRegisteredCenters());

        System.exit(0);

    }

    /*
    @POST
    @Path("/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response form(FilterDTO filter) {

        center.notifyAggregator(filter);

        center.waitResults();

        return Response.status(Response.Status.OK).entity(filter).build();
    }*/

}
