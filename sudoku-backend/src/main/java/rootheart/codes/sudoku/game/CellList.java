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

    public void add(Cell cell) {
        cells.add(cell);
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }
}
