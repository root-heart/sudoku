package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Board;

import java.util.ArrayList;
import java.util.List;

public class Solver {

    public void solve(Board board) {
        if (!board.hasEmptyCells()) {
            return;
        }
        if (!board.isValid()) {
            throw new BoardInvalidException();
        }
        SolverBoard solverBoard = new SolverBoard(board);
        solverBoard.eliminateImpossibleCandidates();
        if (solverBoard.isNotSolvable()) {
            throw new NoSolutionException("found no solution");
        }

        int singleCandidateCount = solverBoard.getSingleCandidates().size();
        solverBoard.getSingleCandidates().forEach(solverCell -> {
            solverCell.getCell().setNumber(solverCell.getFirstCandidate());
            if (!board.isValid()) {
                throw new BoardInvalidException();
            }
        });
        long emptyCellCount = board.streamEmptyCells().count();
        System.out.println("single candidates: " + singleCandidateCount + "  remaining empty cells: " + emptyCellCount);
        if (emptyCellCount > 0) {
            solveBruteForce(board);
        }
    }

    private void solveBruteForce(Board board) {
        Board boardToSetARandomNumberTo = clone(board);
        List<Board> solutions = new ArrayList<>();
        boardToSetARandomNumberTo.streamEmptyCells()
                .findFirst()
                .ifPresent(cell ->
                        boardToSetARandomNumberTo.getPossibleValues().forEach(numberToTry -> {
                            cell.setNumber(numberToTry);
                            if (boardToSetARandomNumberTo.isValid()) {
                                Board boardToTryToSolve = clone(boardToSetARandomNumberTo);
                                try {
                                    System.out.println("Try number " + numberToTry);
                                    solve(boardToTryToSolve);
                                    solutions.add(boardToTryToSolve);
                                } catch (NoSolutionException e) {
                                    // if trying this number did not end up with a solution, try the next one
                                    System.out.println("No solution for " + numberToTry + ": " + e.getMessage());
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
    }

    private Board clone(Board board) {
        String boardString = board.getBoardString();
        return new Board(boardString);
    }
}
