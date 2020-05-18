package rootheart.codes.sudoku.game;

import lombok.Getter;

import java.util.stream.Collectors;

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
        return streamCells()
                .filter(c -> !c.isEmpty())
                .collect(Collectors.groupingBy(Cell::getNumber, Collectors.counting()))
                .entrySet().stream()
                .allMatch(numberCount -> numberCount.getValue() == 1);
    }
}
