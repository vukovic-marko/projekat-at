package messaging;

import model.*;

import javax.ejb.Local;

@Local
public interface IMessenger {

    void sendMessage(ACLMessage message);

    void sendMessageToAgent(ACLMessage message, AID aid, int index);

}
