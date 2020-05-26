package rootheart.codes.sudoku.solver.binaryoptimized;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

public class JavaScriptTranslatedSolver {
    static final int[] SET_BITS_COUNT = new int[512];
    static final int[] FIRST_EMPTY_BIT = new int[512];
    static final int[] BIT_NUMBER = new int[512];

    private static class BitHelper {
        int bits;
        int countSetBits;
        int firstEmptyBit;
    }

    static {
        for (int i = 0; i < 1 << 9; i++) {
            int count = 0;
            int n = i;
            while (n > 0) {
                count += n & 1;
                n >>>= 1;
            }
            SET_BITS_COUNT[i] = count;
            FIRST_EMPTY_BIT[i] = ~i & -~i;
        }

        for (int x = 0; x < 9; x++) {
            BIT_NUMBER[1 << x] = x;
        }


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
    }

    private boolean play(Board board, PrimitiveStack stack, int columnIndex, int rowIndex, int number) {
        var cellIndex = rowIndex * 9 + columnIndex;

        if (!board.cellIsEmpty(cellIndex)) {
            if (board.cellIs(cellIndex, number)) {
                return true;
            }
            undoAllMovesOnStack(board, stack);
            return false;
        }

        var blockIndex = Board.BLOCK_INDEX[cellIndex];
        if (board.numberIsInvalidForCell(columnIndex, rowIndex, blockIndex, number)) {
            undoAllMovesOnStack(board, stack);
            return false;
        }
        board.setZeroBasedNumberToCell(cellIndex, number);
        stack.push(columnIndex << 8 | rowIndex << 4 | number);

        return true;
    }

    private void undoAllMovesOnStack(Board board, PrimitiveStack stack) {
        stack.forEach(stackElement -> {
            int columnIndex = stackElement >> 8;
            int rowIndex = stackElement >> 4 & 15;
            int cellIndex = rowIndex * 9 + columnIndex;
            board.clearCell(cellIndex);
        });
    }

    private static class Best {
        int cellIndex;
        int binaryEncodedNumbersAlreadySetInBuddyCells;
        int excludedCandidateCount;
    }

    static class PrimitiveStack {
        int[] stack = new int[81];
        int currentIndex = -1;

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

    private boolean search(Board board) {
        if (board.emptyCellCount == 0) {
            return true;
        }

        Best best = null;
        int[] hiddenSinglesInColumn = new int[81];
        int[] hiddenSinglesInRow = new int[81];
        int[] hiddenSinglesInBlock = new int[81];

        // scan the grid:
        // - keeping track of where each digit can go on a given column, row or block
        // - looking for a cell with the fewest number of legal moves
        for (int cellIndex, rowIndex = cellIndex = 0; rowIndex < 9; rowIndex++) {
            for (int columnIndex = 0; columnIndex < 9; columnIndex++, cellIndex++) {
                if (board.cellIsEmpty(cellIndex)) {
                    int blockIndex = Board.BLOCK_INDEX[cellIndex];
                    int binaryEncodedSetNumbers = board.getBinaryEncodedBuddyCellsNumbers(columnIndex, rowIndex, blockIndex);
                    int setNumbersCount = SET_BITS_COUNT[binaryEncodedSetNumbers];
                    int binaryEncodedCandidates = binaryEncodedSetNumbers ^ 0x1FF;
                    while (binaryEncodedCandidates != 0) {
                        int binaryEncodedSmallestCandidate = binaryEncodedCandidates & -binaryEncodedCandidates; // find the rightmost set bit
                        int smallestCandidate = BIT_NUMBER[binaryEncodedSmallestCandidate];
                        hiddenSinglesInColumn[columnIndex * 9 + smallestCandidate] |= 1 << rowIndex;
                        hiddenSinglesInRow[rowIndex * 9 + smallestCandidate] |= 1 << columnIndex;
                        hiddenSinglesInBlock[blockIndex * 9 + smallestCandidate] |= 1 << Board.CELL_INDEX_IN_BLOCK[cellIndex];
                        binaryEncodedCandidates ^= binaryEncodedSmallestCandidate;
                    }

                    // update the cell with the fewest number of moves
                    if (best == null) {
                        best = new Best();
                    }

                    if (setNumbersCount > best.excludedCandidateCount) {
                        best.cellIndex = cellIndex;
                        best.binaryEncodedNumbersAlreadySetInBuddyCells = binaryEncodedSetNumbers;
                        best.excludedCandidateCount = setNumbersCount;
                    }
                }
            }
        }

        // play all forced moves (unique candidates on a given column, row or block)
        // and make sure that it doesn't lead to any inconsistency
        PrimitiveStack stack = new PrimitiveStack();
        for (int k = 0; k < 9; k++) {
            for (int candidateToTest = 0; candidateToTest < 9; candidateToTest++) {
                int index = k * 9 + candidateToTest;
                int binaryEncodedPossibleRowsForCandidateInColumnK = hiddenSinglesInColumn[index];
                if (SET_BITS_COUNT[binaryEncodedPossibleRowsForCandidateInColumnK] == 1) {
                    int possibleRow = BIT_NUMBER[binaryEncodedPossibleRowsForCandidateInColumnK];
                    if (!play(board, stack, k, possibleRow, candidateToTest)) {
                        return false;
                    }
                }

                int binaryEncodedPossibleColumnsForCandidateInRowK = hiddenSinglesInRow[index];
                if (SET_BITS_COUNT[binaryEncodedPossibleColumnsForCandidateInRowK] == 1) {
                    int possibleColumn = BIT_NUMBER[binaryEncodedPossibleColumnsForCandidateInRowK];
                    if (!play(board, stack, possibleColumn, k, candidateToTest)) {
                        return false;
                    }
                }

                // ?
                int binaryEncodedPossibleBlockForCandidateInBlockK = hiddenSinglesInBlock[index];
                if (SET_BITS_COUNT[binaryEncodedPossibleBlockForCandidateInBlockK] == 1) {
                    int i = BIT_NUMBER[binaryEncodedPossibleBlockForCandidateInBlockK];
                    if (!play(board, stack, (k % 3) * 3 + i % 3, (k / 3) * 3 + (i / 3), candidateToTest)) {
                        return false;
                    }
                }
            }
        }

        // if we've played at least one forced move, do a recursive call right away
        if (stack.size() > 0) {
            if (search(board)) {
                return true;
            }
            undoAllMovesOnStack(board, stack);
            return false;
        }

        // otherwise, try all moves on the cell with the fewest number of moves
        if (best != null) {
            guessCount++;
            int bit;
            while ((bit = FIRST_EMPTY_BIT[best.binaryEncodedNumbersAlreadySetInBuddyCells]) < 0x200) {
                int numberToTry = BIT_NUMBER[bit];
                board.setZeroBasedNumberToCell(best.cellIndex, numberToTry);

                if (search(board)) {
                    return true;
                }

                board.clearCell(best.cellIndex);

                best.binaryEncodedNumbersAlreadySetInBuddyCells ^= bit;
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
