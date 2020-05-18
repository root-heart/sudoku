package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Board;

import java.util.ArrayList;
import java.util.List;

public class Solver {

    public void solve(Board board) {
        while (board.hasEmptyCells()) {
            if (!board.isValid()) {
                throw new BoardInvalidException();
            }
            SolverBoard solverBoard = new SolverBoard(board);
            solverBoard.calculate();
            if (!solverBoard.hasSolution()) {
                throw new NoSolutionException("found no solution");
            }
//            Board previousState = clone(board);

            if (solverBoard.getSingleCandidates().size() == 0) {
//                System.out.println("XXX");
                Board boardToSetARandomNumberTo = clone(board);
                List<Board> solutions = new ArrayList<>();
                boardToSetARandomNumberTo.streamEmptyCells()
                        .findFirst()
                        .ifPresent(cell ->
                                boardToSetARandomNumberTo.getPossibleValues().forEach(numberToTry -> {
                                    cell.setNumber(numberToTry);
                                    if (boardToSetARandomNumberTo.isValid()) {
//                                        if (System.currentTimeMillis() % 500 == 0) {
//                                            System.out.println(previousState.getBoardString());
//                                            int index = boardToSetARandomNumberTo.indexOf(cell);
//                                            System.out.println(" ".repeat(Math.max(0, index)) + numberToTry);
//                                        }
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
                solverBoard.getSingleCandidates().forEach(solverCell -> {
                    solverCell.getCell().setNumber(solverCell.getFirstCandidate());
                    if (!board.isValid()) {
                        throw new BoardInvalidException();
                    }
                });
//                System.out.println(previousState.getBoardString());
//                int index = board.indexOf(next.getKey());
//                System.out.println(" ".repeat(Math.max(0, index)) + next.getValue());
            }
        }
    }

    private Board clone(Board board) {
        String boardString = board.getBoardString();
        return new Board(boardString);
    }
}
