package controllers;

import configuration.IAgentsCenterBean;
import messaging.IMessenger;
import model.ACLMessage;
import model.Performative;
import mongodb.MongoDB;
import restclient.IRestClient;
import websocket.ConsoleEndpoint;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/messages")

public class MessagesController {

    @EJB
    private IAgentsCenterBean center;

    @EJB
    private IRestClient restClient;

    @EJB
    private IMessenger messenger;

    @EJB
    private ConsoleEndpoint ws;

    @EJB
    private MongoDB db;

    /**
     *
     * @param message Poruka od klijenta
     * @return Status kod
     */
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(ACLMessage message) {

        messenger.sendMessage(message);

        return Response.ok().build();
    }

    /**
     *
     * @return Lista performativa iz enumeracije sa pocetnim velikim slovom i status kod
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerformatives() {

        Performative[] performatives = Performative.class.getEnumConstants();

        List<String> performativesList = new ArrayList<>();

        for (Performative performative : performatives) {

            String perfString = performative.toString().replace("_", " ");

            String firstLetter = perfString.substring(0, 1).toUpperCase();

            perfString = firstLetter + perfString.substring(1).toLowerCase();

            performativesList.add(perfString);
        }

        return Response.ok(performativesList).build();
    }

    @PUT
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response websocketMessage(String message) {

        ws.sendMessage(message);

        return Response.status(Response.Status.OK).build();

    }

    @PUT
    @Path("/redirect/{delay}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response redirectedMessage(@PathParam("delay") Long delay, ACLMessage message) {

        messenger.activateHostAgents(message, delay);

        return Response.status(Response.Status.OK).build();

    }

}

































