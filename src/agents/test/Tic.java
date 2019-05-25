package agents.test;

import model.ACLMessage;
import model.Agent;
import model.AgentI;

import javax.ejb.Remote;
import javax.ejb.Stateful;

@Stateful
@Remote(AgentI.class)
public class Tic extends Agent {

    @Override
    public void handleMessage(ACLMessage message) {

        System.out.println("I'm Tic!");
    }
}
