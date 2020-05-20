package rootheart.codes.sudoku.solver;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class SolverCellCollection {
//    private final List<SolverCell> cells = new ArrayList<>(10);
//    private final NumberSet candidates = new NumberSet();
//
//    public void removeCandidate(int candidate) {
//        cells.forEach(cell -> cell.getCandidates().remove(candidate));
//    }
//
//    public void add(SolverCell cell) {
//        cells.add(cell);
//    }
//
//    public void updateCandidates() {
//        candidates.clear();
//        for (SolverCell cell : cells) {
//            candidates.addAll(cell.getCandidates());
//        }
//    }
//
//    public SolverCell findExactlyOneCellWithCandidates(NumberSet candidates) {
//        SolverCell twin = null;
//        for (SolverCell cell : cells) {
//            if (cell.getCandidates().equals(candidates)) {
//                if (twin != null) {
//                    throw new NoSolutionException("more than two cells only allow the same two numbers, this is not possible");
//                }
//                twin = cell;
//            }
//        }
//        return twin;
//    }
}
