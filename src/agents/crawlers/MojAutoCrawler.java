package agents.crawlers;

import model.ACLMessage;
import model.AgentI;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.Map;

@Stateful
@Remote(AgentI.class)
public class MojAutoCrawler extends CrawlerAgent {

    @Override
    protected void initArgs(Map<String, String> args) {
        // TODO Determine save location
        initCrawler("https://www.mojauto.rs/", "");
    }

    @Override
    protected void onMessage(ACLMessage message) {

        System.out.println("I crawl on mojauto.rs!");
        broadcastInfo("Received message: " + message);

    }
}
