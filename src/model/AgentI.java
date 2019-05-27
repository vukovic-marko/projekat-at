package model;

import java.io.Serializable;

public interface AgentI extends Serializable {

    void handleMessage(ACLMessage message);

    void init(AID aid);

    void stop();

    AID getAid();

}
