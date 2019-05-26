<template>
    <div class="agentTypes">
        <div>Run agent</div>
        <form id="form" v-on:submit="runAgent">
            <table>
                <tbody>
                    <tr>
                        <td>
                            <select class="custom-select my-1 mr-sm-2" v-model="selectedType">
                                <option value="" disabled selected>Choose agent type</option>
                                <option v-for="type in types" :value="type.module + '.' + type.name" v-bind:key="type.name">{{type.name}}</option>
                            </select>
                        </td>
                        <td><input type="text" v-model="name" class="form-control" id="name" placeholder="Agent name"></td>
                    </tr>
                    <br />
                    <tr>
                        <td>&nbsp;</td>
                        <td>
                            <button type="submit" :disabled="!btnEnabled" class="btn btn-lg btn-success">Run</button> 
                        </td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</template>

<script>

import axios from 'axios'
import { API } from '../variables.js'

export default {
    name : 'runAgentSlot',
    components: {

    },
    data() {
        return {
            types: [],
            selectedType: "",
            name: "",
            btnEnabled: true
        }
    },
    created() {

        axios.get(API + '/agents/classes')
            .then(res => this.types = res.data)
            .catch(err => console.log(err))
    },
    methods: {
        runAgent: function(e) {
            
            var type = this.selectedType
            var name = this.name

            if (!type || type === "") {
                alert("Please select agent type")
                return
            }

            if (!name || name === "") {
                alert("Please enter agent name")
                return
            }

            this.btnEnabled = false

            axios.put(API + '/agents/running/' + type + '/' + name)
                .then(res => {
                    console.log('Agent successfully ran')

                    //this.$emit('new-agent')

                    this.name = ""
                    this.selectedType = ""
                })
                .catch( err => {
                    console.log(err)

                    if (err.response) {
                        alert(err.response.data)
                    }
                    
                })
                .finally ( this.btnEnabled = true )
        },
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


