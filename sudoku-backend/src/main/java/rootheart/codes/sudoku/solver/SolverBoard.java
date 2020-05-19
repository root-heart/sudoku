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

@Getter
public class SolverBoard {

    private final Set<SolverCell> singleCandidates = new HashSet<>();
    private final List<SolverCell> emptyCells = new ArrayList<>();

    public SolverBoard(Board board) {
        Map<Cell, SolverCell> solverCellMap = new HashMap<>();
        for (Cell cell : board.getCells()) {
            SolverCell solverCell = new SolverCell(cell, board);
            solverCellMap.put(cell, solverCell);
            if (cell.isEmpty()) {
                emptyCells.add(solverCell);
            }
        }
        solverCellMap.forEach((cell, solverCell) -> {
            for (Cell columnCell : cell.getColumn().getCells()) {
                if (columnCell != cell) {
                    solverCell.addOtherCellInColumn(solverCellMap.get(columnCell));
                }
            }
            for (Cell rowCell : cell.getRow().getCells()) {
                if (rowCell != cell) {
                    solverCell.addOtherCellInRow(solverCellMap.get(rowCell));
                }
            }
            for (Cell blockCell : cell.getBlock().getCells()) {
                if (blockCell != cell) {
                    solverCell.addOtherCellInBlock(solverCellMap.get(blockCell));
                }
            }
        });
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
