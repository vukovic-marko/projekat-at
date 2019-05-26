package mongodb;

import application.App;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Singleton
public class MongoDB {

    private MongoClient client;
    private MongoDatabase db;

    @PostConstruct
    public void init() {

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

}
