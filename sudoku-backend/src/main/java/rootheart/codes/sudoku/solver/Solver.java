package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;
import rootheart.codes.sudoku.game.Group;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solver {
    public void solve(Board board) {
        System.out.println(board);
        while (board.hasEmptyCells()) {
            Map<Cell, Integer> cellIntegerMap = calculateCellsThatOnlyAllowOneValue(board);
            if (cellIntegerMap.isEmpty()) {
                throw new IllegalArgumentException("not solvable " + board);
            }

            cellIntegerMap.forEach((cell, number) -> {
                IntIntPair cellCoordinates = cell.getCoordinates();
                int column = cellCoordinates.getOne();
                int row = cellCoordinates.getTwo();
                System.out.println(String.format("Set %d to column:row %d:%d", number, column, row));
                cell.setNumber(number);
            });
        }
    }

    private Map<Cell, Integer> calculateCellsThatOnlyAllowOneValue(Board board) {
        Map<Cell, Integer> result = new HashMap<>();
        findNakedSingles(board, result);
        findHiddenSingles(board, result);
        return result;
    }

    private void findNakedSingles(Board board, Map<Cell, Integer> result) {
        for (int column = 0; column < board.getUnit(); column++) {
            for (int row = 0; row < board.getUnit(); row++) {
                Cell cell = board.cell(column, row);
                if (cell.getNumber() == 0) {
                    if (cell.getPossibleValues().size() == 1) {
                        Integer number = cell.getPossibleValues().iterator().next();
                        result.put(cell, number);
                    }
                }
            }
        }
    }

    private void findHiddenSingles(Board board, Map<Cell, Integer> result) {
        for (int groupIndex = 0; groupIndex < board.getUnit(); groupIndex++) {
            findHiddenSinglesInGroup(board, result, board.getColumn(groupIndex));
            findHiddenSinglesInGroup(board, result, board.getRow(groupIndex));
            findHiddenSinglesInGroup(board, result, board.getBlock(groupIndex));
        }
    }

    private void findHiddenSinglesInGroup(Board board, Map<Cell, Integer> result, Group group) {
        Map<Integer, Set<Cell>> map = new HashMap<>();
        for (int number = 1; number <= board.getUnit(); number++) {
            map.put(number, new HashSet<>());
        }
        for (int cellIndex = 0; cellIndex < board.getUnit(); cellIndex++) {
            Cell cell = group.getCell(cellIndex);
            for (int possibleValue : cell.getPossibleValues()) {
                map.get(possibleValue).add(cell);
            }
        }
        for (Map.Entry<Integer, Set<Cell>> entry : map.entrySet()) {
            if (entry.getValue().size() == 1) {
                Integer number = entry.getKey();
                result.put(entry.getValue().iterator().next(), number);
            }
        }
    }
}
