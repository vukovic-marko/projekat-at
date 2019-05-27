package messaging;

import model.ACLMessage;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;

public class JMSWrapper implements Serializable {

    private String groupID;
    private int index;
    private ACLMessage message;

    public JMSWrapper(ObjectMessage jmsMsg) {
        try {
            this.groupID = jmsMsg.getStringProperty("GroupID");
            this.index = jmsMsg.getIntProperty("Index");
            this.message = (ACLMessage) jmsMsg.getObject();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public String getGroupId() {
        return groupID;
    }

    public void setGroupId(String groupID) {
        this.groupID = groupID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ACLMessage getMessage() {
        return message;
    }

    public void setMessage(ACLMessage message) {
        this.message = message;
    }
}
