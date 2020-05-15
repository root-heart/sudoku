<template>
    <form @submit.prevent="validateGame">
        <table class="board"
               @keypress="enterDigit"
               tabindex="-1">
            <tr v-for="(rowNumber, row) in 9" :class="'row' + rowNumber" :key="'row' + rowNumber">
                <td v-for="(columnNumber, column) in 9"
                    :key="'column' + columnNumber"
                    :class="['column' + columnNumber,
                                selected.row === row && selected.column === column ? 'selected' : '']"
                    @click="selectField(column, row)">
                    <span v-if="board[column][row] > 0">
                        {{board[column][row]}}
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
            let board = new Array(9);
            for (let column = 0; column < 9; column++) {
                board[column] = new Array(9);
                for (let row = 0; row < 9; row++) {
                    board[column][row] = 0;
                }
            }
            return {
                board: board,
                selected: {row: -1, column: -1}
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
                    this.board[this.selected.column][this.selected.row] = event.key;
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