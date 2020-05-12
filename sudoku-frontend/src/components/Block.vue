<template>
    <div class="block">
        <div>1</div>
        <div>2</div>
        <div>3</div>
        <div>4</div>
        <div>5</div>
        <div>6</div>
        <div>7</div>
        <div>8</div>
        <div>9</div>
    </div>
</template>

<script>
    export default {
        name: "Block"
    }
</script>

<style scoped>
.block {
    display: grid;
    grid-template-columns: 20px 20px 20px;
    grid-template-rows: 20px 20px 20px;
    grid-gap: 4px;
}
</style>