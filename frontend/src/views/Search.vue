<template>
  <div role="tablist">
    <b-card no-body class="mb-1">
      <b-card-header header-tag="header" class="p-1" role="tab">
        <b-button block href="#" v-b-toggle.filter-accordion variant="info">Filter</b-button>
      </b-card-header>
      <b-collapse id="filter-accordion" visible accordion="my-accordion" role="tabpanel">
        <b-card-body>
          <FilterCarsSlot ref="filterCarsSlot" id="filterCarsSlot" @sendWsMsg="sendWsMsg" />
        </b-card-body>
      </b-collapse>
    </b-card>

    <b-card no-body class="mb-1">
      <b-card-header header-tag="header" class="p-1" role="tab">
        <b-button block href="#" v-b-toggle.explore-accordion variant="info">Explore</b-button>
      </b-card-header>
      <b-collapse id="explore-accordion" accordion="my-accordion" role="tabpanel">
        <b-card-body>
          <Explore id="exploreSlot" ref="exploreSlot" />
        </b-card-body>
      </b-collapse>
    </b-card>
  </div>
</template>

<script>

import FilterCarsSlot from '@/components/FilterCarsSlot.vue'
import Explore from '@/views/Explore.vue'

export default {
  components: {
    FilterCarsSlot,
    Explore
  },
  methods: {
    sendWsMsg(filter) {

        this.$emit('sendWsMsg', filter)

    },
    updateAgents() {

    },
    updateTypes() {
      
    },
    updateResults(payload) {
      this.$refs.exploreSlot.updateResults(payload);
      this.$refs.filterCarsSlot.enableButton();
    }
  }
}
</script>

<style>
h1 {
  margin: 20px 0px 20px;
}
#filterCarsSlot {
  height: 40%;
  width: 100%;
  padding: 0% 10% 0%;
  overflow-y: scroll;
}

#exploreSlot {
  position: fixed;
  left: 0;
  height: 45%;
  width: 100%;
  align-content: center;
  overflow-y: scroll;
}

</style>