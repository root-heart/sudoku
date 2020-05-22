package rootheart.codes.sudoku.game;

import lombok.Getter;
import rootheart.codes.sudoku.solver.BoardInvalidException;
import rootheart.codes.sudoku.solver.NumberSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

@Getter
public class Group {
    private final List<Cell> cells;
    private final NumberSet values = new NumberSet();

    public Group(int cellCount) {
        cells = new ArrayList<>(cellCount);
    }

    public void add(Cell cell) {
        if (cell.getNumber() != 0 && values.contains(cell.getNumber())) {
            throw new BoardInvalidException();
        }
        cells.add(cell);
        values.add(cell.getNumber());
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        getCells().forEach(cell -> sb.append(cell.getNumber()));
        return sb.toString();
    }

    public boolean isValid() {
        BitSet b = new BitSet();
        return getCells().stream()
                .filter(c -> !c.isEmpty())
                .allMatch(c -> {
                    if (b.get(c.getNumber())) {
                        return false;
                    }
                    b.set(c.getNumber());
                    return true;
                });
    }
}
