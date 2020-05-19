package rootheart.codes.sudoku.game;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

class CellList {
    @Getter
    private final List<Cell> cells;

    public CellList(int size) {
        cells = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            cells.add(null);
        }
    }

    public Stream<Cell> streamEmptyCells() {
        return cells.stream().filter(Cell::isEmpty);
    }

    public Stream<Cell> streamCells() {
        return cells.stream();
    }

    public boolean hasEmptyCells() {
        return streamEmptyCells().findFirst().isPresent();
    }

    public void forEach(Consumer<Cell> consumer) {
        cells.forEach(consumer);
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }

    public void setCell(int index, Cell cell) {
        cells.set(index, cell);
    }

    public int indexOf(Cell cell) {
        return cells.indexOf(cell);
    }
}
