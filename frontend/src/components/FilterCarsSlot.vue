<template>
    <div>    
        <form v-on:submit="sub">
            <div class="form-row">
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" id="modelAutomobila" placeholder="Model automobila">
                </div>
                <div class="form-group col-md-1">

                </div>
                <div class="form-group col-md-2">
                    <input type="number" class="form-control" id="minPrice" placeholder="Cena od" min="1">
                </div>
                <div class="form-group col-md-2">
                    <input type="number" class="form-control" id="maxPrice" placeholder="Do" min="1">
                </div>
                <div class="form-group col-md-1">

                </div>
                <div class="form-group col-md-2">
                    <select class="form-control" id="vrstaGorivaAutomobila">
                        <option>Dizel</option>
                        <option>Benzin</option>
                    </select>
                </div>
            </div>
            <div class="form-row">
                
            </div>
            <div class="form-row">
                <div class="form-group col-md-2">
                    <input type="number" class="form-control" id="minKubikaza" placeholder="Kubikaža od" min="1">
                </div>
                <div class="form-group col-md-2">
                    <input type="number" class="form-control" id="maxKubikaza" placeholder="Do" min="1">
                </div>
                <div class="form-group col-md-1">

                </div>
                <div class="form-group col-md-2">
                    <input type="number" class="form-control" id="minGodiste" placeholder="Godište od" min="1950">
                </div>
                <div class="form-group col-md-2">
                    <input type="number" class="form-control" id="maxGodiste" placeholder="Do" max="2019">
                </div>
                <div class="form-group col-md-1">

                </div>
                
            </div>
            <div class="form-row">
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" id="brojSedista" placeholder="Broj sedišta" min="1">
                </div>
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" id="brojVrata" placeholder="Broj vrata" min="1">
                </div>
                <div class="form-group col-md-4">
                    <input type="text" class="form-control" id="bojaAutomobila" placeholder="Boja">
                </div>
            </div>

            <button type="submit" class="btn btn-primary">Pretraži</button>
        </form>
    </div>
</template>

<script>
export default {
    name : 'filterCarsSlot',
    components: {

    },
    methods: {
        sub: function(e) {
            e.preventDefault();
            
            alert("form submited!");
        }
    }
}
</script>

<style scoped>

</style>
