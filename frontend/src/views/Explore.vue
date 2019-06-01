<template>
  <div class="explore">
    <h1>Prikaz rezultata</h1>
    <div class="table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th scope="col">Maker</th>
            <th scope="col">Model</th>
            <th scope="col">Year</th>
            <th scope="col">Mileage</th>
            <th scope="col">Fuel</th>
            <th scope="col">Number of seats</th>
            <th scope="col">Doors</th>
            <th scope="col">Color</th>
            <th scope="col">Cubic capacity</th>
            <th scope="col">Horsepower</th>
            <th scope="col">Price</th>
            <th scope="col">Link</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="car in cars" v-bind:key="car.id">
            <td scope="row"></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>

export default {
  name : 'Explore',
  components: {

  },
  data() {
    return {
      cars: [],
      currentPage: 0,
      totalPages: 0
    }
  },
  methods: {
    populate (cars) {
      this.cars = cars
    }
  }
}
</script>


<style>

h1 {
  margin: 20px 0px 20px;
}

</style>

