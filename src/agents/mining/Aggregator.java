package agents.mining;

import com.mongodb.client.MongoIterable;
import model.*;
import model.dto.AggregatorMessage;
import model.dto.FilterDTO;
import model.dto.ResultsDTO;
import mongodb.IMongoDB;
import websocket.MessageType;
import websocket.WSMessage;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.naming.NamingException;
import java.util.*;

@Stateful
@Remote(AgentI.class)
public class Aggregator extends Agent {

    @EJB
    protected IMongoDB mongoDB;

    private static final String EXTENSION = "crw";

    private static final String MINER_TYPE = "projekat_at_ear_exploded.web.Miner";

    private int version = 0;

    private Integer pageSize = 50;
    private Set<Car> results;
    private boolean aggregating;

    private String currentClientSession;

    private int hiredMiners;
    private int minersResponded;

    private Integer hiredAggregators;
    private Integer aggregatorsResponded;

    private AID toReply;

    @Override
    protected void initArgs(Map<String, String> args) {
        this.results = Collections.synchronizedSet(new HashSet<>());
        this.pageSize = Integer.parseInt(args.get("pageSize"));
    }

    @Override
    protected void onMessage(ACLMessage message) {

        broadcastInfo("Received message: " + message);

        if (message.getPerformative() == Performative.REQUEST) {

            initAggregation();

            toReply = null;

            // Ne moze jer mozemo imati kolekcije iz prethodnih pokretanja servera
            //Set<String> collections = mongoDB.getCollections();

            AggregatorMessage aMsg = (AggregatorMessage)message.getContentObj();

            currentClientSession = aMsg.getWsSession();

            hireMiners(aMsg.getFilter());

            // Proveriti ima li agregatora na drugim centrima
            List<AID> agents = getAgents("Aggregator");
            Map<AgentsCenter, AID> receivers = new HashMap<>();
            if (agents!=null && agents.size()!=0) {
                for (AID aid : agents) {
                    AgentsCenter host = aid.getHost();
                    if (!host.equals(this.aid.getHost()) && !receivers.containsKey(host)) {
                        receivers.put(host, aid);
                        hiredAggregators++;
                        //break;
                    }
                }
            }

            ACLMessage msg = new ACLMessage(Performative.PROXY);
            msg.setSender(aid);

            msg.addReceivers(new ArrayList<>(receivers.values()));
            msg.setReplyTo(aid);

            messenger.sendMessage(msg);

            // Ceka se na rezultate neko vreme
            ACLMessage selfMsg = new ACLMessage(Performative.CANCEL);
            selfMsg.setSender(aid);
            selfMsg.addReceiver(aid);
            messenger.sendMessage(selfMsg, 20000);

        } else if (message.getPerformative() == Performative.PROXY) {

            initAggregation();

            toReply = message.getSender();

            // Obavestava samo lokalne miner-e
            hireMiners((FilterDTO) message.getContentObj());

            // Ceka se na rezultate neko vreme (krace nego agregator na hostu)

            // IZGLEDA DA OVO NE RADI
            ACLMessage selfMsg = new ACLMessage(Performative.CANCEL);
            selfMsg.setSender(aid);
            selfMsg.addReceiver(aid);
            messenger.sendMessage(selfMsg, 10000);

        } else if (message.getPerformative() == Performative.INFORM) {

            Object obj = message.getContentObj();

            Collection<Car> receivedResults = null;
            if (obj != null) {
                broadcastInfo("Received results");
                receivedResults = (Collection<Car>) obj;
                results.addAll(receivedResults);
            } else {
                broadcastInfo("Results not delivered");
            }

            if (message.getSender().getType().getName().equals("Miner")) {
                center.stopHostAgent(message.getSender());
                ++minersResponded;
            } else {
                ++aggregatorsResponded;
            }

            if (minersResponded==hiredMiners && aggregatorsResponded==hiredAggregators) {
                finishAggregation();
            }


        } else if (message.getPerformative() == Performative.CANCEL && aggregating) {

            finishAggregation();

        } else if (message.getPerformative() == Performative.REQUEST_WHENEVER) {

            // Abondoned

            /*Integer targetPage = Integer.parseInt(message.getContent());

            broadcastInfo("Got request for data at page " + targetPage);



            WSMessage wsMessage = new WSMessage("Results delivered", MessageType.RESULTS);
            wsMessage.setPayload(new ResultsDTO(new ArrayList<>(results), targetPage, pageSize));
            ws.sendWSMessage(wsMessage);*/

        }

    }

    private void initAggregation() {

        if (aggregating) {
            broadcastInfo("Busy");
        }

        aggregating = true;

        hiredMiners = 0;
        minersResponded = 0;

        hiredAggregators = 0;
        aggregatorsResponded = 0;

        broadcastInfo("Starting aggregation");

        results = Collections.synchronizedSet(new HashSet<>());
    }

    private void finishAggregation() {

        if (toReply==null) {
            finishAggregationHost();
        } else {
            finishAggregationRemote();
        }

        if (aid.getName().startsWith("GENERATED")) {
            center.stopHostAgent(aid);
        }
    }

    private void finishAggregationHost() {
        broadcastInfo("Aggregation finished");

        WSMessage wsMessage = new WSMessage("Results gathered and delivered", MessageType.RESULTS);
        //wsMessage.setPayload(new ResultsDTO(new ArrayList<>(results), pageSize));
        wsMessage.setPayload(new ResultsDTO(new ArrayList<>(results), pageSize, ""));
        ws.sendWSMessage(wsMessage, currentClientSession);

        aggregating = false;
    }

    private void finishAggregationRemote() {
        broadcastInfo("Remote aggregation finished");

        ACLMessage msg = new ACLMessage(Performative.INFORM);
        msg.setSender(aid);
        msg.addReceiver(toReply);
        msg.setContentObj(new HashSet<>(results));

        messenger.sendMessage(msg);

        aggregating = false;
    }

    private void hireMiners(FilterDTO contentObj) {

        //String dbName = center.getDBName();

        MongoIterable<String> collectionNames = mongoDB.getDb().listCollectionNames();

        for (String collectionName : collectionNames) {

            if (collectionName.endsWith("." + EXTENSION)) {
                hireMiner(collectionName.substring(0, collectionName.indexOf("." + EXTENSION)), contentObj);
                hiredMiners++;
            }

        }

    }

    private void hireMiner(String col, FilterDTO contentObj) {

        ACLMessage msg = new ACLMessage(Performative.REQUEST);
        msg.setSender(aid);

        AgentType agentType = new AgentType(MINER_TYPE);

        AgentI miner = null;
        try {
            miner = center.runAgent(agentType, aid.getName() + "_" + col);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        msg.addReceiver(miner.getAid());
        msg.setReplyTo(aid);
        msg.setContent(col);
        msg.setContentObj(contentObj);

        messenger.sendMessage(msg);

    }

    @Override
    public boolean isBusy() {
        return aggregating;
    }

}
