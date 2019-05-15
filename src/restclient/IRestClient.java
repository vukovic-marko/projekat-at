package restclient;

import model.AID;
import model.AgentType;
import model.AgentsCenter;

import javax.ejb.Local;
import model.AgentI;

import java.util.List;

@Local
public interface IRestClient {

    void runRemoteAgent(AgentsCenter center, String type, String name);

    void notifyAgentStarted(AID aid, List<AgentsCenter> toNotify);

    void stopAgent(AID aid);

    void notifyAgentStopped(AID aid, List<AgentsCenter> toNotify);
}
