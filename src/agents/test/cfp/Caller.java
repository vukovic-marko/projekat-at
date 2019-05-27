package agents.test.cfp;

import model.*;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateful
@Remote(AgentI.class)
public class Caller extends Agent {

    private boolean licitationInProgress = false;
    private Map<AID, Integer> proposals;

    @Override
    protected void onMessage(ACLMessage message) {

        System.out.println("Agent " + aid.getName() + " [Caller] received a message!");
        //if (message.getPerformative() == Performative.CFP && message.getContent().trim().toLowerCase().equals("start")) {
        if (message.getPerformative() == Performative.CFP) {

            List<AID> agents = center.getRunningAgents();

            agents = agents.stream().filter(aid -> aid.getType().getName().equals("Bidder")).collect(Collectors.toList());

            ACLMessage msg = new ACLMessage(Performative.CFP);
            msg.setSender(aid);

            msg.addReceivers(agents);

            licitationInProgress = true;

            proposals = new HashMap<>();

            String wsMessage = "Agent " + aid.getName() + " [Caller] started licitation";
            ws.sendMessage(wsMessage);
            center.broadcastMessage(wsMessage);

            messenger.sendMessage(msg);

            ACLMessage selfMsg = new ACLMessage(Performative.INFORM);
            selfMsg.setSender(aid);

            selfMsg.addReceiver(aid);

            messenger.sendMessage(selfMsg, 5000);

        } else if (message.getPerformative() == Performative.INFORM) {

            licitationInProgress = false;

            String wsMessage = "Agent " + aid.getName() + " [Caller] ended licitation";
            ws.sendMessage(wsMessage);
            center.broadcastMessage(wsMessage);

            if (proposals.size()==0) {
                wsMessage = "Agent " + aid.getName() + " [Caller] got no proposals";
                ws.sendMessage(wsMessage);
                center.broadcastMessage(wsMessage);
                return;
            }

            Integer max = proposals.values().stream().max(Integer::compare).get();

            List<AID> rejected = new ArrayList<>();
            for (Map.Entry entry : proposals.entrySet()) {
                AID id = (AID)entry.getKey();
                if (entry.getValue().equals(max)) {

                    ACLMessage msg = new ACLMessage(Performative.ACCEPT_PROPOSAL);
                    msg.setSender(aid);

                    msg.addReceiver(id);

                    List<AID> agents = center.getRunningAgents();

                    for (AID i : agents) {
                        if (i.getType().getName().equals("ResultGatherer")) {
                            msg.setReplyTo(i);
                            System.out.println("Found result gatherer");
                            break;
                        }
                    }

                    wsMessage = "Agent " + aid.getName() + " [Caller] chose proposal from agent '"
                            + id.getName() + "@" + id.getHost().getAlias() + "'";
                    ws.sendMessage(wsMessage);
                    center.broadcastMessage(wsMessage);

                    messenger.sendMessage(msg);

                } else {
                    rejected.add(id);
                }
            }

            ACLMessage msg = new ACLMessage(Performative.REJECT_PROPOSAL);
            msg.setSender(aid);

            msg.addReceivers(rejected);

            messenger.sendMessage(msg);

        } else if (message.getPerformative() == Performative.PROPOSE && licitationInProgress) {

            String wsMessage = "Agent " + aid.getName() + " [Caller] got proposal from '"
                    + message.getSender().getName() + "@" + message.getSender().getHost().getAlias()
                    + "' with value " + message.getUserArgs().get("value");
            ws.sendMessage(wsMessage);
            center.broadcastMessage(wsMessage);

            proposals.put(message.getSender(), Integer.parseInt(message.getUserArgs().get("value").toString()));

        }

    }

}
