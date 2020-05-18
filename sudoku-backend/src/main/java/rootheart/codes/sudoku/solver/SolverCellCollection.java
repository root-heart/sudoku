package rootheart.codes.sudoku.solver;

import lombok.NoArgsConstructor;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import rootheart.codes.sudoku.game.Cell;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class SolverCellCollection {
    private final Set<SolverCell> cells = new HashSet<>();

    private boolean fixed = false;
    private Set<SolverCell> emptyCells;
    private IntSet numbers;

    public SolverCellCollection(Set<SolverCell> cells) {
        this.cells.addAll(cells);
    }

    public void initializationComplete() {
        if (fixed) {
            return;
        }

        emptyCells = cells.stream()
                .filter(SolverCell::isEmpty)
                .collect(Collectors.toSet());
        numbers = IntSets.immutable.ofAll(cells.stream().map(SolverCell::getCell).mapToInt(Cell::getNumber));
        fixed = true;
    }

    public void removeCandidates(IntSet candidates) {
        cells.forEach(otherCell -> otherCell.getCandidates().removeAll(candidates));
    }

    public void removeCandidate(int candidate) {
        cells.forEach(otherCell -> otherCell.getCandidates().remove(candidate));
    }

    public Stream<SolverCell> streamEmptyCellsWhere(Predicate<SolverCell> filter) {
        return emptyCells.stream()
                .filter(filter);
    }

    public IntSet getNumbers() {
        return numbers;
    }

    public void add(SolverCell solverCell) {
        cells.add(solverCell);
    }

    public boolean noCellContainsCandidate(int candidate) {
        for (SolverCell otherCell : cells) {
            if (otherCell.getCandidates().contains(candidate)) {
                return false;
            }
        }
        return true;
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

