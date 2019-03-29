package model;

import java.util.HashMap;

public class ACLMessage {

    enum Performative {
        INFORM,

    }

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
    private String InreplyTo;
    private String replyBy;

    public ACLMessage () {

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

    public String getInreplyTo() {
        return InreplyTo;
    }

    public void setInreplyTo(String inreplyTo) {
        InreplyTo = inreplyTo;
    }

    public String getReplyBy() {
        return replyBy;
    }

    public void setReplyBy(String replyBy) {
        this.replyBy = replyBy;
    }
}
