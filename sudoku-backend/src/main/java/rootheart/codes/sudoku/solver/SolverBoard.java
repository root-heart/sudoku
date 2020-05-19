package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class SolverBoard {

    private final Set<SolverCell> singleCandidates = new HashSet<>();
    private final Set<SolverCell> emptyCells;

    public SolverBoard(Board board) {
        Map<Cell, SolverCell> solverCellMap = board.streamCells()
                .map(cell -> new SolverCell(cell, board))
                .collect(Collectors.toMap(SolverCell::getCell, Function.identity()));
        solverCellMap.forEach((cell, solverCell) -> {
            cell.getColumn().streamCells()
                    .filter(otherCell -> cell != otherCell)
                    .map(solverCellMap::get)
                    .forEach(c -> solverCell.getOtherCellsInColumn().add(c));
            cell.getRow().streamCells()
                    .filter(otherCell -> cell != otherCell)
                    .map(solverCellMap::get)
                    .forEach(c -> solverCell.getOtherCellsInRow().add(c));
            cell.getBlock().streamCells()
                    .filter(otherCell -> cell != otherCell)
                    .map(solverCellMap::get)
                    .forEach(c -> solverCell.getOtherCellsInBlock().add(c));
        });
        solverCellMap.values().forEach(SolverCell::initializationComplete);
        emptyCells = solverCellMap.values().stream().filter(SolverCell::isEmpty).collect(Collectors.toSet());
    }

    public void eliminateImpossibleCandidates() {
        for (long countBefore = singleCandidates.size(); ; ) {
            for (SolverCell cell : emptyCells) {
                cell.eliminateImpossibleCandidates();
                if (cell.hasOneCandidate()) {
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
                .anyMatch(entry -> entry.getCandidateCount() == 0);
    }
}
