package rootheart.codes.sudoku.solver.binaryoptimized;

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
        int binaryEncoded = 0;

        void addOneBased(int number) {
            binaryEncoded |= 1 << (number - 1);
        }

        void addZeroBased(int number) {
            binaryEncoded |= 1 << number;
        }

        void removeZeroBased(int number) {
            binaryEncoded = binaryEncoded & ~(1 << number);
        }
    }


    final int[] cells = new int[81];
    final PositiveIntegerSet[] rows = new PositiveIntegerSet[9];
    final PositiveIntegerSet[] columns = new PositiveIntegerSet[9];
    final PositiveIntegerSet[] blocks = new PositiveIntegerSet[9];
    int emptyCellCount = 81;

    public Board(String puzzle) {
        for (int i = 0; i < 9; i++) {
            columns[i] = new PositiveIntegerSet();
            rows[i] = new PositiveIntegerSet();
            blocks[i] = new PositiveIntegerSet();
        }
        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
            int number = Character.getNumericValue(puzzle.charAt(cellIndex));
            setNumberToCell(cellIndex, number);
        }
    }

    public void clear() {
        for (int i = 0; i < 81; i++) {
            cells[i] = -1;
        }
        for (int i = 0; i < 9; i++) {
            columns[i] = new PositiveIntegerSet();
            rows[i] = new PositiveIntegerSet();
            blocks[i] = new PositiveIntegerSet();
        }
        emptyCellCount = 81;
    }

    public void setNumberToCell(int cellIndex, int number) {
        cells[cellIndex] = number - 1;
        if (number != 0) {
            int columnIndex = cellIndex % 9;
            int rowIndex = cellIndex / 9;
            int binaryEncodedNumber = 1 << (number - 1);
            columns[columnIndex].binaryEncoded |= binaryEncodedNumber;
            rows[rowIndex].binaryEncoded |= binaryEncodedNumber;
            blocks[BLOCK_INDEX[cellIndex]].binaryEncoded |= binaryEncodedNumber;
            emptyCellCount--;
        }
    }

    public boolean cellIsEmpty(int cellIndex) {
        return cells[cellIndex] == -1;
    }

    public int getBinaryEncodedBuddyCellsNumbers(int cellIndex) {
        return columns[cellIndex % 9].binaryEncoded
                | rows[cellIndex / 9].binaryEncoded
                | blocks[BLOCK_INDEX[cellIndex]].binaryEncoded;
    }

    public boolean numberIsInvalidForCell(int cellIndex, int zeroBasedNumber) {
        return (getBinaryEncodedBuddyCellsNumbers(cellIndex) & 1 << zeroBasedNumber) != 0;
    }
}
