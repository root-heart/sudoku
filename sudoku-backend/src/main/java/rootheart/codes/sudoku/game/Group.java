package rootheart.codes.sudoku.game;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class Group {
    private final List<Cell> cells;

    public Group(Board board) {
        this.cells = new ArrayList<>(board.getMaxValue());
        for (int i = 0; i < board.getMaxValue(); i++) {
            cells.add(null);
        }
    }

    public void forAllCells(Consumer<Cell> consumer) {
        cells.forEach(consumer);
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }

    public void setCell(int index, Cell cell) {
        cells.set(index, cell);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        forAllCells(cell -> sb.append(cell.getNumber()));
        return sb.toString();
    }
}
