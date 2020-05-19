package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class SolverBoard {

    private final Set<SolverCell> singleCandidates = new HashSet<>();
    private final List<SolverCell> emptyCells;

    public SolverBoard(Board board) {
        Map<Cell, SolverCell> solverCellMap = board.streamCells()
                .map(cell -> new SolverCell(cell, board))
                .collect(Collectors.toMap(SolverCell::getCell, Function.identity()));
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
        emptyCells = solverCellMap.values().stream().filter(SolverCell::isEmpty).collect(Collectors.toList());
    }

    public void eliminateImpossibleCandidates() {
        for (long countBefore = singleCandidates.size(); ; ) {
            for (SolverCell cell : emptyCells) {
                cell.eliminateImpossibleCandidates();
                if (cell.getCandidates().hasOneNumber()) {
                    singleCandidates.add(cell);
                }
            }
            long countAfter = singleCandidates.size();
            if (countAfter == 0 || countBefore == countAfter) {
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
