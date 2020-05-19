package rootheart.codes.sudoku.solver;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class SolverCellCollection {
    private final List<SolverCell> emptyCells = new ArrayList<>();

    public void removeCandidates(SolverCell solverCell) {
        emptyCells.forEach(cell -> cell.getCandidates().removeAll(solverCell.getCandidates()));
    }

    public void removeCandidate(int candidate) {
        emptyCells.forEach(cell -> cell.getCandidates().remove(candidate));
    }

    public void add(SolverCell cell) {
        if (cell.isEmpty()) {
            emptyCells.add(cell);
        }
    }

    public boolean noCellContainsCandidate(int candidate) {
        NumberSet n = new NumberSet();
        n.add(candidate);
        for (SolverCell cell : emptyCells) {
            n.removeAll(cell.getCandidates());
        }
        return n.contains(candidate);
    }
}
