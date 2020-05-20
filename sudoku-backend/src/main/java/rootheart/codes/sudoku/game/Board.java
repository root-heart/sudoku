package rootheart.codes.sudoku.game;

import lombok.Getter;
import rootheart.codes.sudoku.solver.NumberSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Board {
    private int size;
    private int maxValue;
    private Group[] columns;
    private Group[] rows;
    private Group[] blocks;
    private final NumberSet possibleValues = new NumberSet();
    private final Set<Cell> singleCandidates = new HashSet<>();
    private final List<Cell> fixedCells = new ArrayList<>(100);
    private final List<Cell> emptyCells = new ArrayList<>(100);

    public Board() {
        this("0".repeat(81));
    }

    public Board(String board) {
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
            columns[i] = new Group(maxValue);
            rows[i] = new Group(maxValue);
            blocks[i] = new Group(maxValue);
        }

        possibleValues.clear();
        IntStream.rangeClosed(1, maxValue).forEach(possibleValues::add);

        createCells(board);
    }

    public void setSingleCandidates() {
        for (var it = emptyCells.iterator(); it.hasNext(); ) {
            Cell cell = it.next();
            cell.setNumber();
            it.remove();
            fixedCells.add(cell);
        }
    }

    public Cell getAnyEmptyCell() {
        return emptyCells.get(0);
    }

    private void createCells(String board) {
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                int blockIndex = (rowIndex / size) * size + (columnIndex / size);
                Cell cell = new Cell(columns[columnIndex], rows[rowIndex], blocks[blockIndex]);
                columns[columnIndex].add(cell);
                rows[rowIndex].add(cell);
                blocks[blockIndex].add(cell);

                int number = Character.getNumericValue(board.charAt(columnIndex + rowIndex * maxValue));
                if (number == 0) {
                    cell.getCandidates().addAll(possibleValues);
                    emptyCells.add(cell);
                } else {
                    cell.setNumber(number);
                    fixedCells.add(cell);
                }
            }
        }
        for (Cell cell : emptyCells) {
            cell.updateBuddyCells();
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
                sb.append(columns[columnIndex].getCell(rowIndex).getNumber());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getBoardString() {
        StringBuilder sb = new StringBuilder();
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                sb.append(cell(columnIndex, rowIndex).getNumber());
            }
        }
        return sb.toString();
    }

    public boolean isValid() {
        return Arrays.stream(columns).allMatch(Group::isValid)
                && Arrays.stream(rows).allMatch(Group::isValid)
                && Arrays.stream(blocks).allMatch(Group::isValid);
    }

    public void eliminateImpossibleCandidates() {
        for (int countBefore = singleCandidates.size(); ; ) {
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
        return emptyCells.stream()
                .anyMatch(entry -> entry.getCandidates().getCount() == 0);
    }

    public boolean hasEmptyCells() {
        return emptyCells.size() > 0;
    }
}
