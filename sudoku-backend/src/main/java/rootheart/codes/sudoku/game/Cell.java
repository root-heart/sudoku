package rootheart.codes.sudoku.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import rootheart.codes.sudoku.solver.SolverCell;

@Getter
@Setter
@RequiredArgsConstructor
public class Cell {
    private final Group column;
    private final Group row;
    private final Group block;
    private int number;
    private SolverCell solverCell;

    public boolean isEmpty() {
        return number == 0;
    }
}
