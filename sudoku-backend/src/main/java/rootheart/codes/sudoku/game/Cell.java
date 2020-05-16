package rootheart.codes.sudoku.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Cell {
    private final Group column;
    private final Group row;
    private final Group block;
    private int number;

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    public boolean isEmpty() {
        return number == 0;
    }
}
