package restclient;

import model.AgentType;
import model.AgentsCenter;

import javax.ejb.Local;
import model.AgentI;

import java.util.List;

@Local
public interface IRestClient {

    void runRemoteAgent(AgentsCenter center, String type, String name);

    void notifyAgentStarted(AgentI agent, List<AgentsCenter> toNotify);
}
