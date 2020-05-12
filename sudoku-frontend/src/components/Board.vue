<template>
    <div class="board">
        <Block></Block>
        <Block></Block>
        <Block></Block>
        <Block></Block>
        <Block></Block>
        <Block></Block>
        <Block></Block>
        <Block></Block>
        <Block></Block>
    </div>
</template>

<script>
    import Block from "./Block";

    export default {
        name: "Board",
        components: {Block}
    }
</script>

<style scoped>
.board {
    display: grid;
    grid-template-columns: auto auto auto;
    grid-template-rows: auto auto auto;
}
</style>