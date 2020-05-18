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

    private final Map<Cell, SolverCell> solverCellMap;
    private final Set<SolverCell> singleCandidates = new HashSet<>();

    public SolverBoard(Board board) {
        solverCellMap = board.streamCells()
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
    }

    public void eliminateImpossibleCandidates() {
        long countBefore = countCellsWithSingleCandidate();
        while (true) {
            solverCellMap.values().forEach(SolverCell::eliminateImpossibleCandidates);
            long countAfter = countCellsWithSingleCandidate();
            if (countAfter == 0 || countBefore == countAfter) {
                findNakedSingles();
                return;
            }
            countBefore = countAfter;
        }
    }

    private long countCellsWithSingleCandidate() {
        return solverCellMap.values()
                .stream()
                .filter(SolverCell::hasOneCandidate)
                .count();
    }

    public boolean isNotSolvable() {
        return solverCellMap.values().stream()
                .anyMatch(entry -> entry.isEmpty() && entry.getCandidates().size() == 0);
    }

    private void findNakedSingles() {
        solverCellMap.values()
                .stream()
                .filter(SolverCell::hasOneCandidate)
                .forEach(singleCandidates::add);
    }
}
