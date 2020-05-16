package codes.rootheart.sudoku.solver;

import codes.rootheart.sudoku.game.Board;
import codes.rootheart.sudoku.game.Cell;

public class Solver {
    public void solve(Board board) {
        System.out.println(board);
        while (board.hasEmptyCells()) {
            boolean boardChanged = false;
            for (int column = 0; column < board.getUnit(); column++) {
                for (int row = 0; row < board.getUnit(); row++) {
                    Cell cell = board.cell(column, row);
                    if (cell.getNumber() == 0) {
                        System.out.println(String.format("Cell %d:%d - possible values %s", column, row, cell.getPossibleValues()));
                        if (cell.getPossibleValues().size() == 1) {
                            Integer number = cell.getPossibleValues().iterator().next();
                            cell.setNumber(number);
                            boardChanged = true;
                            System.out.println(String.format("Set %d", number));
                        }
                    }
                }
            }
            if (!boardChanged) {
                throw new IllegalArgumentException("not solvable " + board);
            }
        }
    }
}
