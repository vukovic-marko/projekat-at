package agents.test;

import model.ACLMessage;
import model.Agent;
import model.AgentI;
import model.Performative;

import javax.ejb.Remote;
import javax.ejb.Stateful;

@Stateful
@Remote(AgentI.class)
public class Toe extends Agent {

    @Override
    protected void onMessage(ACLMessage message) {

        broadcastInfo("Agent " + aid + " [Toe] received a message");

        if (!message.getLanguage().equals("ttt")) {
            broadcastInfo("I don't speak your language");
            return;
        }

        if (message.getPerformative() == Performative.REQUEST) {

            ACLMessage msg = message.makeReply(Performative.INFORM);

            msg.setContentObj(new String("Hello from " + aid.getName()));

            messenger.sendMessage(msg);

        }

    }

}
