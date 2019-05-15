package model;

import javax.ejb.Stateful;

@Stateful
public abstract class Agent implements AgentI {

    public AID aid;

    @Override
    public AID getAid() {
        return aid;
    }

    @Override
    public void init(AID aid) {
        this.aid = aid;
    }

}
