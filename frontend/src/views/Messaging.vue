<template>
  <div class="messaging">
    <h1>Send ACL message</h1>
    <SendACLMessageSlot id="sendACLMessageSlot" ref="SendACLMessageSlot" />
  </div>
</template>

<script>

import SendACLMessageSlot from '@/components/SendACLMessageSlot.vue'

export default {
  name: "messaging",
  components: {
    SendACLMessageSlot
  },
  data() {
    return {

    }
  },
  methods: {
    updateAgents() {
      this.$refs.SendACLMessageSlot.updateAgents()
    },
    updateTypes() {
      
    },
    updateResults(payload) {
      
    }
  }
}
</script>

<style scoped>

h1 {
  margin: 20px 0px 20px;
}

#sendACLMessageSlot {
  position: fixed;
  height: 40%;
  width: 100%;
  padding: 0% 10% 0%;
}

</style>

