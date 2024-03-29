package rootheart.codes.sudoku.game;

import lombok.Getter;

import java.util.BitSet;

@Getter
public class Group extends CellList {
    public Group(int cellCount) {
        super(cellCount);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        getCells().forEach(cell -> sb.append(cell.getNumber()));
        return sb.toString();
    }

    public boolean isValid() {
        BitSet b = new BitSet();
        return getCells().stream()
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
