package configuration;

import model.AgentsCenter;
import javax.ejb.Local;
import java.util.List;

@Local
public interface IAgentsCenterBean {
    void sendAgentsCenters(AgentsCenter centers, List<AgentsCenter> receivers);
    AgentsCenter getAgentsCenter();
    List<AgentsCenter> getRegisteredCenters();
    Boolean isMasterNode();
    void addToRegisteredCenters(List<AgentsCenter> list);
}
