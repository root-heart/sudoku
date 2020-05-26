package rootheart.codes.sudoku.solver.binaryoptimized;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Board {
    static final int[] BLOCK_INDEX = new int[81];
    static final int[] CELL_INDEX_IN_BLOCK = new int[81];

    static final int[] columnIndexMap = new int[81];
    static final int[] rowIndexMap = new int[81];

    static {
        for (int cellIndex, rowIndex = cellIndex = 0; rowIndex < 9; rowIndex++) {
            for (int columnIndex = 0; columnIndex < 9; columnIndex++, cellIndex++) {
                BLOCK_INDEX[cellIndex] = (rowIndex / 3) * 3 + (columnIndex / 3);
                CELL_INDEX_IN_BLOCK[cellIndex] = (rowIndex % 3) * 3 + columnIndex % 3;
            }
        }

        for (int i = 0; i < 81; i++) {
            int columnIndex = i % 9;
            int rowIndex = i / 9;
            int blockIndex = (rowIndex / 3) * 3 + (columnIndex / 3);
            int blockCellIndex = (rowIndex % 3) * 3 + columnIndex % 3;
            columnIndexMap[i] = columnIndex;
            rowIndexMap[i] = rowIndex;
        }
    }

    static long count_getColumnIndex = 0;

    private static int getColumnIndex(int cellIndex) {
        count_getColumnIndex++;
        return columnIndexMap[cellIndex];
    }

    static long count_getRowIndex = 0;

    private static int getRowIndex(int cellIndex) {
        count_getRowIndex++;
        return rowIndexMap[cellIndex];
    }

    static long count_getBlockIndex = 0;

    private static int getBlockIndex(int cellIndex) {
        count_getBlockIndex++;
        return BLOCK_INDEX[cellIndex];
    }

    static long count_getBlockCellIndex = 0;

    private static int getBlockCellIndex(int cellIndex) {
        count_getBlockCellIndex++;
        return CELL_INDEX_IN_BLOCK[cellIndex];
    }

    private final int[] cells = new int[81];
    private final int[] rows = new int[9];
    private final int[] columns = new int[9];
    private final int[] blocks = new int[9];
    int emptyCellCount = 81;

    public Board(String puzzle) {
        clear();
        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
            int number = Character.getNumericValue(puzzle.charAt(cellIndex));
            if (number > 0) {
                setZeroBasedNumberToCell(cellIndex, number - 1);
            }
        }
    }

    public void clear() {
        for (int i = 0; i < 81; i++) {
            cells[i] = -1;
        }
        for (int i = 0; i < 9; i++) {
            columns[i] = 0;
            rows[i] = 0;
            blocks[i] = 0;
        }
        emptyCellCount = 81;
    }

    static long count_setZeroBasedNumberToCell = 0;

    public void setZeroBasedNumberToCell(int cellIndex, int number) {
        count_setZeroBasedNumberToCell++;
        cells[cellIndex] = number;
        int columnIndex = getColumnIndex(cellIndex);
        int rowIndex = getRowIndex(cellIndex);
        int blockIndex = getBlockIndex(cellIndex);
        if (number != -1) {
            int binaryEncodedNumber = 1 << number;
            columns[columnIndex] |= binaryEncodedNumber;
            rows[rowIndex] |= binaryEncodedNumber;
            blocks[blockIndex] |= binaryEncodedNumber;
            emptyCellCount--;
        }
    }

    static long count_clearCell = 0;

    public void clearCell(int cellIndex) {
        count_clearCell++;
        emptyCellCount++;
        int number = cells[cellIndex];
        int bit = 1 << number;
        cells[cellIndex] = -1;
        int columnIndex = getColumnIndex(cellIndex);
        int rowIndex = getRowIndex(cellIndex);
        int blockIndex = getBlockIndex(cellIndex);
        columns[columnIndex] &= ~bit;
        rows[rowIndex] &= ~bit;
        blocks[blockIndex] &= ~bit;
    }

    public boolean cellIsEmpty(int cellIndex) {
        return cells[cellIndex] == -1;
    }

    public boolean cellIs(int cellIndex, int number) {
        return cells[cellIndex] == number;
    }

    static long count_numberIsInvalidForCell = 0;

    public boolean numberIsInvalidForCell(int cellIndex, int zeroBasedNumber) {
        count_numberIsInvalidForCell++;
        int numbersSetInBuddyCells = getBinaryEncodedSetNumbersInBuddyCells(cellIndex);
        return (numbersSetInBuddyCells & 1 << zeroBasedNumber) != 0;
    }

    public String asString() {
        return Arrays.stream(cells).mapToObj(n -> String.valueOf(n + 1)).collect(Collectors.joining());
    }

    static long count_getBinaryEncodedCandidates = 0;

    public int getBinaryEncodedCandidates(int cellIndex) {
        count_getBinaryEncodedCandidates++;
        return getBinaryEncodedSetNumbersInBuddyCells(cellIndex) ^ 0x1FF;
    }

    static long count_getBinaryEncodedSetNumbersInBuddyCells = 0;
    static long count_getBinaryEncodedSetNumbersInBuddyCells_cacheHit = 0;

    private int getBinaryEncodedSetNumbersInBuddyCells(int cellIndex) {
        count_getBinaryEncodedSetNumbersInBuddyCells++;
        return columns[getColumnIndex(cellIndex)] | rows[getRowIndex(cellIndex)] | blocks[getBlockIndex(cellIndex)];
    }
}
