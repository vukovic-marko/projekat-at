package model;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj instanceof AgentType) {
            AgentType type = (AgentType)obj;
            if (this.name.equals(type.getName()) && this.module.equals(type.getModule())) {
                return true;
            }
        }

        return false;

    }

    @Override
    public int hashCode() {
        return Objects.hash(name, module);
    }

}
