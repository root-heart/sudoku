package rootheart.codes.sudoku.game;

import lombok.Getter;
import rootheart.codes.sudoku.solver.NoSolutionException;
import rootheart.codes.sudoku.solver.NumberSet;
import rootheart.codes.sudoku.solver.SolverCell;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

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

    public boolean hasEmptyCells() {
        return cells.stream().anyMatch(Cell::isEmpty);
    }


    public Cell getCell(int index) {
        return cells.get(index);
    }

    public void setCell(int index, Cell cell) {
        cells.set(index, cell);
    }
}
