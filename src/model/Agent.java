package model;

import javax.ejb.Stateful;

@Stateful
public abstract class Agent implements AgentI {

    public AID id;

    public AID getId() {
        return id;
    }

    public void setId(AID id) {
        this.id = id;
    }
}
