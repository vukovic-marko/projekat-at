package websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@ServerEndpoint(value = "/soket")
public class ConsoleEndpoint {

    public enum MessageType {
        NEW_TYPE,
        NEW_AGENT,
        STOP_AGENT,
        CONSOLE
    }

    private static final Logger logger = Logger.getLogger(ConsoleEndpoint.class.getName());
    //private Map<String, Session> sessions = new ConcurrentHashMap<>();
    private List<Session> sessions = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessions.add(session);
        logger.log(Level.INFO, "Websocket opened");
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // Handle new messages
        System.out.println("Received message via websocket");
        logger.log(Level.INFO, "Received message via websocket");
        logger.log(Level.INFO, "Message content : " + message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.log(Level.INFO, "Websocket closed");
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.log(Level.INFO, "Websocket error occured");
        sessions.remove(session);
    }

    public void sendMessage(String message, MessageType type) {
        logger.log(Level.INFO, "Broadcasting message via websocket");
        logger.log(Level.INFO, "Message type : " + type.toString());
        logger.log(Level.INFO, "Message content : " + message);

        WSMessage wsMessage = new WSMessage(type, message);

        ObjectMapper mapper = new ObjectMapper();

        String jsonMessage = null;

        try {
            jsonMessage = mapper.writeValueAsString(wsMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        for (Session s : sessions) {
            s.getAsyncRemote().sendText(jsonMessage);
        }


    }

}
