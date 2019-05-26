package agents.test.pingpong;

import model.*;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateful
@Remote(AgentI.class)
public class Ping extends Agent {

    @Override
    protected void onMessage(ACLMessage message) {

        System.out.println("Agent " + aid.getName() + " [Ping] received a message!");
        if (message.getPerformative() == Performative.REQUEST) {

            // Find all pongs

            List<AID> agents = center.getRunningAgents();

            agents = agents.stream().filter(aid -> aid.getType().getName().equals("Pong")).collect(Collectors.toList());

            AID pongAid = new AID(message.getContent(), null, null);

            ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
            msgToPong.setSender(aid);

            msgToPong.addReceivers(agents);

            messenger.sendMessage(msgToPong);

        } else if (message.getPerformative() == Performative.INFORM) {

            Map<String, Object> args = new HashMap<>(message.getUserArgs());
            args.put("pingCreatedOn", aid.getHost());
            args.put("pingWorkingOn", aid.getHost());

            Object counter = message.getUserArgs().get("pongCounter");
            Integer pongCounter;
            if (counter != null) {
               pongCounter = Integer.parseInt(counter.toString());
               String wsMessage = "Pong counter for " + message.getSender().getName() + "@" +
                       message.getSender().getHost().getAlias() + " is " + pongCounter;
               ws.sendMessage(wsMessage);
               center.broadcastMessage(wsMessage);
            }
        }

    }

}
