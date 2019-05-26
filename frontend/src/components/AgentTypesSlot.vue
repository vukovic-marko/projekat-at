<template>
    <div class="agentTypes">
        <span>Agent types</span>
        <ul class="list-group list-group-flush">
            <li class="list-group-item" v-for="type in types" v-bind:key="type.name + type.module">
                {{ type.name }}.{{type.module}}
            </li>
        </ul>
    </div>
</template>

<script>

import axios from 'axios'
import { API } from '../variables.js'

export default {
    name : 'agentTypesSlot',
    components: {

    },
    data() {
        return {
            types: [],
        }
    },
    created() {
        axios.get(API + '/agents/classes')
            .then(res => this.types = res.data)
            .catch(err => console.log(err))
    },
    methods: {
        updateTypes() {
            axios.get(API + '/agents/classes')
                .then(res => this.types = res.data)
                .catch(err => console.log(err))
        }
    }
}
</script>

<style scoped> 
    
.list-group{
    max-height: 100%;
    margin-bottom: 10px;
    overflow:scroll;
    -webkit-overflow-scrolling: touch;
    border: 3px solid rgb(33, 82, 173);
}

</style>


