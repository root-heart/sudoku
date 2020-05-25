package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.ArrayList;
import java.util.List;

public class Solver {

    public BoardSolution solve(Board board) {
        if (!board.hasEmptyCells()) {
            return new BoardSolution(board);
        }
        Board solvedBoard = board.copy();
        try {
            solvedBoard.setValuesInCellsThatOnlyContainsOneCandidate();
        } catch (NoSolutionException e) {
            return BoardSolution.NONE;
        }
//        int singleCandidateCount = solverBoard.getSingleCandidates().size();
//        long emptyCellCount = board.streamEmptyCells().count();
//        log.debug("single candidates: " + singleCandidateCount + "  remaining empty cells: " + emptyCellCount);
        if (solvedBoard.hasEmptyCells()) {
            return solveBruteForce(board);
        }
        return new BoardSolution(solvedBoard);
    }

    private BoardSolution solveBruteForce(Board board) {
        Board boardToSetARandomNumberTo = board;//board.copy();
        List<Board> solutions = new ArrayList<>();
        Cell cell = boardToSetARandomNumberTo.getAnyEmptyCell();
        for (int numberToTry = 0; numberToTry <= boardToSetARandomNumberTo.getMaxValue(); numberToTry++) {
            if (cell.getCandidates().contains(numberToTry)) {
                cell.setNumber(numberToTry);
                Board boardToTryToSolve = boardToSetARandomNumberTo.copy();
//                System.out.println("Try number " + numberToTry);
                BoardSolution solution = solve(boardToTryToSolve);
                if (solution.getSolution() == BoardSolution.Solution.ONE) {
                    solutions.add(solution.getSolvedBoard());
//                    System.out.println("Found a solution for " + numberToTry);
                } else if (solution == BoardSolution.MULTIPLE) {
                    return solution;
                }
            }
        }
        if (solutions.size() == 0) {
//            System.out.println("Found no Solution");
            return BoardSolution.NONE;
        }
        if (solutions.size() > 1) {
//            System.out.println("Found multiple solutions");
            return BoardSolution.MULTIPLE;
        }
//        System.out.println("Found a single solution :)");
        return new BoardSolution(solutions.get(0));
    }
}
