package rootheart.codes.sudoku.game;

import lombok.Getter;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class Board extends CellList {
    private int size;
    private int maxValue;
    private Group[] columns;
    private Group[] rows;
    private Group[] blocks;
    private IntSet possibleValues;

    public Board(String board) {
        super(board.length());
        set(board);
    }

    public void set(String board) {
        if (board.length() == 16) {
            size = 2;
        } else if (board.length() == 81) {
            size = 3;
        } else {
            throw new IllegalArgumentException(board);
        }
        maxValue = size * size;
        columns = new Group[maxValue];
        rows = new Group[maxValue];
        blocks = new Group[maxValue];
        for (int i = 0; i < maxValue; i++) {
            columns[i] = new Group(this);
            rows[i] = new Group(this);
            blocks[i] = new Group(this);
        }

        possibleValues = IntSets.immutable.ofAll(IntStream.rangeClosed(1, maxValue));

        createCells();
        initCells(board);
    }

    private void createCells() {
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                int blockIndex = (rowIndex / size) * size + (columnIndex / size);
                int blockCellIndex = columnIndex % size + rowIndex % size * size;
                Cell cell = new Cell(columns[columnIndex], rows[rowIndex], blocks[blockIndex]);
                columns[columnIndex].setCell(rowIndex, cell);
                rows[rowIndex].setCell(columnIndex, cell);
                blocks[blockIndex].setCell(blockCellIndex, cell);
                setCell(rowIndex * maxValue + columnIndex, cell);
            }
        }
    }

    private void initCells(String board) {
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                int number = Character.getNumericValue(board.charAt(columnIndex + rowIndex * maxValue));
                columns[columnIndex].getCell(rowIndex).setNumber(number);
            }
        }
    }

    public Group getColumn(int columnIndex) {
        return columns[columnIndex];
    }

    public Group getRow(int rowIndex) {
        return rows[rowIndex];
    }

    public Group getBlock(int blockIndex) {
        return blocks[blockIndex];
    }

    public Cell cell(int column, int row) {
        return columns[column].getCell(row);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                sb.append(cell(columnIndex, rowIndex).getNumber());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getBoardString() {
        return streamCells()
                .map(Cell::getNumber)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public boolean isValid() {
        return Arrays.stream(columns).allMatch(Group::isValid)
                && Arrays.stream(rows).allMatch(Group::isValid)
                && Arrays.stream(blocks).allMatch(Group::isValid);
    }
}
