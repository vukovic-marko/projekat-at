package agents.crawlers;

import model.ACLMessage;
import model.AgentI;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.Map;

@Stateful
@Remote(AgentI.class)
public class MobileCrawler extends CrawlerAgent {

    private static final String URL = "https://www.mobile.de/";

    @Override
    protected void initArgs(Map<String, String> args) {
        initCrawler(URL);
    }

    @Override
    protected void onMessage(ACLMessage message) {

        System.out.println("I crawl on mobile.de!");
        broadcastInfo("Received message: " + message);

    }
}
