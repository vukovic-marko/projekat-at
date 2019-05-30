package mongodb;

import com.mongodb.client.MongoDatabase;
import model.Car;
import org.bson.Document;

import javax.ejb.Local;

@Local
public interface IMongoDB {

    MongoDatabase getDb();
    Car documentToCar(Document document);
    Document carToDocument(Car car);
}
