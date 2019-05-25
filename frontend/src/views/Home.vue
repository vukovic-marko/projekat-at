<template>
  <div class="home">
    <AgentTypesSlot id="agentTypesSlot" />
    <RunningAgentsSlot id="runningAgentsSlot" />
    <RunAgentSlot id="runAgentSlot" />
    <StopAgentSlot id="stopAgentSlot" />
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
