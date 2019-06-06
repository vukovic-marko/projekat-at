<template>
    <div>    
        <form v-on:submit="sub">
            <div class="form-row">
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.maker" type="text" class="form-control" id="markaAutomobila" placeholder="Marka">
                </div>
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.model" type="text" class="form-control" id="modelAutomobila" placeholder="Model">
                </div>
                <div class="first-row col-md-1">
                    
                </div>
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.priceFrom" type="number" class="form-control" id="minPrice" placeholder="Cena od" min="1">
                </div>
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.priceTo" type="number" class="form-control" id="maxPrice" placeholder="Do" min="1">
                </div>
                <div class="first-row col-md-1">
                    
                </div>
                <div class="form-group col-md-2">
                    <select v-model="filterDTO.fuel" class="form-control" id="vrstaGorivaAutomobila">
                        <option value="" disabled selected>Gorivo</option>
                        <option>Dizel</option>
                        <option>Benzin</option>
                    </select>
                </div>
            </div>
            <div class="form-row">
                
            </div>
            <div class="form-row">
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.ccFrom" type="number" class="form-control" id="minKubikaza" placeholder="Kubikaža od" min="1">
                </div>
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.ccTo" type="number" class="form-control" id="maxKubikaza" placeholder="Do" min="1">
                </div>
                <div class="form-group col-md-1">

                </div>
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.yearFrom" type="number" class="form-control" id="minGodiste" placeholder="Godište od" min="1950">
                </div>
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.yearTo" type="number" class="form-control" id="maxGodiste" placeholder="Do" max="2019">
                </div>
                <div class="form-group col-md-1">

                </div>
                
            </div>
            <div class="form-row">
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.powerFrom" type="number" class="form-control" id="minSnaga" placeholder="Snaga od" min="1">
                </div>
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.powerTo" type="number" class="form-control" id="maxSnaga" placeholder="Do" min="1">
                </div>
                <div class="form-group col-md-1">

                </div>
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.mileageFrom" type="number" class="form-control" id="minKilometraza" placeholder="Kilometraža od" min="0">
                </div>
                <div class="form-group col-md-2">
                    <input v-model="filterDTO.mileageTo" type="number" class="form-control" id="maxKilometraza" placeholder="Do">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group col-md-4">
                    <input v-model="filterDTO.seats" type="number" class="form-control" id="brojSedista" placeholder="Broj sedišta" min="1">
                </div>
                <div class="form-group col-md-4">
                    <input v-model="filterDTO.doors" type="number" class="form-control" id="brojVrata" placeholder="Broj vrata" min="1">
                </div>
                <div class="form-group col-md-4">
                    <input v-model="filterDTO.color" type="text" class="form-control" id="bojaAutomobila" placeholder="Boja">
                </div>
            </div>

            <button :disabled="!btnEnabled" type="submit" class="btn btn-info">Pretraži</button>
        </form>
    </div>
</template>

<script>
import axios from 'axios';
import { API } from '../variables';
import { filter } from 'minimatch';
export default {
    name : 'filterCarsSlot',
    components: {

    },
    data() {
        return {
            filterDTO: {},
            btnEnabled: true
        }
    },
    methods: {
        sub: function(e) {
            e.preventDefault();

            //alert("form submited!");

            this.btnEnabled = false

            /*axios.post(API + "/filter", 
                    this.filterDTO,
                    { headers: { "Content-Type": "application/json" } } )
                .then( res => {
                    console.log("Got results")
                })
                .catch( err => {
                    console.log(err)
                })
                .finally( this.btnEnabled = true )*/

            this.$emit('sendWsMsg', this.filterDTO)

        },
        enableButton() {
            this.btnEnabled = true
        }
    },
    created: function() {
        this.filterDTO.fuel = "";
    }
}
</script>

<style scoped>

</style>
