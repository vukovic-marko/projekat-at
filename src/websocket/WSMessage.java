package websocket;

public class WSMessage {

    private String text;
    private Object payload;
    private MessageType type;

    public WSMessage() {
        this(null, null, null);
    }

    public WSMessage(String text) {

        this(text, MessageType.CONSOLE, null);
    }

    public WSMessage(String text, MessageType type) {

        this(text, type, null);

    }

    public WSMessage(String text, Object payload) {

        this(text, MessageType.CONSOLE, payload);

    }

    public WSMessage(String text, MessageType type, Object payload) {
        this.text = text;
        this.type = type;
        this.payload = payload;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

}
