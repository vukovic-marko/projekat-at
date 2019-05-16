package configuration;

import model.*;

import javax.ejb.Local;
import javax.naming.NamingException;
import java.util.List;
import java.util.Map;

@Local
public interface IAgentsCenterBean {

    //private final String AGENTS_LOOKUP = "java:jboss/exported/projekat_at_war_exploded/";
    final String AGENTS_LOOKUP = "java:jboss/exported/";
    final String AGENT_NAME_END = "!" + AgentI.class.getName();
    final String CONTEXT_CLASS_NAME = "Context";

    void sendAgentsCenters(AgentsCenter centers, List<AgentsCenter> receivers);
    //AgentsCenter getAgentsCenter();
    List<AgentsCenter> getRegisteredCenters();
    Boolean isMasterNode();
    void addToRegisteredCenters(List<AgentsCenter> list);
    List<AgentType> getTypes();

    List<AgentType> getAvaliableAgentTypes() throws NamingException;
    List<AID> getRunningAgents();
    Map<AID, AgentI> getHostRunningAgents();
    Map<AgentType, AgentsCenter> getTypesMap();

    List<String> traverse();
    AgentsCenter getAgentsCenter();
    AgentI runAgent(AgentType type, String name) throws NamingException;

    void addRunningAgents(List<AID> running);

    void stopAgent(String aidName, String typeName);
    void stopHostAgent(AID aid);

    void deliverMessageToAgent(ACLMessage receivedMessage, AID aid);
}
