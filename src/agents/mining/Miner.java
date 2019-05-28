package agents.mining;

import model.ACLMessage;
import model.Agent;
import model.AgentI;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.Map;

@Stateful
@Remote(AgentI.class)
public class Miner extends Agent {

    protected String db;
    protected String collection;

    @Override
    protected void initArgs(Map<String, String> args) {
        db = args.get(db);
        collection = args.get(collection);
    }

    @Override
    protected void onMessage(ACLMessage message) {



    }
}
