package model;

import java.io.Serializable;

public class AgentType implements Serializable {

    private String name;
    private String module;

    public AgentType() {
    }

    public AgentType(String name, String module) {
        this.name = name;
        this.module = module;
    }

    public AgentType(String fullName) {
        int lastDot = fullName.lastIndexOf('.');
        this.module = fullName.substring(0, lastDot);

        if (lastDot != -1) {
            this.name = fullName.substring(lastDot + 1);
        } else {
            this.name = fullName;
        }
    }

    @Override
    public boolean equals(Object obj) {
        AgentType at = (AgentType) obj;
        return (at.getName().equals(this.name) &&
                at.getModule().equals(this.module));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
