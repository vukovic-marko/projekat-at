<template>
    <div class="runningAgents">
        <div>Stop agent</div>
        <table>
            <tbody>
                <tr>
                    <td>
                        <select class="custom-select my-1 mr-sm-2" v-model="selectedAgent">
                            <option value="" disabled selected>Choose agent</option>
                            <option v-for="agent in running" 
                                    :value="agent" 
                                    v-bind:key="agent.name + agent.host.address">
                                    {{agent.name}}@{{agent.host.alias}}
                            </option>
                        </select>
                    </td>
                    &nbsp;
                    &nbsp;
                    <td>
                        <button v-on:click="stopAgent" id="btnStop" class="btn btn-danger">Stop</button> 
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>

import axios from 'axios'
import { API } from '../variables.js'

export default {
    name : 'stopAgentSlot',
    components: {

    },
    data() {
        return {
            running: [],
            selectedAgent: "",
        }
    },
    created() {
        axios.get(API + '/agents/running')
            .then(res => this.running = res.data)
            .catch(err => console.log(err))
    },
    methods: {
        stopAgent: function(e) {

            if (!this.selectedAgent || this.selectedAgent === "") {
                alert("Please select agent to stop")
                return
            }

            axios.delete(API + '/agents/running', 
                {data: this.selectedAgent },
                { headers: { "Content-Type": "application/json" } } )
                .then( res => console.log('Agent successfully stopped'))
                .catch( err => console.log(err))
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


