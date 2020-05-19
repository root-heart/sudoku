package rootheart.codes.sudoku.solver;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@NoArgsConstructor
@Getter
public class SolverCellCollection {
    private final List<SolverCell> emptyCells = new ArrayList<>();
//    private final MutableIntSet numbers = IntSets.mutable.empty();
    private int numbers;

    public void removeCandidates(SolverCell solverCell) {
        emptyCells.forEach(cell -> cell.removeCandidates(solverCell));
    }

    public void removeCandidate(int candidate) {
        emptyCells.forEach(cell -> cell.removeCandidate(candidate));
    }

    public void add(SolverCell cell) {
        if (cell.isEmpty()) {
            emptyCells.add(cell);
        } else {
//            numbers.add(cell.getCell().getNumber());
            numbers |= 1 << cell.getCell().getNumber();
        }
    }

    public boolean noCellContainsCandidate(int candidate) {
        for (SolverCell cell : emptyCells) {
            if (cell.containsCandidate(candidate)) {
                return false;
            }
        }
        return true;
    }

    public SolverCellCollection createNewWithFilteredEmptyCells(Predicate<SolverCell> filter) {
        SolverCellCollection collection = new SolverCellCollection();
        for (SolverCell cell : emptyCells) {
            if (filter.test(cell)) {
                collection.add(cell);
            }
        }
        return collection;
    }
}
