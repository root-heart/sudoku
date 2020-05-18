package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;
import rootheart.codes.sudoku.game.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solver {

    public void solve(Board board) {
        while (board.hasEmptyCells()) {
            if (!isValid(board)) {
                throw new BoardInvalidException();
            }
            SolverBoard solverBoard = calculateCandidates(board);
            if (!solverBoard.hasSolution()) {
                throw new NoSolutionException("found no solution");
            }
            Board previousState = clone(board);

            Map<Cell, Integer> singleCandidates = findSingleCandidates(solverBoard);
            if (singleCandidates.isEmpty()) {
                System.out.println("XXX");
                Board boardToSetARandomNumberTo = clone(board);
                List<Board> solutions = new ArrayList<>();
                boardToSetARandomNumberTo.streamEmptyCells()
                        .findFirst()
                        .ifPresent(cell ->
                                boardToSetARandomNumberTo.getPossibleValues().forEach(numberToTry -> {
                                    cell.setNumber(numberToTry);
                                    if (isValid(boardToSetARandomNumberTo)) {
                                        if (System.currentTimeMillis() % 500 == 0) {
                                            System.out.println(previousState.getBoardString());
                                            int index = boardToSetARandomNumberTo.indexOf(cell);
                                            System.out.println(" ".repeat(Math.max(0, index)) + numberToTry);
                                        }
                                        Board boardToTryToSolve = clone(boardToSetARandomNumberTo);
                                        try {
                                            solve(boardToTryToSolve);
                                            solutions.add(boardToTryToSolve);
                                        } catch (NoSolutionException e) {
                                            // if trying this number did not end up with a solution, try the next one
                                        }
                                    }
                                }));
                if (solutions.size() == 0) {
                    throw new NoSolutionException("found no solution (2)");
                }
                if (solutions.size() > 1) {
                    throw new MultipleSolutionsException("found multiple solutions");
                }
                board.set(solutions.get(0).getBoardString());
            } else {
                Map.Entry<Cell, Integer> next = singleCandidates.entrySet().iterator().next();
                next.getKey().setNumber(next.getValue());
                System.out.println(previousState.getBoardString());
                int index = board.indexOf(next.getKey());
                System.out.println(" ".repeat(Math.max(0, index)) + next.getValue());
                if (!isValid(board)) {
                    throw new BoardInvalidException();
                }
            }
        }
    }

    private Board clone(Board board) {
        String boardString = board.getBoardString();
        return new Board(boardString);
    }

    private boolean isValid(Board board) {
        return Arrays.stream(board.getColumns()).allMatch(this::isValid)
                && Arrays.stream(board.getRows()).allMatch(this::isValid)
                && Arrays.stream(board.getBlocks()).allMatch(this::isValid);
    }

    private boolean isValid(Group group) {
        return group.streamCells()
                .filter(c -> !c.isEmpty())
                .collect(Collectors.groupingBy(Cell::getNumber, Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() == 1);
    }

    private SolverBoard calculateCandidates(Board board) {
        SolverBoard solverBoard = createSolverCells(board);
        solverBoard.eliminateCandidatesThatAreSetInBuddyCells();
        solverBoard.eliminateLockedCandidates();
        solverBoard.eliminateNakedTwins();
        return solverBoard;
    }

    private Map<Cell, Integer> findSingleCandidates(SolverBoard solverBoard) {
        Map<Cell, Integer> singleCandidates = new HashMap<>();
        singleCandidates.putAll(solverBoard.findNakedSingles());
        singleCandidates.putAll(solverBoard.findHiddenSingles());
        return singleCandidates;
    }

    private SolverBoard createSolverCells(Board board) {
        return new SolverBoard(board);
    }
}
