package agents.test.cfp;

import model.*;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.ArrayList;
import java.util.List;

@Stateful
@Remote(AgentI.class)
public class ResultGatherer extends Agent {

    private List<Integer> results = new ArrayList<>();

    @Override
    protected void onMessage(ACLMessage message) {

        System.out.println("Agent " + aid.getName() + " [ResultGatherer] received a message!");
        if (message.getPerformative() == Performative.INFORM) {

            Integer result = Integer.parseInt(message.getContent());
            AID id = message.getSender();

            results.add(result);

            String wsMessage = "Agent '" + aid.getName() + "@" + aid.getHost().getAlias()
                    + " [ResultGatherer] got result " + result + " from agent '"
                    + id.getName() + "@" + id.getHost().getAlias() + "'";
            ws.sendMessage(wsMessage);
            center.broadcastMessage(wsMessage);

        } else if (message.getPerformative() == Performative.REQUEST) {

            // Do something

        }

    }
}
