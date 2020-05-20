package rootheart.codes.sudoku.game;

import lombok.Getter;
import rootheart.codes.sudoku.solver.NumberSet;
import rootheart.codes.sudoku.solver.SolverCell;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class Board extends CellList {
    private int size;
    private int maxValue;
    private Group[] columns;
    private Group[] rows;
    private Group[] blocks;
    private NumberSet possibleValues;
    private final Set<Cell> singleCandidates = new HashSet<>();

    public Board(String board) {
        super(board.length());
        set(board);
    }

    public void set(String board) {
        if (board.length() == 16) {
            size = 2;
        } else if (board.length() == 81) {
            size = 3;
        } else {
            throw new IllegalArgumentException(board);
        }
        maxValue = size * size;
        columns = new Group[maxValue];
        rows = new Group[maxValue];
        blocks = new Group[maxValue];
        for (int i = 0; i < maxValue; i++) {
            columns[i] = new Group(this);
            rows[i] = new Group(this);
            blocks[i] = new Group(this);
        }

        possibleValues = new NumberSet();
        IntStream.rangeClosed(1, maxValue).forEach(possibleValues::add);

        createCells();
        initCells(board);
    }

    private void createCells() {
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                int blockIndex = (rowIndex / size) * size + (columnIndex / size);
                int blockCellIndex = columnIndex % size + rowIndex % size * size;
                Cell cell = new Cell(columns[columnIndex], rows[rowIndex], blocks[blockIndex]);
                columns[columnIndex].add(cell);
                rows[rowIndex].add(cell);
                blocks[blockIndex].add(cell);
                add(cell);
            }
        }
    }

    private void initCells(String board) {
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                int number = Character.getNumericValue(board.charAt(columnIndex + rowIndex * maxValue));
                columns[columnIndex].getCell(rowIndex).setNumber(number);
            }
        }
    }

    public Group getColumn(int columnIndex) {
        return columns[columnIndex];
    }

    public Group getRow(int rowIndex) {
        return rows[rowIndex];
    }

    public Group getBlock(int blockIndex) {
        return blocks[blockIndex];
    }

    public Cell cell(int column, int row) {
        return columns[column].getCell(row);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                sb.append(cell(columnIndex, rowIndex).getNumber());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getBoardString() {
        return getCells().stream()
                .map(Cell::getNumber)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public boolean isValid() {
        return Arrays.stream(columns).allMatch(Group::isValid)
                && Arrays.stream(rows).allMatch(Group::isValid)
                && Arrays.stream(blocks).allMatch(Group::isValid);
    }


    public void eliminateImpossibleCandidates() {
        for (int countBefore = singleCandidates.size(); ; ) {
            List<Cell> emptyCells = getCells().stream().filter(Cell::isEmpty).collect(Collectors.toList());
            for (Cell cell : emptyCells) {
                if (!singleCandidates.contains(cell)) {
                    cell.eliminateImpossibleCandidates();
                    if (cell.getCandidates().hasOneNumber()) {
                        singleCandidates.add(cell);
                    }
                }
            }
            int countAfter = singleCandidates.size();
            if (countAfter == 0 || countBefore == countAfter || countAfter == emptyCells.size()) {
                return;
            }
            countBefore = countAfter;
        }
    }


    public boolean isNotSolvable() {
        return getCells().stream().filter(Cell::isEmpty)
                .anyMatch(entry -> entry.getCandidates().getCount() == 0);
    }
}
