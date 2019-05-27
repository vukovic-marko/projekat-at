package messaging;

import model.*;

import javax.ejb.Local;

@Local
public interface IMessenger {

    void sendMessage(ACLMessage message);
    void sendMessage(ACLMessage message, long delay);

    void sendMessageToAgent(ACLMessage message, AID aid, int index, long delay);

    void activateHostAgents(ACLMessage message, Long delay);
}
