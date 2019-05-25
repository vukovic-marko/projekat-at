package mongodb;

import application.App;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
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


    public void test(String dbName) {
        System.out.println("");

        MongoDatabase db = client.getDatabase("agents_db");
        db.getCollection("master");
        db.getCollection("slave");
        db.getCollection("nova");

        //client.getDatabaseNames().forEach(System.out::println);

        MongoCollection<org.bson.Document> masterCol = db.getCollection("master");
        Document document = new Document();
        document.put("name", "Shubhama");
        document.put("company", "JCGA");
        document.put("post_count", 21);
        masterCol.insertOne(document);
        System.out.println("Inserted document = " + document);

        // Get all documents
        Document searchQuery = new Document();
        searchQuery.put("company", "JCGA");
        searchQuery.put("company", "");


        FindIterable<Document> documents = masterCol.find(searchQuery);

        for (Document document1: documents) {
            System.out.println(document1);
        }
    }
}
