package agents.mining;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.*;
import model.dto.FilterDTO;
import mongodb.IMongoDB;
import org.bson.Document;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Stateful
@Remote(AgentI.class)
public class Miner extends Agent {

    @EJB
    protected IMongoDB mongoDB;

    private static final String EXTENSION = "crw";

    @Override
    protected void initArgs(Map<String, String> args) {
        //db = args.get(db);
        //collection = args.get(collection);
    }

    @Override
    protected void onMessage(ACLMessage message) {

        broadcastInfo("Received message: " + message);


        MongoDatabase db = mongoDB.getDb();
        if (message.getPerformative()== Performative.REQUEST) {

            MongoCollection<Document> collection = db.getCollection(message.getContent() + "." + EXTENSION);


            FilterDTO map = (FilterDTO)message.getContentObj();

            Set<Car> cars = new HashSet<>();
            Document searchQuery = new Document();
            if (map == null) {
                // Get all
                FindIterable<Document> documents = collection.find(searchQuery);

                for (Document document: documents) {
                    cars.add(mongoDB.documentToCar(document));
                }

            } else {
                // TODO: Filtriranje

                FindIterable<Document> documents = collection.find(searchQuery);

                for (Document document: documents) {
                    cars.add(mongoDB.documentToCar(document));
                }



            }

            ACLMessage msg = new ACLMessage(Performative.INFORM);
            msg.setSender(aid);
            msg.addReceiver(message.getSender());
            msg.setContentObj(cars);
            messenger.sendMessage(msg);
        } else if (message.getPerformative() == Performative.CANCEL) {
            // Gasenje napravljenog miner-a
            // Radi se iz AgentsCenterBean-a
            //stop();
        }

    }
}
