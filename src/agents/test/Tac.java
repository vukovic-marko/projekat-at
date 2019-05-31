package agents.test;

import model.*;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.List;

@Stateful
@Remote(AgentI.class)
public class Tac extends Agent {

    @Override
    protected void onMessage(ACLMessage message) {

        //broadcastInfo("Agent " + aid + " [Tac] received a message");
        broadcastInfo("Received message: " + message);

        if (!message.getLanguage().equals("ttt")) {
            broadcastInfo("I don't speak your language");
            return;
        }

        if (message.getPerformative() == Performative.PROPAGATE) {

            ACLMessage msg = new ACLMessage(Performative.REQUEST);
            msg.setLanguage("ttt");
            msg.setSender(message.getSender());
            msg.setReplyTo(message.getSender());

            List<AID> agents = getAgents("Toe");

            if (agents==null || agents.size()==0) {
                broadcastInfo("No Toe agents found");
                return;
            }

            msg.addReceivers(agents);

            messenger.sendMessage(msg);

        }

    }
}
