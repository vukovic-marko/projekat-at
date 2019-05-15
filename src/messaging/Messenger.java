package messaging;

import model.Agent;
import model.AgentType;
import model.AgentsCenter;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.soap.SOAPConnectionFactory;

@Stateless
public class Messenger implements IMessenger {

    @Override
    public boolean runAgent(AgentsCenter center, AgentType type, String name) {

        /*Context context = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
        final Topic topic = (Topic) context.lookup("jms/topic/run");
        context.close();

        Connection con = null;
        con = cf.createConnection("guest", "guestguest");
        final Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        con.start();

        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(this);
        TextMessage msg = session.createTextMessage();
        msg.setText("Hello!");

        MessageProducer producer = session.createProducer(topic);
        producer.send(msg);
        producer.close();
        consumer.close();
        con.stop();*/

        return  true;
    }

    @Override
    public boolean sendMessage(AgentsCenter center, Agent agent) {
        return false;
    }

}
