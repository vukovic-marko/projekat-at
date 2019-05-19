package websocket;

public class WSMessage {

    private ConsoleEndpoint.MessageType type;
    private String text;

    public WSMessage() {

    }

    public WSMessage(ConsoleEndpoint.MessageType type, String text) {

        this.type = type;

        this.text = text;
    }

    public ConsoleEndpoint.MessageType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(ConsoleEndpoint.MessageType type) {
        this.type = type;
    }
}
