package rootheart.codes.sudoku.generator;

import rootheart.codes.sudoku.game.Board;

public class BoardGenerator {
    public Board generate() {
        Board board = Board.of("0".repeat(81));



        return board;
    }

}
