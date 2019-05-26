<template>
    <div>    
        <form v-on:submit="sendACLMessage">
            <div class="form-row">
                <div class="form-group col-md-4">
                    <select class="custom-select" v-model="performative">
                        <option value="" disabled selected>Choose performative</option>
                        <option v-for="performative in performatives"
                                :value="performative" 
                                v-bind:key="performative">
                                {{performative}}
                        </option>
                    </select>
                </div>
                <div class="form-group col-md-4">
                    <input type="text" placeholder="Content" class="form-control" v-model="content">
                </div>
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" placeholder="Protocol" v-model="protocol">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group col-md-4">
                    <select class="custom-select" v-model="sender">
                        <option value="" disabled selected>Choose sender</option>
                        <option v-for="agent in runningAgents"
                                :value="agent" 
                                v-bind:key="agent.name + agent.type.name + agent.host.address">
                                {{agent.name}}[{{agent.type.name}}]@{{agent.host.alias}}
                        </option>
                    </select>
                </div>
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" placeholder="Language" v-model="language">
                </div>
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" placeholder="ConversationID" v-model="conversationId">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group col-md-4">
                    <select class="custom-select" v-model="receiver">
                        <option value="" disabled selected>Choose receiver</option>
                        <option v-for="agent in runningAgents"
                                :value="agent" 
                                v-bind:key="agent.name + agent.type.name + agent.host.address">
                                {{agent.name}}[{{agent.type.name}}]@{{agent.host.alias}}
                        </option>
                    </select>
                </div>
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" placeholder="Encoding" v-model="encoding">
                </div>
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" placeholder="ReplyWith" v-model="replyWith">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group col-md-4">
                    <select class="custom-select" v-model="replyTo">
                        <option value="" disabled selected>Reply to</option>
                        <option v-for="agent in runningAgents"
                                :value="agent" 
                                v-bind:key="agent.name + agent.type.name + agent.host.address">
                                {{agent.name}}[{{agent.type.name}}]@{{agent.host.alias}}
                        </option>
                    </select>
                </div>
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" placeholder="Ontology" v-model="ontology">
                </div>
                <div class="form-group col-md-4">
                    <input type="number" class="form-control" placeholder="ReplyBy" v-model="replyBy">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group col-md-1">
                    <button type="submit" :disabled="!btnEnabled" class="btn btn-lg btn-primary">Send</button>
                </div>
            </div>
        </form>
    </div>
</template>

<script>

import axios from 'axios'
import { API } from '../variables.js'

export default {
    name: "sendACLMessageSlot",
    data() {
        return {
            runningAgents: [],
            performatives: [],
            sender: "",
            receiver: "",
            performative: "",
            content: "",
            replyTo: "",
            language: "",
            encoding: "",
            ontology: "",
            protocol: "",
            conversationId: "",
            replyWith: "",
            replyBy: null,
            btnEnabled: true
        }
    },
    created() {

        axios.get(API + "/messages")
            .then(res =>  {
                this.performatives = res.data
            })
            .catch(err => console.log(err))

        axios.get(API + "/agents/running")
            .then(res =>  {
                this.runningAgents = res.data
            })
            .catch(err => console.log(err))
        
    },
    methods: {
        sendACLMessage(e) {
            e.preventDefault()

            var aclMsg = {}
            var performative = this.performative

            if (performative === "") {
                alert("Please select performative")
                return
            }

            if (this.sender === "") {
                aclMsg.sender = null
            } else {
                aclMsg.sender = this.sender
            }

            if (this.replyTo === "") {
                aclMsg.replyTo = null
            } else {
                aclMsg.replyTo = this.replyTo
            }
            
            aclMsg.receivers = new Array()
            
            if (this.receiver !== "") {
                aclMsg.receivers.push(this.receiver)
            }
            
            performative = performative.replace(" ", "_")
            performative = performative.toUpperCase()
            aclMsg.performative = performative
            
            aclMsg.content = this.content

            aclMsg.language = this.language
            aclMsg.encoding = this.encoding
            aclMsg.ontology = this.ontology
            aclMsg.protocol = this.protocol
            aclMsg.conversationId = this.conversationId
            aclMsg.replyWith = this.replyWith
            aclMsg.replyBy = this.replyBy      
            
            aclMsg.userArgs = null
            aclMsg.contentObj = new Object()
            aclMsg.inReplyTo = ""

            this.btnEnabled = false

            axios.post(API + "/messages", 
                aclMsg,
                { headers: { "Content-Type": "application/json" } } )
                .then( res => {
                    console.log("Message sent via JMS")
                })
                .catch( err => {
                    console.log(err)
                })
                .finally( this.btnEnabled = true )

            // this.resetForm()

        },
        updateAgents() {
            axios.get(API + "/agents/running")
                .then(res =>  {
                    this.runningAgents = res.data
                    // this.resetForm()
                })
                .catch(err => console.log(err))            
        },
        updateTypes() {
            return
        },
        resetForm() {
            this.sender = ""
            this.receiver = ""
            this.performative = ""
            this.replyTo = ""
            this.content = ""
            this.language = ""
            this.encoding = ""
            this.ontology = ""
            this.protocol = ""
            this.conversationId = ""
            this.replyWith = ""
            this.replyBy = null
        }
    }
}
</script>


<style scoped>

</style>

