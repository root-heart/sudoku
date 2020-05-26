package rootheart.codes.sudoku.solver.binaryoptimized;

import java.util.function.IntConsumer;

public class JavaScriptTranslatedSolver {
    static final int[] SET_BITS_COUNT = new int[512];
    static final int[] BIT_NUMBER = new int[512];

    static {
        for (int i = 0; i < 1 << 9; i++) {
            int count = 0;
            int n = i;
            while (n > 0) {
                count += n & 1;
                n >>>= 1;
            }
            SET_BITS_COUNT[i] = count;
            BIT_NUMBER[i] = -1;
        }

        for (int i = 0; i < 9; i++) {
            BIT_NUMBER[1 << i] = i;
        }
    }

    private static int getSetBitsCount(int number) {
        return SET_BITS_COUNT[number];
    }

    private static int getFirstSetBit(int number) {
        return number & -number;
    }

    private static int getNumberOfSingleSetBit(int number) {
        return BIT_NUMBER[number];
    }

    private boolean play(Board board, PrimitiveStack stack, int columnIndex, int rowIndex, int number) {
        var cellIndex = rowIndex * 9 + columnIndex;
        return play(board, stack, cellIndex, number);
    }

    private boolean play(Board board, PrimitiveStack stack, int cellIndex, int number) {
        if (!board.cellIsEmpty(cellIndex)) {
            if (board.cellIs(cellIndex, number)) {
                return true;
            }
            undoAllMovesOnStack(board, stack);
            return false;
        }

        if (board.numberIsInvalidForCell(cellIndex, number)) {
            undoAllMovesOnStack(board, stack);
            return false;
        }
        board.setZeroBasedNumberToCell(cellIndex, number);
        stack.push(cellIndex);

        return true;
    }

    private void undoAllMovesOnStack(Board board, PrimitiveStack stack) {
        stack.forEach(board::clearCell);
    }

    static class PrimitiveStack {
        int[] stack = new int[81];
        int currentIndex = -1;

        void clear() {
            currentIndex = -1;
        }

        void push(int number) {
            stack[++currentIndex] = number;
        }

        int pop() {
            return stack[currentIndex--];
        }

        int size() {
            return currentIndex + 1;
        }

        void forEach(IntConsumer consumer) {
            for (int i = 0; i <= currentIndex; i++) {
                consumer.accept(stack[i]);
            }
        }
    }

    static int[] columnIndexForBlockAndCellIndex = new int[81];
    static int[] rowIndexForBlockAndCellIndex = new int[81];

    static {
        for (int blockIndex = 0; blockIndex < 9; blockIndex++) {
            for (int cellIndexInBlock = 0; cellIndexInBlock < 9; cellIndexInBlock++) {
                columnIndexForBlockAndCellIndex[blockIndex * 9 + cellIndexInBlock] = (blockIndex % 3) * 3 + (cellIndexInBlock % 3);
                rowIndexForBlockAndCellIndex[blockIndex * 9 + cellIndexInBlock] = (blockIndex / 3) * 3 + (cellIndexInBlock / 3);
            }
        }
    }

    private boolean search(Board board) {
        if (board.emptyCellCount == 0) {
            return true;
        }

        PrimitiveStack indexesOfUpdatedCells = new PrimitiveStack();
        // FULL HOUSES doesn't work because I don't know the index of any given empty cell inside a group


        // NAKED SINGLES
        indexesOfUpdatedCells.clear();
        Boolean x = findAndSetNakedSingles(board, indexesOfUpdatedCells);
        if (x != null) return x;

        x = findAndSetHiddenSingles(board, indexesOfUpdatedCells);
        if (x != null) return x;


        // BRUTE FORCE
        return solveBruteForce(board);
    }

    private Boolean findAndSetHiddenSingles(Board board, PrimitiveStack indexesOfUpdatedCells) {
        // HIDDEN SINGLES
        int[] possibleRowsForCandidateInColumn = new int[81];
        int[] possibleColumnsForCandidateInRow = new int[81];
        int[] possibleCellIndexForCandidateInBlock = new int[81];
        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
            if (board.cellIsEmpty(cellIndex)) {
                int blockIndex = Board.BLOCK_INDEX[cellIndex];
                int binaryEncodedCandidates = board.getBinaryEncodedCandidates(cellIndex);
                while (binaryEncodedCandidates != 0) {
                    int binaryEncodedSmallestCandidate = getFirstSetBit(binaryEncodedCandidates);
                    int smallestCandidate = getNumberOfSingleSetBit(binaryEncodedSmallestCandidate);
                    int columnIndex = cellIndex % 9;
                    int rowIndex = cellIndex / 9;
                    possibleRowsForCandidateInColumn[columnIndex * 9 + smallestCandidate] |= 1 << rowIndex;
                    possibleColumnsForCandidateInRow[rowIndex * 9 + smallestCandidate] |= 1 << columnIndex;
                    possibleCellIndexForCandidateInBlock[blockIndex * 9 + smallestCandidate] |= 1 << Board.CELL_INDEX_IN_BLOCK[cellIndex];
                    binaryEncodedCandidates ^= binaryEncodedSmallestCandidate;
                }
            }
        }


        for (int groupIndex = 0; groupIndex < 9; groupIndex++) {
            for (int candidateToTest = 0; candidateToTest < 9; candidateToTest++) {
                int groupCandidateIndex = groupIndex * 9 + candidateToTest;
                int binaryEncodedPossibleRowsForCandidateInColumnK = possibleRowsForCandidateInColumn[groupCandidateIndex];
                int possibleRow = getNumberOfSingleSetBit(binaryEncodedPossibleRowsForCandidateInColumnK);
                if (possibleRow != -1) {
                    if (!play(board, indexesOfUpdatedCells, groupIndex, possibleRow, candidateToTest)) {
                        return false;
                    }
                }

                int binaryEncodedPossibleColumnsForCandidateInRowK = possibleColumnsForCandidateInRow[groupCandidateIndex];
                int possibleColumn = getNumberOfSingleSetBit(binaryEncodedPossibleColumnsForCandidateInRowK);
                if (possibleColumn != -1) {
                    if (!play(board, indexesOfUpdatedCells, possibleColumn, groupIndex, candidateToTest)) {
                        return false;
                    }
                }

                int binaryEncodedPossibleBlockForCandidateInBlockK = possibleCellIndexForCandidateInBlock[groupCandidateIndex];
                int possibleBlockCellIndex = getNumberOfSingleSetBit(binaryEncodedPossibleBlockForCandidateInBlockK);
                if (possibleBlockCellIndex != -1) {
                    if (!play(board, indexesOfUpdatedCells,
                            (groupIndex % 3) * 3 + possibleBlockCellIndex % 3,
                            (groupIndex / 3) * 3 + (possibleBlockCellIndex / 3),
                            candidateToTest)) {
                        return false;
                    }
                }
            }
        }

        if (board.emptyCellCount == 0) {
            return true;
        }

        // if we've played at least one forced move, do a recursive call right away
        if (indexesOfUpdatedCells.size() > 0) {
            if (search(board)) {
                return true;
            }
            undoAllMovesOnStack(board, indexesOfUpdatedCells);
            return false;
        }
        return null;
    }

    private Boolean findAndSetNakedSingles(Board board, PrimitiveStack indexesOfUpdatedCells) {
        int iterationCount = 0;
        for (int cellIndex = 0; iterationCount < 81; cellIndex++) {
            if (cellIndex == 81) {
                cellIndex = 0;
            }
            if (board.cellIsEmpty(cellIndex)) {
                int binaryEncodedCandidates = board.getBinaryEncodedCandidates(cellIndex);
                int number = getNumberOfSingleSetBit(binaryEncodedCandidates);
                if (number != -1) {
                    if (!play(board, indexesOfUpdatedCells, cellIndex, number)) {
                        return false;
                    }
                    iterationCount = 0;
                }
            }
            iterationCount++;
        }
        if (board.emptyCellCount == 0) {
            return true;
        }
        return null;
    }

    private boolean solveBruteForce(Board board) {
        guessCount++;

        int lowestCandidateCount = 9;
        int lowestCandidateCountCellIndex = 0;
        int lowestCandidateCountCandidates = 0;

        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
            if (board.cellIsEmpty(cellIndex)) {
                int binaryEncodedCandidates = board.getBinaryEncodedCandidates(cellIndex);
                int candidateCount = getSetBitsCount(binaryEncodedCandidates);
                if (candidateCount < lowestCandidateCount) {
                    lowestCandidateCountCellIndex = cellIndex;
                    lowestCandidateCountCandidates = binaryEncodedCandidates;
                    lowestCandidateCount = candidateCount;
                }
            }
        }
        int bit;
        while ((bit = getFirstSetBit(lowestCandidateCountCandidates)) > 0) {
            int numberToTry = getNumberOfSingleSetBit(bit);
            board.setZeroBasedNumberToCell(lowestCandidateCountCellIndex, numberToTry);

            if (search(board)) {
                return true;
            }

            board.clearCell(lowestCandidateCountCellIndex);
            lowestCandidateCountCandidates ^= bit;
        }

        failedGuessCount++;
        return false;
    }

    public static int guessCount = 0;
    public static int failedGuessCount = 0;

    public String solve(String puzzle) {
        Board board = new Board(puzzle);
        boolean isSolvable = search(board);
        return isSolvable ? board.asString() : "";
    }
}
