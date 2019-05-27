<template>
    <div class="console">
        <span id="consoleTitle">Console output - <button v-on:click="clearConsole" class="btn btn-link">Clear</button> </span>
        <b-form-textarea
            id="textarea"
            v-model="consoleOutput"
            plaintext
        ></b-form-textarea>
    </div>
</template>

<script>
export default {
    name: 'Console',
    props: ["consoleOutput"],
    data() {
      return {
        text: ""
      }
    },
    methods: {
        clearConsole: function(e) {

            this.$emit("clearConsole")
        }
    }
}



</script>

<style scoped>

    .console {
	    display: flex;
    	flex-direction: column;
    }

    #textarea {
        height: 100%;
        display: flex;
		flex-direction: column;
        justify-content: center;
        flex: 1;
        padding: 0px 5px 5px 5px;
    }

    #consoleTitle {
        color: rgb(33, 82, 173);
    }


</style>


