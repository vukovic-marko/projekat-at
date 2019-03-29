package model;

public class AgentsCenter {

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
}
