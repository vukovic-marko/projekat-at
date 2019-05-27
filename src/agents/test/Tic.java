package agents.test;

import model.*;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.List;

@Stateful
@Remote(AgentI.class)
public class Tic extends Agent {

    @Override
    protected void onMessage(ACLMessage message) {

        broadcastInfo("Agent " + aid + " [Tic] received a message");

        if (!message.getLanguage().equals("ttt")) {
            broadcastInfo("I don't speak your language");
            return;
        }

        if (message.getPerformative() == Performative.REQUEST) {

            ACLMessage msg = new ACLMessage(Performative.PROPAGATE);
            msg.setLanguage("ttt");
            msg.setSender(aid);

            // Find remote Tac

            List<AID> agents = getAgents("Tac");

            AID rec = null;
            for (AID aid : agents) {
                if (!aid.getHost().equals(this.aid.getHost())) {
                    rec = aid;
                    break;
                }
            }

            if (rec==null) {
                broadcastInfo("No remote Tac agents found");
                return;
            }

            msg.addReceiver(rec);

            messenger.sendMessage(msg);

        } else if (message.getPerformative() == Performative.INFORM && message.getLanguage().equals("ttt")) {

            broadcastInfo("Informed with : " + message.getContentObj());

        }
    }
}
