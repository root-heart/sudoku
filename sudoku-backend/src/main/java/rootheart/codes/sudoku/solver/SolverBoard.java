package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.Collection;
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
    }

    public void calculate() {
        long countBefore = countCellsWithMultipleCandidates();
        while (true) {
            solverCellMap.values().forEach(SolverCell::eliminateImpossibilities);
            long countAfter = countCellsWithMultipleCandidates();
            if (countAfter == 0 || countBefore == countAfter) {
                findNakedSingles();
                return;
            }
            countBefore = countAfter;
        }
    }

    private long countCellsWithMultipleCandidates() {
        return solverCellMap.values()
                .stream()
                .map(SolverCell::getCandidates)
                .mapToInt(Collection::size)
                .filter(size -> size > 1)
                .count();
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

    public void revealHiddenSingles() {
        solverCellMap.values().forEach(SolverCell::revealHiddenSingle);
    }

    public boolean hasSolution() {
        return solverCellMap.values().stream()
                .noneMatch(entry -> entry.isEmpty() && entry.getCandidates().size() == 0);
    }

    public void findNakedSingles() {
        solverCellMap.values()
                .stream()
                .filter(SolverCell::hasOneCandidate)
                .forEach(singleCandidates::add);
    }
}
