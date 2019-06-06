package model;

import configuration.IAgentsCenterBean;
import messaging.IMessenger;
import websocket.ConsoleEndpoint;

import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public void init(AID aid, Map<String, String> args) {
        this.aid = aid;
        try {
            initArgs(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initArgs(Map<String, String> args) {

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

    protected List<AID> getAgents(String type) {

        List<AID> agents = null;

        try {

            agents = center.getRunningAgents();

            agents = agents.stream().filter(aid -> aid.getType().getName().equals(type)).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return agents;
    }

    protected void broadcastInfo(String text) {

        try {
            String fullText = aid.getName() + "[" + aid.getType().getName() + "]@" + aid.getHost().getAlias() + " : " + text;
            ws.sendMessage(fullText);
            center.broadcastMessage(fullText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isBusy() {
        return true;
    }

    protected abstract void onMessage(ACLMessage message);

}
