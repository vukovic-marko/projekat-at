package agents.mining;

import model.*;
import model.dto.ResultsDTO;
import websocket.MessageType;
import websocket.WSMessage;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Stateful
@Remote(AgentI.class)
public class Aggregator extends Agent {

    private Integer pageSize = 50;
    private List<Car> results;
    private boolean aggregating;

    @Override
    protected void initArgs(Map<String, String> args) {
        this.results = Collections.synchronizedList(new ArrayList<>());
        this.pageSize = Integer.parseInt(args.get("pageSize"));
    }

    @Override
    protected void onMessage(ACLMessage message) {

        broadcastInfo("Received message: " + message);

        if (message.getPerformative() == Performative.REQUEST) {

            if (aggregating) {
                broadcastInfo("Busy");
            }

            aggregating = true;

            broadcastInfo("Starting aggregation");

            results = Collections.synchronizedList(new ArrayList<>());

            // TODO Napraviti i pokrenuti miner-e
            // TODO Da li uposliti i druge centre ?



            // Ceka se na rezultate neko vreme
            ACLMessage selfMsg = new ACLMessage(Performative.CANCEL);
            selfMsg.setSender(aid);
            selfMsg.addReceiver(aid);
            messenger.sendMessage(selfMsg, 20000);

        }
        else if (message.getPerformative() == Performative.INFORM) {

            // TODO Dodati dobijene rezultate

            Object obj = message.getContentObj();

            List<Car> receivedResults = null;
            if (obj != null) {
                broadcastInfo("Received results");
                receivedResults = (List<Car>) obj;
                results.addAll(receivedResults);
            } else {
                broadcastInfo("Results not delivered");
            }


        } else if (message.getPerformative() == Performative.CANCEL) {

            broadcastInfo("Aggregation finished");

            // TODO Poslati rezultate preko soketa (da li slati sve ?)
            // TODO Filtriranje se radi na frontu ?

            WSMessage wsMessage = new WSMessage("Results delivered", MessageType.RESULTS);
            wsMessage.setPayload(new ResultsDTO(results, pageSize));
            ws.sendWSMessage(wsMessage);

            aggregating = false;
        } else if (message.getPerformative() == Performative.REQUEST_WHENEVER) {

            Integer targetPage = Integer.parseInt(message.getContent());

            broadcastInfo("Got request for data at page " + targetPage);

            WSMessage wsMessage = new WSMessage("Results delivered", MessageType.RESULTS);
            wsMessage.setPayload(new ResultsDTO(results, targetPage, pageSize));
            ws.sendWSMessage(wsMessage);

        }

    }
}
