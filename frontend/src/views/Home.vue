<template>
  <div class="home">
    <AgentTypesSlot id="agentTypesSlot" ref="agentTypesSlot" />
    <RunningAgentsSlot id="runningAgentsSlot" ref="runningAgentsSlot" />
    <RunAgentSlot id="runAgentSlot" ref="runAgentSlot" @new-agent="newAgent" />
    <StopAgentSlot id="stopAgentSlot" ref="stopAgentSlot" @stopped-agent="stoppedAgent" />
  </div>
</template>

<script>

import AgentTypesSlot from '@/components/AgentTypesSlot.vue'
import RunningAgentsSlot from '@/components/RunningAgentsSlot.vue'
import RunAgentSlot from '@/components/RunAgentSlot.vue'
import StopAgentSlot from '@/components/StopAgentSlot.vue'

export default {
  name: 'home',
  components: {
    AgentTypesSlot,
    RunningAgentsSlot,
    RunAgentSlot,
    StopAgentSlot
  },
  data() {
    return {

    }
  },
  created() {

  },
  methods: {
    newAgent(aid) {
      if (aid) {
        this.$refs.runningAgentsSlot.addRunningAgent(aid)
        this.$refs.stopAgentSlot.addRunningAgent(aid)
      } else {
        this.$refs.runningAgentsSlot.updateAgents()
        this.$refs.stopAgentSlot.updateAgents()
      }
      
    },
    stoppedAgent(aid) {
      this.$refs.runningAgentsSlot.removeRunningAgent(aid)
      this.$refs.stopAgentSlot.removeRunningAgent(aid)
    },
    updateAgents() {
      this.$refs.runningAgentsSlot.updateAgents()
      this.$refs.stopAgentSlot.updateAgents()
    },
    updateTypes() {
      this.$refs.runAgentSlot.updateTypes()
      this.$refs.agentTypesSlot.updateTypes()
    },
    updateResults(payload) {
      
    }
  }
}
</script>


<style scoped>

#agentTypesSlot {
  position: fixed;
  left: 0;
  height: 45%;
  width: 25%;
  transform: translate(8%, 5%);
}

#runningAgentsSlot {
  position: fixed;
  left: 0;
  height: 45%;
  width: 25%;
  transform: translate(116%, 5%);
}

#runAgentSlot {
  position: fixed;
  left: 0;
  height: 45%;
  width: 25%;
  transform: translate(254%, 5%);
  align-content: center;
}

#stopAgentSlot {
  position: fixed;
  left: 0;
  height: 45%;
  width: 25%;
  transform: translate(254%, 70%);
  align-content: center;
}

</style>
