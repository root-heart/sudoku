<template>
    <form @submit.prevent="validateGame">
        <table class="board"
               @keypress="enterDigit"
               tabindex="-1">
            <tr v-for="row in 9" :class="'row' + row" :key="row">
                <td v-for="column in 9"
                    :key="column"
                    :class="['column' + column,
                                selected.row === row && selected.column === column ? 'selected' : '']"
                    @click="selectField(column, row)">
                    <span v-if="board[row * 9 + column - 10] > 0">
                        {{board[row * 9 + column - 10]}}
                    </span>
                </td>
            </tr>
        </table>

        <div class="gameActions">
            <button @click="newGame">New Game</button>
            <button @click="validateGame">Validate Game</button>
        </div>
    </form>
</template>

<script>
    export default {
        name: "Board",
        data() {
            let board = [];
            for (let i = 0; i < 81; i++) {
                board.push(0);
            }
            board[21] = 3;
            return {
                board: board,
                selected: {row: 0, column: 0}
            }
        },
        methods: {
            newGame() {

            },
            validateGame() {

            },
            selectField(column, row) {
                this.selected.column = column;
                this.selected.row = row;
            },
            enterDigit(event) {
                if (event.key >= 1 && event.key <= 9) {
                    console.log(event);
                    this.board[this.selected.column + this.selected.row * 9 - 10] = event.key;
                    this.$forceUpdate();
                }
            }
        }
    }
</script>

<style scoped>
    .board {
        border: 3px solid #333;
        border-collapse: collapse;
        table-layout: fixed;
        width: calc(100vh / 11 * 9);
        font-size: calc(100vh / 11);
    }

    .board td {
        border: 1px solid #333;
        width: calc(100vh / 11);
        height: calc(100vh / 11);
    }

    .board .row3, .board .row6 {
        border-bottom: 2px solid #333;
    }

    .board .column3, .board .column6 {
        border-right: 2px solid #333;
    }

    .selected {
        background-color: beige;
    }

    form {
        display: grid;
        grid-template-columns: 70% 30%;
        box-sizing: border-box;
    }
</style>