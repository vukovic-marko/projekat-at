package agents.test;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentI;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Stateful
@Remote(AgentI.class)
public class Ping extends Agent {

    @Override
    public void handleMessage(ACLMessage message) {

        System.out.println("Hi from Ping!");
        if (message.getPerformative() == ACLMessage.Performative.REQUEST) {

            AID pongAid = new AID(message.getContent(), null, null);

            ACLMessage msgToPong = new ACLMessage(ACLMessage.Performative.REQUEST);
            msgToPong.setSender(aid);
            msgToPong.addReceiver(pongAid);

            messenger.sendMessage(msgToPong);

        } else if (message.getPerformative() == ACLMessage.Performative.INFORM) {

            Map<String, Object> args = new HashMap<>(message.getUserArgs());
            args.put("pingCreatedOn", aid.getHost());
            args.put("pingWorkingOn", aid.getHost());
        }

    }

}
