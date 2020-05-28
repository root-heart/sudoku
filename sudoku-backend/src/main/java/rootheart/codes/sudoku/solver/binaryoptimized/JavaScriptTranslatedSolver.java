package rootheart.codes.sudoku.solver.binaryoptimized;

public class JavaScriptTranslatedSolver {

    public static int guessCount = 0;
    public static int failedGuessCount = 0;

    static final int[] SET_BITS_COUNT = new int[512];
    static final int[] BIT_NUMBER = new int[512];

    static final int[] columnIndexForBlockAndCellIndex = new int[81];
    static final int[] rowIndexForBlockAndCellIndex = new int[81];

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

        for (int blockIndex = 0; blockIndex < 9; blockIndex++) {
            for (int cellIndexInBlock = 0; cellIndexInBlock < 9; cellIndexInBlock++) {
                columnIndexForBlockAndCellIndex[blockIndex * 9 + cellIndexInBlock] = (blockIndex % 3) * 3 + (cellIndexInBlock % 3);
                rowIndexForBlockAndCellIndex[blockIndex * 9 + cellIndexInBlock] = (blockIndex / 3) * 3 + (cellIndexInBlock / 3);
            }
        }
    }

    public String solve(String puzzle) {
        Board board = new Board(puzzle);
        boolean isSolvable = search(board);
        return isSolvable ? board.asString() : "";
    }

    private boolean search(Board board) {
        if (board.emptyCellCount == 0) {
            return true;
        }

        IntStack indexesOfUpdatedCells = new IntStack();
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

    private Boolean findAndSetNakedSingles(Board board, IntStack indexesOfUpdatedCells) {
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

    private Boolean findAndSetHiddenSingles(Board board, IntStack indexesOfUpdatedCells) {
//        int[] columnCandidates = new int[81];
//        int[] rowCandidates = new int[81];
//        int[] blockCandidates = new int[81];
//
//        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
//            if (board.cellIsEmpty(cellIndex)) {
//                int binaryEncodedCandidates = board.getBinaryEncodedCandidates(cellIndex);
//                int columnIndex = cellIndex % 9;
//                int rowIndex = cellIndex / 9;
//                int blockIndex = Board.BLOCK_INDEX[cellIndex];
//                int cellIndexInBlock = Board.CELL_INDEX_IN_BLOCK[cellIndex];
//                columnCandidates[columnIndex * 9 + rowIndex] = binaryEncodedCandidates;
//                rowCandidates[rowIndex * 9 + columnIndex] = binaryEncodedCandidates;
//                blockCandidates[blockIndex * 9 + cellIndexInBlock] = binaryEncodedCandidates;
//            }
//        }
//
//        int[] hiddenSingles = new int[81];
//        int hiddenSinglesCount = 0;
//        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
//            if (board.cellIsEmpty(cellIndex)) {
//                int columnIndex = cellIndex % 9;
//                int rowIndex = cellIndex / 9;
//                int blockIndex = Board.BLOCK_INDEX[cellIndex];
//                int cellIndexInBlock = Board.CELL_INDEX_IN_BLOCK[cellIndex];
//                int candidatesInOtherRowsInThisColumn = 0;
//                int candidatesInOtherColumnsInThisRow = 0;
//                int candidatesInOtherCellsOfThisBlock = 0;
//                for (int i = 0; i < 9; i++) {
//                    if (i != rowIndex) {
//                        candidatesInOtherRowsInThisColumn |= columnCandidates[columnIndex * 9 + i];
//                    }
//                    if (i != columnIndex) {
//                        candidatesInOtherColumnsInThisRow |= rowCandidates[rowIndex * 9 + i];
//                    }
//                    if (i != cellIndexInBlock) {
//                        candidatesInOtherCellsOfThisBlock |= blockCandidates[blockIndex * 9 + i];
//                    }
//                }
//                int binaryEncodedCandidates = board.getBinaryEncodedCandidates(cellIndex);
//
//                int hiddenCandidate = getNumberOfSingleSetBit(binaryEncodedCandidates & ~candidatesInOtherColumnsInThisRow);
//                if (hiddenCandidate != -1) {
//                    hiddenSingles[hiddenSinglesCount++] = hiddenCandidate | (cellIndex << 5);
//                }
//                hiddenCandidate = getNumberOfSingleSetBit(binaryEncodedCandidates & ~candidatesInOtherRowsInThisColumn);
//                if (hiddenCandidate != -1) {
//                    hiddenSingles[hiddenSinglesCount++] = hiddenCandidate | (cellIndex << 5);
//                }
//                hiddenCandidate = getNumberOfSingleSetBit(binaryEncodedCandidates & ~candidatesInOtherCellsOfThisBlock);
//                if (hiddenCandidate != -1) {
//                    hiddenSingles[hiddenSinglesCount++] = hiddenCandidate | (cellIndex << 5);
//                }
//            }
//        }
//
//        for (int i = 0; i < hiddenSinglesCount; i++) {
//            int hiddenCandidate = hiddenSingles[i] & 0b11111;
//            int cellIndex = hiddenSingles[i] >> 5;
//            if (!play(board, indexesOfUpdatedCells, cellIndex, hiddenCandidate)) {
//                return Boolean.FALSE;
//            }
//        }

        // HIDDEN SINGLES
        int[] possibleRowsForCandidatesInColumn = new int[81];
        int[] possibleColumnsForCandidatesInRow = new int[81];
        int[] possibleCellIndexForCandidatesInBlock = new int[81];
        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
            if (board.cellIsEmpty(cellIndex)) {
                int columnIndex = cellIndex % 9;
                int rowIndex = cellIndex / 9;
                int blockIndex = Board.BLOCK_INDEX[cellIndex];
                int binaryEncodedCandidates = board.getBinaryEncodedCandidates(cellIndex);
                while (binaryEncodedCandidates != 0) {
                    int binaryEncodedSmallestCandidate = getFirstSetBit(binaryEncodedCandidates);
                    int candidate = getNumberOfSingleSetBit(binaryEncodedSmallestCandidate);
                    possibleRowsForCandidatesInColumn[columnIndex * 9 + candidate] |= 1 << rowIndex;
                    possibleColumnsForCandidatesInRow[rowIndex * 9 + candidate] |= 1 << columnIndex;
                    possibleCellIndexForCandidatesInBlock[blockIndex * 9 + candidate] |= 1 << Board.CELL_INDEX_IN_BLOCK[cellIndex];
                    binaryEncodedCandidates ^= binaryEncodedSmallestCandidate;
                }
            }
        }


        for (int i = 0; i < 81; i++) {
            int possibleRow = getNumberOfSingleSetBit(possibleRowsForCandidatesInColumn[i]);
            if (possibleRow != -1) {
                int groupIndex = i / 9;
                int candidateToTest = i % 9;
                if (!play(board, indexesOfUpdatedCells, groupIndex, possibleRow, candidateToTest)) {
                    return Boolean.FALSE;
                }
            }

            int possibleColumn = getNumberOfSingleSetBit(possibleColumnsForCandidatesInRow[i]);
            if (possibleColumn != -1) {
                int groupIndex = i / 9;
                int candidateToTest = i % 9;
                if (!play(board, indexesOfUpdatedCells, possibleColumn, groupIndex, candidateToTest)) {
                    return Boolean.FALSE;
                }
            }

            int possibleBlockCellIndex = getNumberOfSingleSetBit(possibleCellIndexForCandidatesInBlock[i]);
            if (possibleBlockCellIndex != -1) {
                int groupIndex = i / 9;
                int candidateToTest = i % 9;
                int columnIndex = (groupIndex % 3) * 3 + possibleBlockCellIndex % 3;
                int rowIndex = (groupIndex / 3) * 3 + (possibleBlockCellIndex / 3);
                if (!play(board, indexesOfUpdatedCells, columnIndex, rowIndex, candidateToTest)) {
                    return Boolean.FALSE;
                }
            }
        }


//        for (int groupIndex = 0; groupIndex < 9; groupIndex++) {
//            for (int candidateToTest = 0; candidateToTest < 9; candidateToTest++) {
//                int groupCandidateIndex = groupIndex * 9 + candidateToTest;
//                int binaryEncodedPossibleRowsForCandidateInColumn = possibleRowsForCandidatesInColumn[groupCandidateIndex];
//                int possibleRow = getNumberOfSingleSetBit(binaryEncodedPossibleRowsForCandidateInColumn);
//                if (possibleRow != -1) {
//                    if (!play(board, indexesOfUpdatedCells, groupIndex, possibleRow, candidateToTest)) {
//                        return false;
//                    }
//                }
//
//                int binaryEncodedPossibleColumnsForCandidateInRow = possibleColumnsForCandidatesInRow[groupCandidateIndex];
//                int possibleColumn = getNumberOfSingleSetBit(binaryEncodedPossibleColumnsForCandidateInRow);
//                if (possibleColumn != -1) {
//                    if (!play(board, indexesOfUpdatedCells, possibleColumn, groupIndex, candidateToTest)) {
//                        return false;
//                    }
//                }
//
//                int binaryEncodedPossibleBlockForCandidateInBlock = possibleCellIndexForCandidatesInBlock[groupCandidateIndex];
//                int possibleBlockCellIndex = getNumberOfSingleSetBit(binaryEncodedPossibleBlockForCandidateInBlock);
//                if (possibleBlockCellIndex != -1) {
//                    int columnIndex = (groupIndex % 3) * 3 + possibleBlockCellIndex % 3;
//                    int rowIndex = (groupIndex / 3) * 3 + (possibleBlockCellIndex / 3);
//                    if (!play(board, indexesOfUpdatedCells, columnIndex, rowIndex, candidateToTest)) {
//                        return false;
//                    }
//                }
//            }
//        }


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

    private boolean play(Board board, IntStack stack, int columnIndex, int rowIndex, int number) {
        var cellIndex = rowIndex * 9 + columnIndex;
        return play(board, stack, cellIndex, number);
    }

    private boolean play(Board board, IntStack stack, int cellIndex, int number) {
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

    private void undoAllMovesOnStack(Board board, IntStack stack) {
        stack.forEach(board::clearCell);
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
}
