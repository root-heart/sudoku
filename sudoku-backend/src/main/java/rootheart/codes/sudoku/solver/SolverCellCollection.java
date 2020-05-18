package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Cell;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SolverCellCollection {
    private final Set<SolverCell> otherCells = new HashSet<>();


    public void removeCandidates(Set<Integer> candidates) {
        otherCells.forEach(otherCell -> otherCell.getCandidates().removeAll(candidates));

    }

    public Stream<SolverCell> streamEmptyCellsWhere(Predicate<SolverCell> filter) {
        return otherCells.stream()
                .filter(SolverCell::isEmpty)
                .filter(filter);
    }

    public Stream<Integer> streamSetNumbers() {
        return otherCells.stream().map(SolverCell::getCell).map(Cell::getNumber);
    }

    public void add(SolverCell solverCell) {
        otherCells.add(solverCell);
    }


    public boolean anyCellContainsCandidate(Integer candidate) {
        return otherCells
                .stream()
                .anyMatch(otherCell -> otherCell.getCandidates().contains(candidate));
    }

}

