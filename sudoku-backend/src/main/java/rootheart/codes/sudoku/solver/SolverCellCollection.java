package rootheart.codes.sudoku.solver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import rootheart.codes.sudoku.game.Cell;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class SolverCellCollection {
    private final Set<SolverCell> cells = new HashSet<>();

    private Set<SolverCell> emptyCells;
    private IntSet numbers;

    public SolverCellCollection(Set<SolverCell> cells) {
        this.cells.addAll(cells);
    }

    public void initializationComplete() {
        emptyCells = cells.stream()
                .filter(SolverCell::isEmpty)
                .collect(Collectors.toSet());
        numbers = IntSets.immutable.ofAll(cells.stream().map(SolverCell::getCell).mapToInt(Cell::getNumber));
    }

    public void removeCandidates(IntSet candidates) {
        cells.forEach(otherCell -> otherCell.getCandidates().removeAll(candidates));
    }

    public void removeCandidate(int candidate) {
        cells.forEach(otherCell -> otherCell.getCandidates().remove(candidate));
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
}

