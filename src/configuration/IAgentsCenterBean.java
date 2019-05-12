package configuration;

import model.AgentType;
import model.AgentsCenter;
import javax.ejb.Local;
import javax.naming.NamingException;
import java.util.List;

@Local
public interface IAgentsCenterBean {
    void sendAgentsCenters(AgentsCenter centers, List<AgentsCenter> receivers);
    //AgentsCenter getAgentsCenter();
    List<AgentsCenter> getRegisteredCenters();
    Boolean isMasterNode();
    void addToRegisteredCenters(List<AgentsCenter> list);
    List<AgentType> getTypes();

    List<AgentType> getAvaliableAgentTypes() throws NamingException;
}
