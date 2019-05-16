package messaging;

import model.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.UUID;

@Stateless
public class Messenger implements IMessenger {

    @EJB
    private MessagingFactory factory;

    private Session session;

    private MessageProducer producer;

    @PostConstruct
    public void init() {
        try {
            session = factory.getSession();
            producer = factory.getProducer(session);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageToAgent(ACLMessage message, AID aid, int index) {

        ObjectMessage jmsMsg = null;

        try {
            jmsMsg = session.createObjectMessage(message);

            // Setup
            jmsMsg.setStringProperty("GroupID", aid.getName() + "@" + aid.getHost().getAlias());
            jmsMsg.setIntProperty("Index", index);
            jmsMsg.setStringProperty("_HQ_DUPL_ID", UUID.randomUUID().toString());

            // Slanje
            producer.send(jmsMsg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
