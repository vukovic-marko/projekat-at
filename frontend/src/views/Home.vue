<template>
  <div class="home">
    <AgentTypesSlot id="agentTypesSlot" />
    
  </div>
</template>

<script>

import AgentTypesSlot from '@/components/AgentTypesSlot.vue'

export default {
  name: 'home',
  components: {
    AgentTypesSlot
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
  border: 3px solid rgb(33, 82, 173);
}

</style>
