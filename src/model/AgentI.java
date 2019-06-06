package model;

import javax.ejb.Remove;
import java.io.Serializable;
import java.util.Map;

public interface AgentI extends Serializable {

    void handleMessage(ACLMessage message);

    void init(AID aid);

    void init(AID aid, Map<String, String> args);

    boolean isBusy();

    @Remove
    void stop();

    AID getAid();

}
