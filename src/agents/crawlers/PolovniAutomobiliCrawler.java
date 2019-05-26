package agents.crawlers;

import model.ACLMessage;
import model.Agent;
import model.AgentI;

import javax.ejb.Remote;
import javax.ejb.Stateful;

@Stateful
@Remote(AgentI.class)
public class PolovniAutomobiliCrawler extends Agent {

    @Override
    protected void onMessage(ACLMessage message) {

        System.out.println("I crawl on polovniautomobili.com!");

    }
}
