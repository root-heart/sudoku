package rootheart.codes.sudoku.solver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rootheart.codes.sudoku.game.Board;

@RequiredArgsConstructor
@Getter
public class BoardSolution {
    private final Solution solution;
    private final Board solvedBoard;

    public static enum Solution {
        NONE, ONE, MULTIPLE
    }

    public static final BoardSolution NONE = new BoardSolution(Solution.NONE, null);
    public static final BoardSolution MUTLIPLE = new BoardSolution(Solution.MULTIPLE, null);

    public BoardSolution(Board solvedBoard) {
        this.solvedBoard = solvedBoard;
        this.solution = Solution.ONE;
    }
}
