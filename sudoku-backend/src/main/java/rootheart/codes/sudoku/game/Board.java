package rootheart.codes.sudoku.game;

import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class Board {
    private final int size;
    private final int maxValue;
    private final Group[] columns;
    private final Group[] rows;
    private final Group[] blocks;
    private final Cell[] cells;
    private final Set<Integer> possibleValues;

    public Board(String board) {
        if (board.length() == 16) {
            size = 2;
        } else if (board.length() == 81) {
            size = 3;
        } else {
            throw new IllegalArgumentException(board);
        }
        maxValue = size * size;
        cells = new Cell[board.length()];
        columns = new Group[maxValue];
        rows = new Group[maxValue];
        blocks = new Group[maxValue];
        for (int i = 0; i < maxValue; i++) {
            columns[i] = new Group(this);
            rows[i] = new Group(this);
            blocks[i] = new Group(this);
        }

        possibleValues = IntStream.rangeClosed(1, maxValue).boxed().collect(Collectors.toSet());

        createCells();
        initCells(board);
    }

    private void createCells() {
        for (int i = 0; i < cells.length; i++) {
            int columnIndex = i % maxValue;
            int rowIndex = i / maxValue;
            int blockIndex = (rowIndex / size) * size + (columnIndex / size);
            int blockCellIndex = columnIndex % size + rowIndex % size * size;
            Cell cell = new Cell(columns[columnIndex], rows[rowIndex], blocks[blockIndex]);
            columns[columnIndex].setCell(rowIndex, cell);
            rows[rowIndex].setCell(columnIndex, cell);
            blocks[blockIndex].setCell(blockCellIndex, cell);
            cells[i] = cell;
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

    public boolean hasEmptyCells() {
        for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
            for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
                if (cell(columnIndex, rowIndex).getNumber() == 0) {
                    return true;
                }
            }
        }
        return false;
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
}
