package mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Car;
import org.bson.Document;

import javax.ejb.Local;
import java.util.Set;

@Local
public interface IMongoDB {

    MongoDatabase getDb();
    Car documentToCar(Document document);
    Document carToDocument(Car car);
    void addDocument(String collectionName, Document document);
    MongoCollection<Document> prepareCollection(String col);
    Set<String> getCollections();

}
