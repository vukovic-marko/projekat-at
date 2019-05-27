package restclient;

import model.ACLMessage;
import model.AID;
import model.AgentsCenter;

import javax.ejb.Local;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Local
public interface IRestClient {

    Response runRemoteAgent(String address, String type, String name);

    void notifyAgentStarted(AID aid, Set<AgentsCenter> toNotify);

    void stopAgent(AID aid);

    void notifyAgentStopped(List<AID> ids, Set<AgentsCenter> toNotify);

    void sendMessageToCenter(ACLMessage message, AgentsCenter center);

    void broadcastMessage(String wsMessage, Set<AgentsCenter> registeredCenters);

    void notifyCenterLeft(String key, Set<AgentsCenter> registeredCenters);
}
