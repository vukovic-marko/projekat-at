package agents.test;

import model.ACLMessage;
import model.Agent;
import model.AgentI;
import model.Performative;

import javax.ejb.Remote;
import javax.ejb.Stateful;

@Stateful
@Remote(AgentI.class)
public class Pong extends Agent {

    private int counter = 0;

    @Override
    public void handleMessage(ACLMessage message) {

        System.out.println("Hi from Pong!");

        if(message.getPerformative() == Performative.REQUEST) {
            ACLMessage reply = message.makeReply(Performative.INFORM);
            reply.setSender(aid);
            reply.getUserArgs().put("pongCreatedOn", aid.getHost());
            reply.getUserArgs().put("pongWorkingOn", aid.getHost());
            reply.getUserArgs().put("pongCounter", ++counter);
            messenger.sendMessage(reply);
        }

    }
}
