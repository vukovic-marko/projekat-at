package agents.crawlers;

import model.Agent;
import model.AgentI;
import mongodb.IMongoDB;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.net.MalformedURLException;
import java.net.URL;

@Stateful
@Remote(AgentI.class)
public abstract class CrawlerAgent extends Agent {

    @EJB
    protected IMongoDB mongoDB;

    protected URL site;
    protected String location;

    protected void initCrawler(String url, String location) {

        try {
            this.site = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.location = location;

    }

}
