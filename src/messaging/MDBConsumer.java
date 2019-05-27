package messaging;

import configuration.IAgentsCenterBean;
import model.ACLMessage;
import model.AID;
import model.AgentI;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/red")
        }
)
public class MDBConsumer implements MessageListener {

    @EJB
    private IAgentsCenterBean center;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objMessage = (ObjectMessage) message;
        ACLMessage receivedMessage = null;

        AID aid = null;
        try {
            receivedMessage = (ACLMessage) objMessage.getObject();
            int i = message.getIntProperty("Index");
            AID[] receivers  = receivedMessage.getReceivers();
            aid = receivers[i];

        } catch (JMSException e) {
            e.printStackTrace();
        }

        AgentI agent = center.getHostRunningAgents().get(aid);

        if (agent == null) {
            return;
        }

        agent.handleMessage(receivedMessage);

        /*try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

    }
}
