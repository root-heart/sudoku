package rootheart.codes.sudoku.solver.binaryoptimized;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

    public static void main(String[] args) throws IOException {
        JavaScriptTranslatedSolver solver = new JavaScriptTranslatedSolver();

        File solutionFile = new File("C:\\Users\\kai\\IdeaProjects\\sudoku\\sudoku.log");
        BufferedReader r = new BufferedReader(new FileReader(solutionFile));
        StringBuilder output = new StringBuilder();
        Map<String, String> solutions = new HashMap<>();
        while (r.ready()) {
            String[] strings = r.readLine().split(",");
            if (strings.length == 2) {
                solutions.put(strings[0], strings[1]);
            }
        }

        File file = new File("c:\\temp\\all_17_clue_sudokus.txt");
        r = new BufferedReader(new FileReader(file));
        int count = 0;
        long s = System.nanoTime();
        while (r.ready()) {
            if (count % 2000 == 0) {
                System.out.println(count + " sudokus solved");
            }
//            if (count == 200) break;
            count++;
            String line = r.readLine();
            if (line.length() == 81) {
                String solution = solver.solve(line);
                if (!solutions.containsKey(line)) {
                    System.out.println("(1)");
                }
                if (!solutions.get(line).equals(solution)) {
                    System.out.println("(2)");
                }
                output.append(line).append(',').append(solution).append('\n');
            } else {
                output.append(line);
            }
        }
        long e = System.nanoTime();
        System.out.println((e - s) / 1000 + " microseconds, guessed " + guessCount + " times, " + failedGuessCount + " failed");

        Arrays.stream(rootheart.codes.sudoku.solver.binaryoptimized.Board.class.getDeclaredFields())
                .filter(f -> f.getName().startsWith("count_"))
                .forEach(f -> {
                    try {
                        f.setAccessible(true);
                        System.out.println(f.getName() + " -> " + f.get(null));
                    } catch (IllegalAccessException e2) {
                        e2.printStackTrace();
                    }
                });
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

    private static class CellWithTheLowestNumberOfCandidates {
        int cellIndex;
        int candidateCount = 9;
        int binaryEncodedCandidates;
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
        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
            if (board.cellIsEmpty(cellIndex)) {
                int binaryEncodedCandidates = board.getBinaryEncodedCandidates(cellIndex);
                int number = getNumberOfSingleSetBit(binaryEncodedCandidates);
                if (number != -1) {
                    if (!play(board, indexesOfUpdatedCells, cellIndex, number)) {
                        return false;
                    }
                }
            }
        }

        if (indexesOfUpdatedCells.size() > 0) {
            // At least one naked single was set. Directly keep searching for new easy candidates
            if (search(board)) {
                return true;
            }
            undoAllMovesOnStack(board, indexesOfUpdatedCells);
            return false;
        }


        // HIDDEN SINGLES
        CellWithTheLowestNumberOfCandidates cellWithTheLowestNumberOfCandidates = null;
        int[] possibleRowsForCandidateInColumn = new int[81];
        int[] possibleColumnsForCandidateInRow = new int[81];
        int[] possibleCellIndexForCandidateInBlock = new int[81];
        for (int cellIndex = 0; cellIndex < 81; cellIndex++) {
            if (board.cellIsEmpty(cellIndex)) {
                int blockIndex = Board.BLOCK_INDEX[cellIndex];
                int binaryEncodedCandidates = board.getBinaryEncodedCandidates(cellIndex);
                int binaryEncodedCandidatesCopy = binaryEncodedCandidates;
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

                if (cellWithTheLowestNumberOfCandidates == null) {
                    cellWithTheLowestNumberOfCandidates = new CellWithTheLowestNumberOfCandidates();
                }

                int candidateCount = getSetBitsCount(binaryEncodedCandidatesCopy);
                if (candidateCount < cellWithTheLowestNumberOfCandidates.candidateCount) {
                    cellWithTheLowestNumberOfCandidates.cellIndex = cellIndex;
                    cellWithTheLowestNumberOfCandidates.binaryEncodedCandidates = binaryEncodedCandidatesCopy;
                    cellWithTheLowestNumberOfCandidates.candidateCount = candidateCount;
                }
            }
        }

        indexesOfUpdatedCells.clear();
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

        // if we've played at least one forced move, do a recursive call right away
        if (indexesOfUpdatedCells.size() > 0) {
            if (search(board)) {
                return true;
            }
            undoAllMovesOnStack(board, indexesOfUpdatedCells);
            return false;
        }

        // otherwise, try all moves on the cell with the fewest number of moves
        if (cellWithTheLowestNumberOfCandidates != null) {
            guessCount++;
            int bit;
            while ((bit = getFirstSetBit(cellWithTheLowestNumberOfCandidates.binaryEncodedCandidates)) > 0) {
                int numberToTry = getNumberOfSingleSetBit(bit);
                board.setZeroBasedNumberToCell(cellWithTheLowestNumberOfCandidates.cellIndex, numberToTry);

                if (search(board)) {
                    return true;
                }

                board.clearCell(cellWithTheLowestNumberOfCandidates.cellIndex);
                cellWithTheLowestNumberOfCandidates.binaryEncodedCandidates ^= bit;
            }
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
