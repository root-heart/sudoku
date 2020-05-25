package rootheart.codes.sudoku.solver.binaryoptimized;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

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
        System.out.println((e - s) / 1000 + " microseconds");
    }

    private boolean play(Board board, Stack<Integer> stack, int columnIndex, int rowIndex, int number) {
        var cellIndex = rowIndex * 9 + columnIndex;

        if (!board.cellIsEmpty(cellIndex)) {
            if (board.cells[cellIndex] == number) {
                return true;
            }
            undoAllMovesOnStack(board, stack);
            return false;
        }

        var blockIndex = Board.BLOCK_INDEX[cellIndex];
        if (board.numberIsInvalidForCell( cellIndex, number)) {
            undoAllMovesOnStack(board, stack);
            return false;
        }
        board.emptyCellCount--;
        board.columns[columnIndex].addZeroBased(number);
        board.rows[rowIndex].addZeroBased(number);
        board.blocks[blockIndex].addZeroBased(number);
        board.cells[cellIndex] = number;
        stack.push(columnIndex << 8 | rowIndex << 4 | number);

        return true;
    }

    private void undoAllMovesOnStack(Board board, Stack<Integer> stack) {
        stack.forEach(stackElement -> {
            int columnIndex = stackElement >> 8;
            int rowIndex = stackElement >> 4 & 15;
            int blockIndex = rowIndex * 9 + columnIndex;
            int blockCellIndex = Board.BLOCK_INDEX[blockIndex];

            stackElement = 1 << (stackElement & 15);

            board.emptyCellCount++;
            board.columns[columnIndex].binaryEncoded ^= stackElement;
            board.rows[rowIndex].binaryEncoded ^= stackElement;
            board.blocks[blockCellIndex].binaryEncoded ^= stackElement;
            board.cells[blockIndex] = -1;
        });
    }

    private static class Best {
        int columnIndex;
        int rowIndex;
        int cellIndex;
        int binaryEncodedNumbersAlreadySetInBuddyCells;
        int excludedCandidateCount;
    }

    private boolean search(Board board) {
        if (board.emptyCellCount == 0) {
            return true;
        }

        int cellIndex, columnIndex, rowIndex;
        int setNumbersCount;
        Best best = null;
        int[] dCol = new int[81];
        int[] dRow = new int[81];
        int[] dBlk = new int[81];

        // scan the grid:
        // - keeping track of where each digit can go on a given column, row or block
        // - looking for a cell with the fewest number of legal moves
        for (cellIndex = rowIndex = 0; rowIndex < 9; rowIndex++) {
            for (columnIndex = 0; columnIndex < 9; columnIndex++, cellIndex++) {
                if (board.cellIsEmpty(cellIndex)) {
                    int binaryEncodedSetNumbers = board.getBinaryEncodedBuddyCellsNumbers(cellIndex);
                    setNumbersCount = SET_BITS_COUNT[binaryEncodedSetNumbers];
//                    if (setNumbersCount == 9) {
//                        return false;
//                    }

                    int binaryEncodedCandidates = binaryEncodedSetNumbers ^ 0x1FF;
                    while (binaryEncodedCandidates != 0) {
                        int binaryEncodedSmallestCandidate = binaryEncodedCandidates & -binaryEncodedCandidates; // find the rightmost set bit
                        int smallestCandidate = BIT_NUMBER[binaryEncodedSmallestCandidate];
                        dCol[columnIndex * 9 + smallestCandidate] |= 1 << rowIndex;
                        dRow[rowIndex * 9 + smallestCandidate] |= 1 << columnIndex;
                        dBlk[Board.BLOCK_INDEX[cellIndex] * 9 + smallestCandidate] |= 1 << Board.CELL_INDEX_IN_BLOCK[cellIndex];
                        binaryEncodedCandidates ^= binaryEncodedSmallestCandidate;
                    }

                    // update the cell with the fewest number of moves
                    if (best == null) {
                        best = new Best();
                    }

                    if (setNumbersCount > best.excludedCandidateCount) {
                        best.columnIndex = columnIndex;
                        best.rowIndex = rowIndex;
                        best.cellIndex = cellIndex;
                        best.binaryEncodedNumbersAlreadySetInBuddyCells = binaryEncodedSetNumbers;
                        best.excludedCandidateCount = setNumbersCount;
                    }
                }
            }
        }

        // play all forced moves (unique candidates on a given column, row or block)
        // and make sure that it doesn't lead to any inconsistency
        Stack<Integer> stack = new Stack<>();
        for (int k = 0; k < 9; k++) {
            for (int candidateToTest = 0; candidateToTest < 9; candidateToTest++) {
                int binaryEncodedPossibleRowsForCandidateInColumnK = dCol[k * 9 + candidateToTest];
                if (SET_BITS_COUNT[binaryEncodedPossibleRowsForCandidateInColumnK] == 1) {
                    int possibleRow = BIT_NUMBER[binaryEncodedPossibleRowsForCandidateInColumnK];
                    if (!play(board, stack, k, possibleRow, candidateToTest)) {
                        return false;
                    }
                }

                int binaryEncodedPossibleColumnsForCandidateInRowK = dRow[k * 9 + candidateToTest];
                if (SET_BITS_COUNT[binaryEncodedPossibleColumnsForCandidateInRowK] == 1) {
                    int possibleColumn = BIT_NUMBER[binaryEncodedPossibleColumnsForCandidateInRowK];
                    if (!play(board, stack, possibleColumn, k, candidateToTest)) {
                        return false;
                    }
                }

                // ?
                int binaryEncodedPossibleBlockForCandidateInBlockK = dBlk[k * 9 + candidateToTest];
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
            int bit;
            while ((bit = FIRST_EMPTY_BIT[best.binaryEncodedNumbersAlreadySetInBuddyCells]) < 0x200) {
                int numberToTry = BIT_NUMBER[bit];
                board.columns[best.columnIndex].addZeroBased(numberToTry);
                board.rows[best.rowIndex].addZeroBased(numberToTry);
                board.blocks[Board.BLOCK_INDEX[best.cellIndex]].addZeroBased(numberToTry);
                board.cells[best.cellIndex] = numberToTry;
                board.emptyCellCount--;

                if (search(board)) {
                    return true;
                }

                board.emptyCellCount++;
                board.cells[best.cellIndex] = -1;
                board.columns[best.columnIndex].removeZeroBased(numberToTry);
                board.rows[best.rowIndex].removeZeroBased(numberToTry);
                board.blocks[Board.BLOCK_INDEX[best.cellIndex]].removeZeroBased(numberToTry);

                best.binaryEncodedNumbersAlreadySetInBuddyCells ^= bit;
            }
        }

        return false;
    }

    public String solve(String puzzle) {
        Board board = new Board(puzzle);
        boolean isSolvable = search(board);
        return isSolvable ? Arrays.stream(board.cells).mapToObj(n -> String.valueOf(n + 1)).collect(Collectors.joining()) : "";
    }
}
