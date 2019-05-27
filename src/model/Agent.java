package model;

import configuration.IAgentsCenterBean;
import messaging.IMessenger;
import websocket.ConsoleEndpoint;

import javax.ejb.Remove;

import javax.ejb.EJB;
import javax.ejb.Stateful;

@Stateful
public abstract class Agent implements AgentI {

    protected AID aid;

    @EJB
    protected IMessenger messenger;

    @EJB
    protected IAgentsCenterBean center;

    @EJB
    protected ConsoleEndpoint ws;

    @Override
    public AID getAid() {
        return aid;
    }

    @Override
    public void init(AID aid) {
        this.aid = aid;
    }

    // Znacajno jer se stateful bean brise ako neka njgova metoda baci izuzetak
    @Override
    public void handleMessage(ACLMessage message) {
        try {
            onMessage(message);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    @Remove
    public void stop() {
        try {
            onTerminate();
            System.out.println("Agent destroyed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onTerminate() {

    }

    protected abstract void onMessage(ACLMessage message);

}
