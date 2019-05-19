<template>
    <div class="agentTypes">
        <span>Running agents</span>
        <ul class="list-group list-group-flush">
            <li class="list-group-item" v-for="aid in running" v-bind:key="aid.id">
                {{ aid.name }}[{{ aid.type.name }}]@{{ aid.host.alias }}
            </li>
        </ul>
    </div>
</template>

<script>

import axios from 'axios'
import { API } from '../variables.js'

export default {
    name : 'runningAgentsSlot',
    components: {

    },
    data() {
        return {
            running: [],
        }
    },
    created() {        
        axios.get(API + "/agents/running")
            .then(res => this.running = res.data)
            .catch(err => console.log(err))
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


