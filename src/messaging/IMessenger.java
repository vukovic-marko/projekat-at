package messaging;

import model.Agent;
import model.AgentType;
import model.AgentsCenter;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.jms.JMSException;
import javax.naming.NamingException;

@Local
public interface IMessenger {

    boolean runAgent(AgentsCenter center, AgentType type, String name);
    boolean sendMessage(AgentsCenter center, Agent agent);

}
