package rootheart.codes.sudoku.generator;

import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;
import rootheart.codes.sudoku.solver.NotSolvableException;
import rootheart.codes.sudoku.solver.Solver;

public class BoardGenerator {
    public Board generate() {
        Solver solver = new Solver();
        Board board = new Board();
        while (true) {
            try {
                int columnIndex = (int) (Math.random() * 9);
                int rowIndex = (int) (Math.random() * 9);
                Cell cell = board.cell(columnIndex, rowIndex);
                if (cell.isEmpty()) {
                    cell.getCandidates().forEach(candidate -> {
                        cell.setNumber(candidate);
                        solver.solve(board);
                        break;
                    });
                }
            } catch (NotSolvableException e) {

            }
        }
        return board;
    }

}
