package model;

import java.io.Serializable;

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
}
