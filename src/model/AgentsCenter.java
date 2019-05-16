package model;

import java.io.Serializable;

public class AgentsCenter implements Serializable {

    private String address;
    private String alias;

    public AgentsCenter() {

    }

    public AgentsCenter(String address, String alias) {
        this.address = address;
        this.alias = alias;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj instanceof AgentsCenter) {
            AgentsCenter center = (AgentsCenter)obj;
            if (this.address.equals(center.getAddress()) && this.alias.equals(center.getAlias())) {
                return true;
            }
        }

        return false;

    }
}
