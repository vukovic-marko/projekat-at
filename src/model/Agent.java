package model;

import messaging.IMessenger;

import javax.ejb.EJB;
import javax.ejb.Stateful;

@Stateful
public abstract class Agent implements AgentI {

    protected AID aid;

    @EJB
    protected IMessenger messenger;

    @Override
    public AID getAid() {
        return aid;
    }

    @Override
    public void init(AID aid) {
        this.aid = aid;
    }

}
