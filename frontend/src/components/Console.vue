<template>
    <div class="console">
        <span id="consoleTitle">Console output</span>
        <b-form-textarea
            id="textarea"
            v-model="text"
            plaintext
        ></b-form-textarea>
    </div>
</template>

<script>
export default {
    name: 'Console',
    data() {
      return {
        text: ""
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


