package messaging;

import application.App;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.jms.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Singleton
public class MessagingFactory {

    private Connection connection;

    @Resource(lookup = "java:jboss/exported/jms/connFactory")
    private ConnectionFactory connFactory;

    @Resource(lookup = "java:jboss/exported/jms/queue")
    private Queue queue;

    @PostConstruct
    public void init() {

        InputStream configFile = App.class.getClassLoader().getResourceAsStream("config.properties");

        Properties props = new Properties();

        try {
            props.load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String username = props.getProperty("username");
        String password = props.getProperty("password");

        try {
            connection = connFactory.createConnection(username, password);
            connection.setClientID("guest");
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public Session getSession() throws JMSException {
        try {
            return connection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new JMSException(e.getMessage());
        }
    }

    public MessageProducer getProducer(Session session) throws JMSException {
        try {
            return session.createProducer(queue);
        } catch (JMSException e) {
            throw new JMSException(e.getMessage());
        }
    }


}
