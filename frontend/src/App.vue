<template>
  <div id="app">
    <b-navbar toggleable="lg" type="dark" variant="dark">
      <b-navbar-brand href="#">AT-projekat</b-navbar-brand>

      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

      <b-collapse id="nav-collapse" is-nav>
        <b-navbar-nav>
          <b-nav-item to="/">Agents</b-nav-item>
          <b-nav-item to="/messaging">Messaging</b-nav-item>
          <b-nav-item to="/search">Search</b-nav-item>
          <!-- <b-nav-item to="/explore">Explore</b-nav-item> -->
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <router-view ref="routerView" @sendWsMsg="sendWsMsg" />
    <Console id="console" :consoleOutput="consoleOutput" @clearConsole="consoleOutput=''" />
  </div>
</template>

<script>

import Console from '@/components/Console.vue'
import axios from 'axios'
import { types } from 'util';
import { SOCKET } from './variables';
import { filter } from 'minimatch';

export default {
  name: 'home',
  components: {
    Console
  },
  methods: {

    socket_opened: function() {
      //alert("Socket opened")
      console.log("Socket opened")
    },

    socket_closed: function(e) {
      //alert("Socket closed")
      console.log("Socket closed")
    },

    socket_error: function(e) {
      //alert("Socket error")
      console.log("Socket error")
    },

    on_message: function(message) {
      var msg = JSON.parse(message.data)
      
      var d = new Date();
      var time = ("0" + d.getHours()).substr(-2) + ':' + ("0" + d.getMinutes()).substr(-2) + ':' + ("0" + d.getSeconds()).substr(-2);
      this.consoleOutput += time + " - " + msg.text + "\n"
      
      if (msg.type === "CONSOLE") {

      } else if (msg.type === "UPDATE_TYPES") {
        this.$refs.routerView.updateTypes()
      } else if (msg.type == "UPDATE_AGENTS") {
        this.$refs.routerView.updateAgents()
      } else if (msg.type === "UPDATE_ALL") {
        this.$refs.routerView.updateTypes()
        this.$refs.routerView.updateAgents()
      } else if (msg.type === "RESULTS") {
        var d = new Date();
        var time = ("0" + d.getHours()).substr(-2) + ':' + ("0" + d.getMinutes()).substr(-2) + ':' + ("0" + d.getSeconds()).substr(-2);
        this.consoleOutput += time + " - " + "Total number of results: " + msg.payload.totalResults + "\n"
        this.$refs.routerView.updateResults(msg.payload)
      } else {
        alert("Unknown message type")
      }

    },
      sendWsMsg(filter) {
          this.socket.send(JSON.stringify(filter))
      }
  },
  data() {
    return {
      types: [],
      running: [],
      performatives: [],
      socket: new WebSocket(SOCKET),
      msg: "",
      obj: Object,
      consoleOutput: "",
    }
  },
  created() {
    this.socket.onopen = this.socket_opened;
    this.socket.onclose = this.socket_closed;
    this.socket.onerror = this.socket_error;
    this.socket.onmessage = this.on_message;
  }

}

</script>


<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 10px;

}

#console {
  position: fixed;
  bottom: 0;
  width: 96%;
  height: 35%;
  border: 3px solid rgb(33, 82, 173);
  transform: translate(2%, -5%);
  opacity: 1;
}
</style>
