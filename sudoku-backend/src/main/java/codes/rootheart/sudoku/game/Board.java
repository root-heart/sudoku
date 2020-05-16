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
        for (int rowIndex = 0; rowIndex < unit; rowIndex++) {
            for (int columnIndex = 0; columnIndex < unit; columnIndex++) {
                int number = Character.getNumericValue(board.charAt(columnIndex + rowIndex * unit));
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
        for (int columnIndex = 0; columnIndex < unit; columnIndex++) {
            for (int rowIndex = 0; rowIndex < unit; rowIndex++) {
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
        for (int rowIndex = 0; rowIndex < unit; rowIndex++) {
            for (int columnIndex = 0; columnIndex < unit; columnIndex++) {
                sb.append(cell(columnIndex, rowIndex).getNumber());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
