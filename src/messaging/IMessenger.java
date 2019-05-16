package messaging;

import model.*;

import javax.ejb.Local;

@Local
public interface IMessenger {

    void sendMessageToAgent(ACLMessage message, AID aid, int index);

}
