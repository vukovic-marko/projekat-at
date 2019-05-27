package messaging;

import configuration.IAgentsCenterBean;
import model.ACLMessage;
import model.AID;
import model.AgentsCenter;
import restclient.IRestClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
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

    @Resource
    private TimerService timerService;

    private AgentsCenter agentsCenter;

    @PostConstruct
    public void init() {
        try {
            session = factory.getSession();
            producer = factory.getProducer(session);
            agentsCenter = center.getAgentsCenter();
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
    public void sendMessage(ACLMessage message, long delay) {

        AID[] ids = message.getReceivers();

        if (ids == null || ids.length==0) {
            return;
        }

        Set<AgentsCenter> remoteDestinations = new HashSet<>();

        for (int i = 0; i < ids.length; i++) {
            AID aid = ids[i];
            AgentsCenter host = aid.getHost();
            if (host.equals(agentsCenter)) {
                sendMessageToAgent(message, aid, i, delay);
            } else {
                remoteDestinations.add(host);
            }
        }

        remoteDestinations.forEach(host -> restClient.sendMessageToCenter(message, host, delay));

    }

    @Override
    public void sendMessage(ACLMessage message) {
        sendMessage(message, 0L);
    }

    @Override
    public void sendMessageToAgent(ACLMessage message, AID aid, int index, long delay) {

        ObjectMessage jmsMsg = null;

        try {
            jmsMsg = session.createObjectMessage(message);

            // Setup
            jmsMsg.setStringProperty("GroupID", aid.getName() + "@" + aid.getHost().getAlias());
            jmsMsg.setIntProperty("Index", index);
            // jmsMsg.setStringProperty("_HQ_DUPL_ID", UUID.randomUUID().toString());

            // Slanje
            if (delay == 0) {
                producer.send(jmsMsg);
            } else {
                timerService.createSingleActionTimer(delay, new TimerConfig(new JMSWrapper(jmsMsg), false));
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activateHostAgents(ACLMessage message, Long delay) {

        AID[] ids = message.getReceivers();

        if (ids == null || ids.length==0) {
            return;
        }

        Set<AgentsCenter> remoteDestinations = new HashSet<>();

        for (int i = 0; i < ids.length; i++) {
            AID aid = ids[i];
            AgentsCenter host = aid.getHost();
            if (host.equals(agentsCenter)) {
                sendMessageToAgent(message, aid, i, delay);
            }
        }

    }

    @Timeout
    public void timeout(Timer timer) {

        try {
            JMSWrapper wrapper = (JMSWrapper) timer.getInfo();
            ACLMessage message = wrapper.getMessage();
            ObjectMessage jmsMsg = session.createObjectMessage(message);
            jmsMsg.setStringProperty("GroupID", wrapper.getGroupId());
            jmsMsg.setIntProperty("Index", wrapper.getIndex());
            producer.send(jmsMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
