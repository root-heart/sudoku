package rootheart.codes.sudoku.solver.binaryoptimized;

import java.util.Arrays;
import java.util.stream.Collectors;

class Board {
     static final int[] BLOCK_INDEX = new int[81];
     static final int[] CELL_INDEX_IN_BLOCK = new int[81];

    static {
        for (int cellIndex, rowIndex = cellIndex = 0; rowIndex < 9; rowIndex++) {
            for (int columnIndex = 0; columnIndex < 9; columnIndex++, cellIndex++) {
                BLOCK_INDEX[cellIndex] = (rowIndex / 3) * 3 + (columnIndex / 3);
                CELL_INDEX_IN_BLOCK[cellIndex] = (rowIndex % 3) * 3 + columnIndex % 3;
            }
        }
    }

    static class PositiveIntegerSet {
//        int binaryEncoded = 0;

        static void addZeroBased(int binaryEncoded, int number) {
            binaryEncoded |= 1 << number;
        }

        static void removeZeroBased(int binaryEncoded, int number) {
            binaryEncoded = binaryEncoded & ~(1 << number);
        }
    }


    private final int[] cells = new int[81];
    private final int[] rows = new int[9];
    private final int[] columns = new int[9];
    private final int[] blocks = new int[9];
    int emptyCellCount = 81;

    public Board(String puzzle) {
        for (int i = 0; i < 9; i++) {
            columns[i] = new PositiveIntegerSet();
            rows[i] = new PositiveIntegerSet();
            blocks[i] = new PositiveIntegerSet();
        }
        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
            int number = Character.getNumericValue(puzzle.charAt(cellIndex));
            setZeroBasedNumberToCell(cellIndex, number - 1);
        }
    }

    public void clear() {
        for (int i = 0; i < 81; i++) {
            cells[i] = -1;
        }
        for (int i = 0; i < 9; i++) {
            columns[i] = 0; //new PositiveIntegerSet();
            rows[i] = 0; //new PositiveIntegerSet();
            blocks[i] = 0; //new PositiveIntegerSet();
        }
        emptyCellCount = 81;
    }

    public void setZeroBasedNumberToCell(int cellIndex, int number) {
        cells[cellIndex] = number;
        if (number != -1) {
            int binaryEncodedNumber = 1 << number;
            columns[cellIndex % 9] |= binaryEncodedNumber;
            rows[cellIndex / 9] |= binaryEncodedNumber;
            blocks[BLOCK_INDEX[cellIndex]] |= binaryEncodedNumber;
            emptyCellCount--;
        }
    }

    public void clearCell(int cellIndex) {
        emptyCellCount++;
        int number = cells[cellIndex];
        int bit = 1 << number;
        cells[cellIndex] = -1;
        columns[cellIndex % 9] &= ~bit;
        rows[cellIndex / 9] &= ~bit;
        blocks[BLOCK_INDEX[cellIndex]] &= ~bit;
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
}
