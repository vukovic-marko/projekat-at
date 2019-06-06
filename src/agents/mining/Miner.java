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

                if (map.getMaker() != null && !map.getMaker().equals(""))
                    searchQuery.put("manufacturer", new Document("$regex", "^" + map.getMaker()).append("$options", "i"));
                if (map.getModel() != null && !map.getModel().equals(""))
                    searchQuery.put("model", new Document("$regex", "^" + map.getModel()).append("$options", "i"));

                if (map.getPriceFrom() != null && map.getPriceTo() != null)
                    searchQuery.put("price", new Document("$gte", map.getPriceFrom()).append("$lte", map.getPriceTo()));
                else if (map.getPriceFrom() != null && map.getPriceTo() == null)
                    searchQuery.put("price", new Document("$gte", map.getPriceFrom()));
                else if (map.getPriceFrom() == null && map.getPriceTo() != null)
                    searchQuery.put("price", new Document("$lte", map.getPriceTo()));

                if (map.getFuel() != null && !map.getFuel().equals(""))
                    searchQuery.put("fuel", map.getFuel());

                if (map.getCcFrom() != null && map.getCcTo() != null)
                    searchQuery.put("cubicCapacity", new Document("$gte", map.getCcFrom()).append("$lte", map.getCcTo()));
                else if (map.getCcFrom() != null && map.getCcTo() == null)
                    searchQuery.put("cubicCapacity", new Document("$gte", map.getCcFrom()));
                else if (map.getCcFrom() == null && map.getCcTo() != null)
                    searchQuery.put("cubicCapacity", new Document("$lte", map.getCcTo()));

                if (map.getYearFrom() != null && map.getYearTo() != null)
                    searchQuery.put("year", new Document("$gte", map.getYearFrom()).append("$lte", map.getYearTo()));
                else if (map.getYearFrom() != null && map.getYearTo() == null)
                    searchQuery.put("year", new Document("$gte", map.getYearFrom()));
                else if (map.getYearFrom() == null && map.getYearTo() != null)
                    searchQuery.put("year", new Document("$lte", map.getYearTo()));

                if (map.getPowerFrom() != null && map.getPowerTo() != null)
                    searchQuery.put("horsepower", new Document("$gte", map.getPowerFrom()).append("$lte", map.getPowerTo()));
                else if (map.getPowerFrom() != null && map.getPowerTo() == null)
                    searchQuery.put("horsepower", new Document("$gte", map.getPowerFrom()));
                else if (map.getPowerFrom() == null && map.getPowerTo() != null)
                    searchQuery.put("horsepower", new Document("$lte", map.getPowerTo()));

                if (map.getMileageFrom() != null && map.getMileageTo() != null)
                    searchQuery.put("mileage", new Document("$gte", map.getMileageFrom()).append("$lte", map.getMileageTo()));
                else if (map.getMileageFrom() != null && map.getMileageTo() == null)
                    searchQuery.put("mileage", new Document("$gte", map.getMileageFrom()));
                else if (map.getMileageFrom() == null && map.getMileageTo() != null)
                    searchQuery.put("mileage", new Document("$lte", map.getMileageTo()));

                if (map.getSeats() != null)
                    searchQuery.put("numberOfSeats", map.getSeats());

                if (map.getDoors() != null && !map.getDoors().equals(""))
                    searchQuery.put("doorCount", map.getDoors());

                if (map.getColor() != null && !map.getColor().equals(""))
                    searchQuery.put("color", new Document("$regex", "^" + map.getColor()).append("$options", "i"));

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
