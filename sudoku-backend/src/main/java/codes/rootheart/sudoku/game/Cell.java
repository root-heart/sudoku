package codes.rootheart.sudoku.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class Cell {
    private final Group column;
    private final Group row;
    private final Group block;
    private final int unit;
    private int number;
    private final Set<Integer> possibleValues;

    public Cell(Group column, Group row, Group block, int unit) {
        this.column = column;
        this.row = row;
        this.block = block;
        this.unit = unit;
        possibleValues = new HashSet<>();
        for (int i = 1; i <= unit; i++) {
            possibleValues.add(i);
        }
    }

    public void setNumber(int newNumber) {
        column.forAllCells(cell -> updatePossibleValues(cell, newNumber));
        row.forAllCells(cell -> updatePossibleValues(cell, newNumber));
        block.forAllCells(cell -> updatePossibleValues(cell, newNumber));
        this.number = newNumber;
    }

    private void updatePossibleValues(Cell cell, int newNumber) {
        if (number != 0) {
            cell.possibleValues.add(number);
        }
        cell.possibleValues.remove(newNumber);
    }
}
