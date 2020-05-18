package rootheart.codes.sudoku.game;

import lombok.Getter;

@Getter
public class Group extends CellList {
    public Group(Board board) {
        super(board.getMaxValue());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        forEach(cell -> sb.append(cell.getNumber()));
        return sb.toString();
    }
}
