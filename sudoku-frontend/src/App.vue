<template>
    <div id="app">
        <Board></Board>
    </div>
</template>

<script>
    import Board from "./components/Board";

    export default {
        name: 'App',
        components: {
            Board
        }
    }
</script>

<style>
    #app {
        font-family: Avenir, Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
    }

    body {
        margin: 0;
    }
</style>
