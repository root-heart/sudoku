package codes.rootheart.sudoku.game;

import lombok.Getter;

@Getter
public class Board {
    private final int size;
    private final Group[] columns;
    private final Group[] rows;
    private final Group[] blocks;
    private final int unit;

    public Board(String board) {
        if (board.length() == 16) {
            size = 2;
        } else if (board.length() == 81) {
            size = 3;
        } else {
            throw new IllegalArgumentException(board);
        }
        unit = size * size;
        columns = new Group[unit];
        rows = new Group[unit];
        blocks = new Group[unit];
        for (int i = 0; i < unit; i++) {
            columns[i] = new Group(this);
            rows[i] = new Group(this);
            blocks[i] = new Group(this);
        }

        createCells();
        initCells(board);
    }

    private void createCells() {
        for (int columnIndex = 0; columnIndex < unit; columnIndex++) {
            for (int rowIndex = 0; rowIndex < unit; rowIndex++) {
                int blockIndex = (rowIndex / size) * size + (columnIndex / size);
                int blockCellIndex = columnIndex % size + rowIndex % size * size;

                Cell cell = new Cell(columns[columnIndex], rows[rowIndex], blocks[blockIndex], unit);
                columns[columnIndex].setCell(rowIndex, cell);
                rows[rowIndex].setCell(columnIndex, cell);
                blocks[blockIndex].setCell(blockCellIndex, cell);
            }
        }
    }

    private void initCells(String board) {
        for (int columnIndex = 0; columnIndex < unit; columnIndex++) {
            for (int rowIndex = 0; rowIndex < unit; rowIndex++) {
                int number = Character.getNumericValue(board.charAt(columnIndex + rowIndex * unit));
                columns[columnIndex].getCell(rowIndex).setNumber(number);
            }
        }
    }

    public Group getColumn(int column) {
        return columns[column];
    }

    public Group getRow(int column) {
        return rows[column];
    }

    public Group getBlock(int column) {
        return blocks[column];
    }

    public Cell cell(int column, int row) {
        return columns[column].getCell(row);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
