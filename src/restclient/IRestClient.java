package restclient;

import model.*;

import javax.ejb.Local;

import java.util.List;

@Local
public interface IRestClient {

    void runRemoteAgent(String address, String type, String name);

    void notifyAgentStarted(AID aid, List<AgentsCenter> toNotify);

    void stopAgent(AID aid);

    void notifyAgentStopped(AID aid, List<AgentsCenter> toNotify);

    void sendMessageToCenter(ACLMessage message, AgentsCenter center);
}
