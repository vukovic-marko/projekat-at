package controllers;

import configuration.AgentsCenterBean;
import model.ACLMessage;
import model.AgentsCenter;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Path("/messages")
public class MessagesController {

    @EJB
    AgentsCenterBean center;

    /**
     *
     * @param message Poruka od klijenta
     * @return Status kod
     */
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(ACLMessage message) {

        center.sendMessage(message);

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
