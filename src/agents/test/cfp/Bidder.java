package agents.test.cfp;

import model.ACLMessage;
import model.Agent;
import model.AgentI;
import model.Performative;

import javax.ejb.Remote;
import javax.ejb.Stateful;

@Stateful
@Remote(AgentI.class)
public class Bidder extends Agent {

    @Override
    protected void onMessage(ACLMessage message) {

        //System.out.println("Agent " + aid.getName() + " [Bidder] received a message!");
        broadcastInfo("Received message: " + message);
        if (message.getPerformative() == Performative.CFP) {

            double rand = Math.random();

            if (rand > 0.3) {
                ACLMessage reply = message.makeReply(Performative.PROPOSE);

                reply.setSender(aid);
                reply.getUserArgs().put("value", (int) (Math.random()*1000));

                messenger.sendMessage(reply);

                String wsMessage = "Agent " + aid.getName() + " [Bidder] sent proposal";
                ws.sendMessage(wsMessage);
                center.broadcastMessage(wsMessage);

            } else {

                String wsMessage = "Agent " + aid.getName() + " [Bidder] refused to propose";
                ws.sendMessage(wsMessage);
                center.broadcastMessage(wsMessage);

            }
        } else if (message.getPerformative() == Performative.ACCEPT_PROPOSAL) {

            String wsMessage = "Proposal of agent '" + aid.getName() + "@" + aid.getHost().getAlias() + "' [Bidder] accepted";
            ws.sendMessage(wsMessage);
            center.broadcastMessage(wsMessage);

            int result = doJob();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            wsMessage = "Agent '" + aid.getName() + "@" + aid.getHost().getAlias() +
                    "' [Bidder] finished job with result " + result;
            ws.sendMessage(wsMessage);
            center.broadcastMessage(wsMessage);

            if (message.getReplyTo() != null) {
                ACLMessage msg = new ACLMessage(Performative.INFORM);
                msg.setSender(aid);

                msg.addReceiver(message.getReplyTo());
                msg.addReceiver(message.getSender());
                msg.setContent(result + "");

                wsMessage = "Agent '" + aid.getName() + "@" + aid.getHost().getAlias() +
                        "' [Bidder] informing gatherer";
                ws.sendMessage(wsMessage);
                center.broadcastMessage(wsMessage);

                messenger.sendMessage(msg);
            } else {
                System.out.println("No gatherer to inform");
            }

        } else if (message.getPerformative() == Performative.REJECT_PROPOSAL) {
            String wsMessage = "Proposal of agent '" + aid.getName() + "@" + aid.getHost().getAlias() + "' [Bidder] rejected";
            ws.sendMessage(wsMessage);
            center.broadcastMessage(wsMessage);
        }

    }

    private int doJob() {
        return 42;
    }

}
