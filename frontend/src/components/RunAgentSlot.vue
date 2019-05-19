<template>
    <div class="agentTypes">
        <div>Run agent</div>
        <table>
            <tbody>
                <tr>
                    <td>
                        <select class="custom-select my-1 mr-sm-2" v-model="selectedType">
                            <option v-for="type in types" :value="option" v-bind:key="type.name">{{type.name}}</option>
                        </select>
                    </td>
                    <td><input type="name" class="form-control" id="name" placeholder="Agent name"></td>
                </tr>
                <br />
                <tr>
                    <td>&nbsp;</td>
                    <td>
                        <button type="submit" class="btn btn-primary">Run</button> 
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
    name : 'runAgentSlot',
    components: {

    },
    data() {
        return {
            types: [],
            selectedType: "",
            option: "",
        }
    },
    created() {

        axios.get(API + '/agents/classes')
            .then(res => this.types = res.data)
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


