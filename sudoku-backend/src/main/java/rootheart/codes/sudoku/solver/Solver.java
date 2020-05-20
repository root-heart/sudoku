package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

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
        board.eliminateImpossibleCandidates();
        if (board.isNotSolvable()) {
            throw new NoSolutionException("found no solution");
        }
        board.setSingleCandidates();
        if (!board.isValid()) {
            throw new BoardInvalidException();
        }
//        int singleCandidateCount = solverBoard.getSingleCandidates().size();
//        long emptyCellCount = board.streamEmptyCells().count();
//        log.debug("single candidates: " + singleCandidateCount + "  remaining empty cells: " + emptyCellCount);
        if (board.hasEmptyCells()) {
            solveBruteForce(board);
        }
    }

    private void solveBruteForce(Board board) {
        Board boardToSetARandomNumberTo = clone(board);
        List<Board> solutions = new ArrayList<>();
        Cell cell = boardToSetARandomNumberTo.getAnyEmptyCell();
        cell.getCandidates().forEach(numberToTry -> {
            cell.setNumber(numberToTry);
            if (boardToSetARandomNumberTo.isValid()) {
                Board boardToTryToSolve = clone(boardToSetARandomNumberTo);
                try {
//                    log.debug("Try number " + numberToTry);
                    solve(boardToTryToSolve);
                    solutions.add(boardToTryToSolve);
                } catch (NoSolutionException e) {
                    // if trying this number did not end up with a solution, try the next one
//                    log.debug("No solution for " + numberToTry + ": " + e.getMessage());
                }
            }
        });
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
