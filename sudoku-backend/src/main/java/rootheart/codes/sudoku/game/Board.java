package rootheart.codes.sudoku.game;

import lombok.Getter;
import rootheart.codes.sudoku.solver.NoSolutionException;
import rootheart.codes.sudoku.solver.NumberSet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Board {
    private int size;
    @Getter
    private int maxValue;
    private final List<Group> columns = new ArrayList<>(10);
    private final List<Group> rows = new ArrayList<>(10);
    private final List<Group> blocks = new ArrayList<>(10);
    private final NumberSet possibleValues = new NumberSet();
    private final List<Cell> singleCandidates = new ArrayList<>(100);
    @Getter
    private final List<Cell> emptyCells = new ArrayList<>(100);

    private Board() {

    }

    public static Board of(String board) {
        return new Board(board);
    }

    public Board(String board) {
        this();
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

        possibleValues.clear();
        IntStream.rangeClosed(1, maxValue).forEach(possibleValues::add);

        createCells();
        initFromString(board);
        updateBuddyCells();
    }

    private void updateBuddyCells() {
        for (int i = 0; i < maxValue; i++) {
            columns.get(i).removeCellsCandidatesFromBuddyCells();
            rows.get(i).removeCellsCandidatesFromBuddyCells();
            blocks.get(i).removeCellsCandidatesFromBuddyCells();
        }
    }

    private void initFromString(String board) {
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                int number = Character.getNumericValue(board.charAt(columnIndex + rowIndex * maxValue));
                setCellNumber(columnIndex, rowIndex, number);
            }
        }
    }

    private void setCellNumber(int columnIndex, int rowIndex, int number) {
        Cell cell = cell(columnIndex, rowIndex);
        if (number == 0) {
            cell.getCandidates().addAll(possibleValues);
            emptyCells.add(cell);
        } else {
            cell.setNumber(number);
        }
    }

    private void setSingleCandidates() {
        for (var it = singleCandidates.iterator(); it.hasNext(); ) {
            Cell cell = it.next();
            cell.setNumber();
            it.remove();
        }
    }

    public Cell getAnyEmptyCell() {
        return emptyCells.get(0);
    }

    private void createCells() {
        for (int i = 0; i < maxValue; i++) {
            columns.add(new Group(maxValue));
            rows.add(new Group(maxValue));
            blocks.add(new Group(maxValue));
        }
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                int blockIndex = (rowIndex / size) * size + (columnIndex / size);
                Group column = columns.get(columnIndex);
                Group row = rows.get(rowIndex);
                Group block = blocks.get(blockIndex);
                Cell cell = new Cell(column, row, block);
                column.add(cell);
                row.add(cell);
                block.add(cell);
            }
        }
    }

    public Group getColumn(int columnIndex) {
        return columns.get(columnIndex);
    }

    public Group getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    public Group getBlock(int blockIndex) {
        return blocks.get(blockIndex);
    }

    public Cell cell(int column, int row) {
        return columns.get(column).getCell(row);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                sb.append(columns.get(columnIndex).getCell(rowIndex).getNumber());
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
        return columns.stream().allMatch(Group::isValid)
                && rows.stream().allMatch(Group::isValid)
                && blocks.stream().allMatch(Group::isValid);
    }

    private void eliminateImpossibleCandidates() {
        for (int countBefore = singleCandidates.size(); ; ) {
            for (var it = emptyCells.iterator(); it.hasNext(); ) {
                Cell cell = it.next();
                if (cell.getCandidates().isEmpty()) {
                    throw new NoSolutionException("found an empty cell with no candidates, board is not solvable");
                }
                cell.eliminateImpossibleCandidates();
                if (cell.getCandidates().hasOneNumber()) {
                    singleCandidates.add(cell);
                    it.remove();
                }
            }
            int countAfter = singleCandidates.size();
            if (countAfter == 0 || countBefore == countAfter || countAfter == emptyCells.size()) {
                return;
            }
            countBefore = countAfter;
        }
    }

    public boolean hasEmptyCells() {
        return emptyCells.size() > 0;
    }

    public Board copy() {
        Board copiedBoard = new Board();
        copiedBoard.size = size;
        copiedBoard.maxValue = maxValue;
        copiedBoard.possibleValues.addAll(possibleValues);
        copiedBoard.createCells();
        for (int rowIndex = 0; rowIndex < maxValue; rowIndex++) {
            for (int columnIndex = 0; columnIndex < maxValue; columnIndex++) {
                Cell cell = cell(columnIndex, rowIndex);
                copiedBoard.setCellNumber(columnIndex, rowIndex, cell.getNumber());
            }
        }
        copiedBoard.updateBuddyCells();
        return copiedBoard;
    }

    public void setValuesInCellsThatOnlyContainsOneCandidate() {
        eliminateImpossibleCandidates();
        setSingleCandidates();
    }
}
