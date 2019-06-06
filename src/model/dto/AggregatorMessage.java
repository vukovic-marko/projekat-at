package model.dto;

import java.io.Serializable;

public class AggregatorMessage implements Serializable {

    private FilterDTO filter;
    private String wsSession;

    public AggregatorMessage() {

    }

    public FilterDTO getFilter() {
        return filter;
    }

    public void setFilter(FilterDTO filter) {
        this.filter = filter;
    }

    public String getWsSession() {
        return wsSession;
    }

    public void setWsSession(String wsSession) {
        this.wsSession = wsSession;
    }
}
