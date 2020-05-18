package rootheart.codes.sudoku.game;

import lombok.Getter;

import java.util.BitSet;

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

    public boolean isValid() {
        BitSet b = new BitSet();
        return streamCells()
                .filter(c -> !c.isEmpty())
                .allMatch(c -> {
                    if (b.get(c.getNumber())) {
                        return false;
                    }
                    b.set(c.getNumber());
                    return true;
                });
    }
}
