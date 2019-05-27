package agents.test;

import model.ACLMessage;
import model.Agent;
import model.AgentI;

import javax.ejb.Remote;
import javax.ejb.Stateful;

@Stateful
@Remote(AgentI.class)
public class Phantom extends Agent {

    @Override
    protected void onMessage(ACLMessage message) {

        System.out.println("Hello from the shadows");

    }
}
