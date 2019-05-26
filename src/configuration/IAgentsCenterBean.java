package configuration;

import model.*;

import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.naming.NamingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Local
@Lock(LockType.READ)
public interface IAgentsCenterBean {

    //private final String AGENTS_LOOKUP = "java:jboss/exported/projekat_at_war_exploded/";
    final String AGENTS_LOOKUP = "java:jboss/exported/";
    final String AGENT_NAME_END = "!" + AgentI.class.getName();
    final String CONTEXT_CLASS_NAME = "Context";

    void sendAgentsCenters(AgentsCenter centers, List<AgentsCenter> receivers);
    //AgentsCenter getAgentsCenter();
    Set<AgentsCenter> getRegisteredCenters();
    Boolean isMasterNode();
    void addToRegisteredCenters(List<AgentsCenter> list);

    List<AgentType> getAvaliableAgentTypes() throws NamingException;
    List<AID> getRunningAgents();
    Map<AID, AgentI> getHostRunningAgents();

    List<String> traverse();
    AgentsCenter getAgentsCenter();
    AgentI runAgent(AgentType type, String name) throws NamingException;

    void addRunningAgents(List<AID> running);

    boolean stopHostAgent(AID aid);

    void deliverMessageToAgent(ACLMessage receivedMessage, AID aid);

    Map<String, List<AgentType>> getClusterTypesMap();
    void setClusterTypesMap(Map<String, List<AgentType>> clusterTypesMap);

    Set<AgentType> getAllTypes();
    List<AgentType> getHostTypes();

    void broadcastMessage(String wsMessage);
}
