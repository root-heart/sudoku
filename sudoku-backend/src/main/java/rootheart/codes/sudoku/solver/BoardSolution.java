package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;

@Getter
public class BoardSolution {
    public enum Solution {
        ONE, NONE, MULTIPLE, INVALID
    }

    private final Solution solution;
    private final Board solvedBoard;

    public BoardSolution(Board solvedBoard) {
        this.solution = Solution.ONE;
        this.solvedBoard = solvedBoard;
    }

    public BoardSolution(Solution solution) {
        this.solution = solution;
        this.solvedBoard = null;
    }

    public static BoardSolution NONE = new BoardSolution(Solution.NONE);
    public static BoardSolution MULTIPLE = new BoardSolution(Solution.MULTIPLE);
    public static BoardSolution INVALID = new BoardSolution(Solution.INVALID);
}
