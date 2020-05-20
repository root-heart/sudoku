package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// XXX idea: consider this solver board a cube with the fixed numbers on layer 0. layer 1 is where 1 is possible etc.
// Would that make things easier? Faster? Better readable?
@Getter
public class SolverBoard {

    private final Set<SolverCell> singleCandidates = new HashSet<>();
    private final List<SolverCell> emptyCells;

    public SolverBoard(Board board) {
        emptyCells = new ArrayList<>(board.getCells().size());
        for (Cell cell : board.getCells()) {
            if (cell.isEmpty()) {
                SolverCell solverCell = new SolverCell(cell, board);
                emptyCells.add(solverCell);
            }
        }
        for (SolverCell solverCell : emptyCells) {
            solverCell.updateBuddyCells();
        }
    }


    public void eliminateImpossibleCandidates() {
        for (int countBefore = singleCandidates.size(); ; ) {
            for (SolverCell cell : emptyCells) {
                if (!singleCandidates.contains(cell)) {
                    cell.eliminateImpossibleCandidates();
                    if (cell.getCandidates().hasOneNumber()) {
                        singleCandidates.add(cell);
                    }
                }
            }
            int countAfter = singleCandidates.size();
            if (countAfter == 0 || countBefore == countAfter || countAfter == emptyCells.size()) {
                return;
            }
            countBefore = countAfter;
        }
    }

    public boolean isNotSolvable() {
        return emptyCells.stream()
                .anyMatch(entry -> entry.getCandidates().getCount() == 0);
    }
}
