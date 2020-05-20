package rootheart.codes.sudoku.game;

import lombok.Getter;
import rootheart.codes.sudoku.solver.NoSolutionException;
import rootheart.codes.sudoku.solver.NumberSet;

import java.util.ArrayList;
import java.util.List;

@Getter
class CellList {
    private final List<Cell> cells;
    private final NumberSet candidates = new NumberSet();

    CellList(int size) {
        cells = new ArrayList<>(size);
    }

    public void removeCandidate(int candidate) {
        cells.forEach(cell -> cell.getCandidates().remove(candidate));
    }

    public void add(Cell cell) {
        cells.add(cell);
    }

    public void updateCandidates() {
        candidates.clear();
        for (Cell cell : cells) {
            candidates.addAll(cell.getCandidates());
        }
    }

    public Cell findExactlyOneCellWithCandidates(NumberSet candidates) {
        Cell twin = null;
        for (Cell cell : cells) {
            if (cell.getCandidates().equals(candidates)) {
                if (twin != null) {
                    throw new NoSolutionException("more than two cells only allow the same two numbers, this is not possible");
                }
                twin = cell;
            }
        }
        return twin;
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }
}
