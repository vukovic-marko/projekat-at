<template>
  <div id="app">
    <b-navbar toggleable="lg" type="dark" variant="dark">
      <b-navbar-brand href="#">AT-projekat</b-navbar-brand>

      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

      <b-collapse id="nav-collapse" is-nav>
        <b-navbar-nav>
          <b-nav-item to="/">Agents</b-nav-item>
          <b-nav-item to="/messaging">Messaging</b-nav-item>
          <b-nav-item to="/explore">Explore</b-nav-item>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <router-view/>
    <Console id="console" />
  </div>
</template>

<script>

import Console from '@/components/Console.vue'

export default {
  name: 'home',
  components: {
    Console
  },
  methods: {

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
  transform: translate(2%, -5%)
}
</style>
