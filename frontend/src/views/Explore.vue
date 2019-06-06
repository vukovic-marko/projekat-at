<template>
  <div class="explore">
    <div class="table-responsive">
        <div class="overflow-auto">
          <b-pagination
            v-model="currentPage"
            :total-rows="rows"
            :per-page="perPage"
          ></b-pagination>
          <b-table
            id="cars-table"
            hover
            :items="cars"
            :per-page="perPage"
            :current-page="currentPage"
            :fields="fields"
            :tbody-tr-class="rowClass">
              <template slot="link" slot-scope="data">
                <a :href=data.value><font-awesome-icon icon="link" /></a>
              </template>
              <template slot="price" slot-scope="data">
                <span v-if="data.value!=0">{{data.value}}</span>
                <span v-else>Na upit</span>
              </template>
            </b-table>
        </div>
      <!--<table class="table">
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
          <tr v-for="car in cars" v-bind:key="car.link">
            <td scope="row">{{car.manufacturer}}</td>
            <td>{{car.model}}</td>
            <td>{{car.year}}</td>
            <td>{{car.mileage}}</td>
            <td>{{car.fuel}}</td>
            <td>{{car.numberOfSeats}}</td>
            <td>{{car.doorCount}}</td>
            <td>{{car.color}}</td>
            <td>{{car.cubicCapacity}}</td>
            <td>{{car.horsepower}}</td>
            <td>{{car.price}}</td>
            <td><a :href=car.link>link </a></td>
          </tr>
        </tbody>
      </table>-->
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
      totalPages: 0,
      version: -1,
      rows: 0,
      perPage: 50,
      currentPage: 1,
      fields: [
        { key: 'manufacturer', label: 'Manufacturer' },
        { key: 'model', label: 'Model' },
        { key: 'year', label: 'Year' },
        { key: 'mileage', label: 'Mileage' },
        { key: 'fuel', label: 'Fuel' },
        { key: 'numberOfSeats', label: 'Seats' },
        { key: 'doorCount', label: 'doors' },
        { key: 'color', label: 'Color' },
        { key: 'cubicCapacity', label: 'Cubic capacity' },
        { key: 'horsepower', label: 'Horsepower' },
        { key: 'price', label: 'Price' },
        { key: 'link', label: 'Link' },
      ],
    }
  },
  created: function() {
    
  },
  methods: {
    updateResults(payload) {

      this.rows = payload.resultsPage.length
      this.perPage = 50
      this.currentPage = 1
      this.cars = payload.resultsPage;

    },
    rowClass(item, type) {
      if (!item) return
      if (item.mileage === 0) return 'table-info'
      return 'table-warning'
    }
  }
}
</script>


<style>

h1 {
  margin: 20px 0px 20px;
}

</style>

