package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ACLMessage implements Serializable {

    private Performative performative;
    private AID sender;
    private AID[] receivers;
    private AID replyTo;

    private String content;
    private Object contentObj;
    private HashMap<String, Object> userArgs;

    private String language;
    private String encoding;
    private String ontology;
    private String protocol;
    private String conversationId;

    private String replyWith;
    private String inReplyTo;
    private Long replyBy;

    public ACLMessage () {

    }

    public ACLMessage(Performative performative) {
        this.performative = performative;
    }

    public Performative getPerformative() {
        return performative;
    }

    public void setPerformative(Performative performative) {
        this.performative = performative;
    }

    public AID getSender() {
        return sender;
    }

    public void setSender(AID sender) {
        this.sender = sender;
    }

    public AID[] getReceivers() {
        return receivers;
    }

    public void setReceivers(AID[] receivers) {
        this.receivers = receivers;
    }

    public AID getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(AID replyTo) {
        this.replyTo = replyTo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getContentObj() {
        return contentObj;
    }

    public void setContentObj(Object contentObj) {
        this.contentObj = contentObj;
    }

    public HashMap<String, Object> getUserArgs() {
        if (userArgs == null) {
            userArgs = new HashMap<>();
        }
        return userArgs;
    }

    public void setUserArgs(HashMap<String, Object> userArgs) {
        this.userArgs = userArgs;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getReplyWith() {
        return replyWith;
    }

    public void setReplyWith(String replyWith) {
        this.replyWith = replyWith;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inreplyTo) {
        inReplyTo = inreplyTo;
    }

    public Long getReplyBy() {
        return replyBy;
    }

    public void setReplyBy(Long replyBy) {
        this.replyBy = replyBy;
    }

    public void addReceiver(AID aid) {

        List<AID> tempList;

        if (receivers == null) {
            tempList = new ArrayList<>();
        } else {
            tempList = Arrays.asList( receivers );
        }

        tempList.add(aid);

        receivers = new AID[tempList.size()];

        receivers = tempList.toArray(receivers);

    }

    public boolean canReplyTo() {
        return sender != null || replyTo != null;
    }

    public ACLMessage makeReply(Performative performative) {
        if (!canReplyTo())
            throw new IllegalArgumentException("There's no-one to receive the reply.");

        ACLMessage reply = new ACLMessage(performative);

        reply.addReceiver(replyTo != null ? replyTo : sender);

        reply.language = language;
        reply.ontology = ontology;
        reply.encoding = encoding;

        reply.protocol = protocol;
        reply.conversationId = conversationId;
        reply.inReplyTo = replyWith;

        reply.userArgs = new HashMap<>();

        return reply;
    }

    public void addReceivers(List<AID> agents) {

        receivers = new AID[agents.size()];

        receivers = agents.toArray(receivers);

    }

    @Override
    public String toString() {

        String retVal = "\nPerformative: " + performative;
        retVal += (sender!=null)?"\nSender: "+sender.getName():"";
        retVal += (replyTo!=null && !replyTo.equals(""))?"\nReply to: "+replyTo.getName():"";
        retVal += (content!=null && !content.equals(""))?"\nContent: "+content:"";

        retVal += (language!=null && !language.equals(""))?"\nLanguage: "+language:"";
        retVal += (encoding!=null && !encoding.equals(""))?"\nEncoding: "+encoding:"";
        retVal += (protocol!=null && !protocol.equals(""))?"\nProtocol: "+protocol:"";
        retVal += (ontology!=null && !ontology.equals(""))?"\nOntology: "+ontology:"";

        retVal += (conversationId!=null && !conversationId.equals(""))?"\nConversation ID: "+conversationId:"";

        retVal += (replyWith!=null && !replyWith.equals(""))?"\nReply with: "+replyWith:"";
        retVal += (inReplyTo!=null && !inReplyTo.equals(""))?"\nIn reply to: "+inReplyTo:"";
        retVal += (replyBy!=null)?"\n"+replyBy:"";

        return retVal;

    }

}
