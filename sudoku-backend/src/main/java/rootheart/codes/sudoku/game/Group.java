package rootheart.codes.sudoku.game;

import lombok.Getter;
import rootheart.codes.sudoku.solver.NumberSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

@Getter
public class Group {
    private final List<Cell> cells;

    public Group(int cellCount) {
        cells = new ArrayList<>(cellCount);
    }

    public void add(Cell cell) {
        cells.add(cell);
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

    public void removeCellsCandidatesFromBuddyCells() {
        NumberSet numbers = new NumberSet();
        for (Cell cell : cells) {
            numbers.add(cell.getNumber());
        }
        for (Cell cell : cells) {
            cell.getCandidates().removeAll(numbers);
        }
    }
}
