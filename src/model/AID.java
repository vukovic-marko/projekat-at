package model;

import java.io.Serializable;
import java.util.Objects;

public class AID implements Serializable {

    String name;
    AgentsCenter host;
    AgentType type;

    public AID() {
    }

    public AID(String name, AgentsCenter host, AgentType type) {
        this.name = name;
        this.host = host;
        this.type = type;
    }

    public AID(String aidName, String typeName, String moduleName, AgentsCenter agentsCenter) {

        this.name = aidName;
        this.type = new AgentType(typeName, moduleName);
        this.host = agentsCenter;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AgentsCenter getHost() {
        return host;
    }

    public void setHost(AgentsCenter host) {
        this.host = host;
    }

    public AgentType getType() {
        return type;
    }

    public void setType(AgentType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj instanceof AID) {
            AID aid = (AID) obj;
            if (this.name.equals(aid.getName()) && this.host.equals(aid.getHost())
                    && this.type.equals(aid.getType())) {
                return true;
            }
        }

        return false;

    }

    @Override
    public int hashCode() {
        return Objects.hash(name, host, type);
    }
}
