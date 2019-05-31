package mongodb;

import application.App;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Car;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Singleton
public class MongoDB implements IMongoDB {

    private MongoClient client;
    private MongoDatabase db;
    private List<String> collections;

    @PostConstruct
    public void init() {

        System.out.println("initializing db bean");

        InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties");
        Properties props = new Properties();

        String localhost = null;
        Integer port = null;
        String dbName = null;
        if (input!=null) {
            try {
                props.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            localhost = props.getProperty("mongodb.localhost", "localhost");
            port = Integer.parseInt(props.getProperty("mongodb.port", "27017"));
            dbName = props.getProperty("db", "default_db");
        } else {
            System.out.println("Nije pronadjena config.properties datoteka!");
        }

        client = new MongoClient(localhost, port);

        db = client.getDatabase(dbName);

    }

    @PreDestroy
    public void preDestroy() {
        client.close();
    }

    public void addDocument(String databaseName, String collectionName, Document document) {

        MongoCollection<org.bson.Document> collection = db.getCollection("master");

        collection.insertOne(document);

    }

    public void deleteFrom() {

    }

    @Override
    public MongoDatabase getDb() {
        return db;
    }

    @Override
    public Car documentToCar(Document document) {
        Car car = new Car();
        car.setId(document.getString("id"));
        car.setModel(document.getString("model"));
        car.setYear(document.getInteger("year"));
        car.setPrice(document.getDouble("price"));
        car.setCubicCapacity(document.getInteger("cubicCapacity"));
        car.setFuel(document.getString("fuel"));
        car.setNumberOfSeats(document.getInteger("numberOfSeats"));
        car.setDoorCount(document.getString("doorCount"));
        car.setColor(document.getString("color"));

        car.setHeading(document.getString("heading"));
        car.setManufacturer(document.getString("manufacturer"));
        car.setLink(document.getString("link"));

        return car;
    }

    @Override
    public Document carToDocument(Car car) {
        Document document = new Document();
        document.put("id", car.getId());
        document.put("model", car.getModel());
        document.put("year", car.getYear());
        document.put("price", car.getPrice());
        document.put("cubicCapacity", car.getCubicCapacity());
        document.put("fuel", car.getFuel());
        document.put("numberOfSeats", car.getNumberOfSeats());
        document.put("doorCount", car.getDoorCount());
        document.put("color", car.getColor());

        document.put("heading", car.getHeading());
        document.put("manufacturer", car.getManufacturer());
        document.put("link", car.getLink());

        return document;
    }
}
