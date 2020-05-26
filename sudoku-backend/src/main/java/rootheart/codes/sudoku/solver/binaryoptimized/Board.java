package rootheart.codes.sudoku.solver.binaryoptimized;

import java.util.Arrays;
import java.util.stream.Collectors;

class Board {
    static final int[] BLOCK_INDEX = new int[81];
    static final int[] CELL_INDEX_IN_BLOCK = new int[81];

    static final int[] indexMap = new int[81];

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
            indexMap[i] = columnIndex | rowIndex << 4 | blockIndex << 8 | blockCellIndex << 12;
        }
    }

    private static int getColumnIndex(int cellIndex) {
        return indexMap[cellIndex] & 0xF;
    }

    private static int getRowIndex(int cellIndex) {
        return indexMap[cellIndex] >> 4 & 0xF;
    }

    private static int getBlockIndex(int cellIndex) {
        return indexMap[cellIndex] >> 8 & 0xF;
    }

    private static int getBlockCellIndex(int cellIndex) {
        return indexMap[cellIndex] >> 12 & 0xF;
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

    public void setZeroBasedNumberToCell(int cellIndex, int number) {
        cells[cellIndex] = number;
        if (number != -1) {
            int binaryEncodedNumber = 1 << number;
            columns[getColumnIndex(cellIndex)] |= binaryEncodedNumber;
            rows[getRowIndex(cellIndex)] |= binaryEncodedNumber;
            blocks[getBlockIndex(cellIndex)] |= binaryEncodedNumber;
            emptyCellCount--;
        }
    }

    public void clearCell(int cellIndex) {
        emptyCellCount++;
        int number = cells[cellIndex];
        int bit = 1 << number;
        cells[cellIndex] = -1;
        columns[getColumnIndex(cellIndex)] &= ~bit;
        rows[getRowIndex(cellIndex)] &= ~bit;
        blocks[getBlockIndex(cellIndex)] &= ~bit;
    }

    public boolean cellIsEmpty(int cellIndex) {
        return cells[cellIndex] == -1;
    }

    public boolean cellIs(int cellIndex, int number) {
        return cells[cellIndex] == number;
    }

    public int getBinaryEncodedBuddyCellsNumbers(int columnIndex, int rowIndex, int blockIndex) {
        return columns[columnIndex] | rows[rowIndex] | blocks[blockIndex];
    }

    public boolean numberIsInvalidForCell(int columnIndex, int rowIndex, int blockIndex, int zeroBasedNumber) {
        return (getBinaryEncodedBuddyCellsNumbers(columnIndex, rowIndex, blockIndex) & 1 << zeroBasedNumber) != 0;
    }

    public String asString() {
        return Arrays.stream(cells).mapToObj(n -> String.valueOf(n + 1)).collect(Collectors.joining());
    }

    public int getBinaryEncodedCandidates(int cellIndex) {
        int columnIndex = getColumnIndex(cellIndex);
        int rowIndex = getRowIndex(cellIndex);
        int blockIndex = getBlockIndex(cellIndex);
        int binaryEncodedSetNumbers = columns[columnIndex] | rows[rowIndex] | blocks[blockIndex];
        return binaryEncodedSetNumbers ^ 0x1FF;
    }
}
