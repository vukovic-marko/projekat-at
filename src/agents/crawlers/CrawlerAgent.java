package agents.crawlers;

import model.Agent;
import model.AgentI;
import model.Car;
import mongodb.IMongoDB;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

@Stateful
@Remote(AgentI.class)
public abstract class CrawlerAgent extends Agent {

    private static final String AGGREGAOR_TYPE = "projekat_at_ear_exploded.web.Aggregator";

    @EJB
    protected IMongoDB mongoDB;

    protected URL site;
    protected String location_db;

    protected Map<String, Car> cars;
    protected Set<String> visited;

    protected void initCrawler(String url) {

        try {
            this.site = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.location_db = center.getDBName();

    }

}
