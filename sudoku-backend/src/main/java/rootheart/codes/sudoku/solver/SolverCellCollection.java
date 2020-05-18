package rootheart.codes.sudoku.solver;

import org.eclipse.collections.api.set.primitive.IntSet;
import rootheart.codes.sudoku.game.Cell;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SolverCellCollection {
    private final Set<SolverCell> otherCells = new HashSet<>();

    public void removeCandidates(IntSet candidates) {
        otherCells.forEach(otherCell -> otherCell.getCandidates().removeAll(candidates));
    }

    public Stream<SolverCell> streamEmptyCellsWhere(Predicate<SolverCell> filter) {
        return otherCells.stream()
                .filter(SolverCell::isEmpty)
                .filter(filter);
    }

    public Stream<Integer> streamNumbers() {
        return otherCells.stream().map(SolverCell::getCell).map(Cell::getNumber);
    }

    public void add(SolverCell solverCell) {
        otherCells.add(solverCell);
    }

    public boolean noCellContainsCandidate(Integer candidate) {
        return otherCells.stream()
                .noneMatch(otherCell -> otherCell.getCandidates().contains(candidate));
    }

    public Optional<SolverCell> findSingleCellWithCandidates(IntSet candidates) {
        return streamEmptyCellsWhere(c -> c.getCandidates().equals(candidates))
                .reduce((a, b) -> {
                    throw new NoSolutionException("more than two cells only allow the same two numbers, this is not possible");
                });
    }

    public Stream<SolverCell> streamAllOtherEmptyCells(SolverCell except) {
        return streamEmptyCellsWhere(cell -> cell != except);
    }
}

