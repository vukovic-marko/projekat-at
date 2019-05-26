package messaging;

import configuration.IAgentsCenterBean;
import model.ACLMessage;
import model.AID;
import model.AgentsCenter;
import restclient.IRestClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.HashSet;
import java.util.Set;

@Stateless
public class Messenger implements IMessenger {

    @EJB
    private MessagingFactory factory;

    private Session session;

    private MessageProducer producer;

    @EJB
    private IAgentsCenterBean center;

    @EJB
    private IRestClient restClient;

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
    public void sendMessage(ACLMessage message) {

        AID[] ids = message.getReceivers();

        if (ids == null || ids.length==0) {
            return;
        }

        Set<AgentsCenter> remoteDestinations = new HashSet<>();

        for (int i = 0; i < ids.length; i++) {
            AID aid = ids[i];
            AgentsCenter host = aid.getHost();
            if (host.equals(center.getAgentsCenter())) {
                sendMessageToAgent(message, aid, i);
            } else {
                remoteDestinations.add(host);
            }
        }

        remoteDestinations.forEach(host -> restClient.sendMessageToCenter(message, host));
    }

    @Override
    public void sendMessageToAgent(ACLMessage message, AID aid, int index) {

        ObjectMessage jmsMsg = null;

        try {
            jmsMsg = session.createObjectMessage(message);

            // Setup
            jmsMsg.setStringProperty("GroupID", aid.getName() + "@" + aid.getHost().getAlias());
            jmsMsg.setIntProperty("Index", index);
            // jmsMsg.setStringProperty("_HQ_DUPL_ID", UUID.randomUUID().toString());

            // Slanje
            producer.send(jmsMsg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
