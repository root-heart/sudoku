package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SolverBoard {

    private final Map<Cell, SolverCell> solverCellMap;

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
    }

    public void eliminateCandidatesThatAreSetInBuddyCells() {
        solverCellMap.values().forEach(SolverCell::eliminateCandidatesThatAreSetInBuddyCells);
    }

    public void eliminateLockedCandidates() {
        solverCellMap.values().forEach(SolverCell::eliminateLockedCandidates);
    }

    public void eliminateNakedTwins() {
        solverCellMap.values().forEach(SolverCell::eliminateNakedTwins);
    }

    public boolean hasSolution() {
        return solverCellMap.entrySet().stream()
                .noneMatch(entry -> entry.getKey().isEmpty() && entry.getValue().getCandidates().size() == 0);
    }

    public Map<Cell, Integer> findNakedSingles() {
        return solverCellMap.values()
                .stream()
                .filter(SolverCell::hasOneCandidate)
                .collect(Collectors.toMap(SolverCell::getCell, SolverCell::getFirstCandidate));
    }

    public Map<Cell, Integer> findHiddenSingles() {
        Map<Cell, Integer> hiddenSingles = new HashMap<>();
        for (SolverCell solverCell : solverCellMap.values()) {
            Integer hiddenSingle = solverCell.findHiddenSingle();
            if (hiddenSingle != null) {
                hiddenSingles.put(solverCell.getCell(), hiddenSingle);
            }
        }
        return hiddenSingles;
    }
}
