package agents;

import model.ACLMessage;
import model.Agent;
import model.AgentI;

import javax.ejb.LocalHome;
import javax.ejb.Remote;
import javax.ejb.RemoteHome;
import javax.ejb.Stateful;

@Stateful
@Remote(AgentI.class)
public class Pong extends Agent {

    @Override
    public void handleMessage(ACLMessage message) {

    }
}
