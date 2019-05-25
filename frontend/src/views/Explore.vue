<template>
  <div class="explore">
    <h1>Pretraga automobila</h1>
    <FilterCarsSlot id="filterCarsSlot" />
  </div>
</template>

<script>

import FilterCarsSlot from '@/components/FilterCarsSlot.vue'


export default {
  components: {
    FilterCarsSlot
  }
}
</script>

<style>
h1 {
  margin: 20px 0px 20px;
}
#filterCarsSlot {
  position: fixed;
  height: 40%;
  width: 100%;
  padding: 0% 10% 0%;
  overflow-y: scroll;
}
</style>

