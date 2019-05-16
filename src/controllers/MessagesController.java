package controllers;

import configuration.IAgentsCenterBean;
import messaging.IMessenger;
import model.ACLMessage;
import model.AID;
import model.AgentsCenter;
import restclient.IRestClient;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/messages")
public class MessagesController {

    @EJB
    IAgentsCenterBean center;

    @EJB
    IRestClient restClient;

    @EJB
    IMessenger messenger;

    /**
     *
     * @param message Poruka od klijenta
     * @return Status kod
     */
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(ACLMessage message) {

        AID[] ids = message.getReceivers();

        for (int i = 0; i < ids.length; i++) {
            AID aid = ids[i];
            AgentsCenter host = aid.getHost();
            if (host.equals(center.getAgentsCenter())) {
                messenger.sendMessageToAgent(message, aid, i);
            } else {
                restClient.sendMessageToCenter(message, host);
            }
        }

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

        ACLMessage.Performative[] performatives = ACLMessage.Performative.class.getEnumConstants();

        List<String> performativesList = new ArrayList<>();

        for (ACLMessage.Performative performative : performatives) {

            String perfString = performative.toString().replace("_", " ");

            String firstLetter = perfString.substring(0, 1).toUpperCase();

            perfString = firstLetter + perfString.substring(1).toLowerCase();

            performativesList.add(perfString);
        }

        return Response.ok(performativesList).build();
    }

}
