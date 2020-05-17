package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;
import rootheart.codes.sudoku.game.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solver {
    public void solve(Board board) {
        while (board.hasEmptyCells()) {
            Map<Cell, Integer> singleCandidates = calculateSingleCandidates(board);
            if (singleCandidates.isEmpty()) {
                throw new IllegalArgumentException("not solvable " + board);
            }
            singleCandidates.forEach(Cell::setNumber);
        }
    }

    private Map<Cell, Integer> calculateSingleCandidates(Board board) {
        Map<Cell, Set<Integer>> cellsCandidates = createCandidates(board);
        eliminateCandidatesAreSetInBuddyCells(cellsCandidates);
        eliminateLockedCandidates(cellsCandidates);
        eliminateNakedTwins(cellsCandidates);

        Map<Cell, Integer> cellsNumbers = new HashMap<>();
        cellsNumbers.putAll(findNakedSingles(cellsCandidates));
        cellsNumbers.putAll(findHiddenSingles(cellsCandidates));
        return cellsNumbers;
    }

    private Map<Cell, Set<Integer>> createCandidates(Board board) {
        return Arrays.stream(board.getCells())
                .filter(Cell::isEmpty)
                .collect(Collectors.toMap(Function.identity(), c -> new HashSet<>(board.getPossibleValues())));
    }

    private void eliminateCandidatesAreSetInBuddyCells(Map<Cell, Set<Integer>> cellsCandidates) {
        cellsCandidates.forEach((cell, candidates) -> forAllBuddyCells(cell, candidates::remove));
    }

    private void eliminateLockedCandidates(Map<Cell, Set<Integer>> cellsCandidates) {
        cellsCandidates.forEach(((cell, candidates) -> {
            // Für jeden Kandidaten schauen, ob er in einer Zelle einer anderen Zeile/Spalte in diesem Block existiert.
            // Falls nein, den Kandidaten für alle Zellen dieser Zeile/Spalte in anderen Blöcken löschen
            for (Integer candidate : candidates) {
                if (getEmptyCellsInSameBlockInOtherRows(cell)
                        .noneMatch(otherCell -> cellsCandidates.get(otherCell).contains(candidate))) {
                    getEmptyCellsInSameRowInOtherBlocks(cell)
                            .forEach(otherCell -> cellsCandidates.get(otherCell).remove(candidate));
                }
                if (getEmptyCellsInSameBlockInOtherColumns(cell)
                        .noneMatch(otherCell -> cellsCandidates.get(otherCell).contains(candidate))) {
                    getEmptyCellsInSameColumnInOtherBlocks(cell)
                            .forEach(otherCell -> cellsCandidates.get(otherCell).remove(candidate));
                }
                if (getEmptyCellsInSameRowInOtherBlocks(cell)
                        .noneMatch(otherCell -> cellsCandidates.get(otherCell).contains(candidate))) {
                    getEmptyCellsInSameBlockInOtherRows(cell)
                            .forEach(otherCell -> cellsCandidates.get(otherCell).remove(candidate));
                }
                if (getEmptyCellsInSameColumnInOtherBlocks(cell)
                        .noneMatch(otherCell -> cellsCandidates.get(otherCell).contains(candidate))) {
                    getEmptyCellsInSameBlockInOtherColumns(cell)
                            .forEach(otherCell -> cellsCandidates.get(otherCell).remove(candidate));
                }
            }
        }));
    }

    private void eliminateNakedTwins(Map<Cell, Set<Integer>> cellsCandidates) {
        cellsCandidates.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 2)
                .forEach(entry -> {
                    Cell cell = entry.getKey();
                    List<Cell> otherCellsInRow = getOtherCellsInGroup(cell, cell.getRow());
                    List<Cell> otherCellsInColumn = getOtherCellsInGroup(cell, cell.getColumn());
                    List<Cell> otherCellsInBlock = getOtherCellsInGroup(cell, cell.getBlock());
                    eliminateNakedTwinsInGroup(cellsCandidates, otherCellsInRow, entry.getValue());
                    eliminateNakedTwinsInGroup(cellsCandidates, otherCellsInColumn, entry.getValue());
                    eliminateNakedTwinsInGroup(cellsCandidates, otherCellsInBlock, entry.getValue());
                });
    }

    private List<Cell> getOtherCellsInGroup(Cell cell, Group group) {
        return group
                .getCells()
                .stream()
                .filter(otherCell -> cell != otherCell)
                .collect(Collectors.toList());
    }

    private void eliminateNakedTwinsInGroup(Map<Cell, Set<Integer>> cellsCandidates, List<Cell> otherCellsInGroup, Set<Integer> candidates) {
        otherCellsInGroup
                .stream()
                .filter(Cell::isEmpty)
                .filter(otherCell -> cellsCandidates.get(otherCell).equals(candidates))
                .findAny()
                .ifPresent(otherCell -> {
                    otherCellsInGroup
                            .stream()
                            .filter(Cell::isEmpty)
                            .filter(x -> otherCell != x)
                            .forEach(x -> cellsCandidates.get(x).removeAll(candidates));
                });
    }


    private Stream<Cell> getEmptyCellsInSameBlockInOtherRows(Cell cell) {
        return cell.getBlock()
                .getCells()
                .stream()
                .filter(Cell::isEmpty)
                .filter(otherCell -> otherCell.getRow() != cell.getRow());
    }

    private Stream<Cell> getEmptyCellsInSameRowInOtherBlocks(Cell cell) {
        return cell.getRow()
                .getCells()
                .stream()
                .filter(Cell::isEmpty)
                .filter(otherCell -> otherCell.getBlock() != cell.getBlock());
    }

    private Stream<Cell> getEmptyCellsInSameBlockInOtherColumns(Cell cell) {
        return cell.getBlock()
                .getCells()
                .stream()
                .filter(Cell::isEmpty)
                .filter(otherCell -> otherCell.getColumn() != cell.getColumn());
    }

    private Stream<Cell> getEmptyCellsInSameColumnInOtherBlocks(Cell cell) {
        return cell.getColumn()
                .getCells()
                .stream()
                .filter(Cell::isEmpty)
                .filter(otherCell -> otherCell.getBlock() != cell.getBlock());
    }

    private void forAllBuddyCells(Cell cell, Consumer<Integer> consumer) {
        forAllCellsExcept(cell.getColumn(), cell, consumer);
        forAllCellsExcept(cell.getRow(), cell, consumer);
        forAllCellsExcept(cell.getBlock(), cell, consumer);
    }

    private void forAllCellsExcept(Group group, Cell exception, Consumer<Integer> consumer) {
        group.getCells()
                .stream()
                .filter(cell -> cell != exception)
                .map(Cell::getNumber)
                .forEach(consumer);
    }

    private Map<Cell, Integer> findNakedSingles(Map<Cell, Set<Integer>> cellsCandidates) {
        return cellsCandidates.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().iterator().next()));
    }

    private Map<Cell, Integer> findHiddenSingles(Map<Cell, Set<Integer>> cellsCandidates) {
        Map<Cell, Integer> hiddenSingles = new HashMap<>();
        for (Map.Entry<Cell, Set<Integer>> entry : cellsCandidates.entrySet()) {
            hiddenSingles.putAll(findHiddenSinglesInGroup(cellsCandidates, entry.getKey().getColumn()));
            hiddenSingles.putAll(findHiddenSinglesInGroup(cellsCandidates, entry.getKey().getRow()));
            hiddenSingles.putAll(findHiddenSinglesInGroup(cellsCandidates, entry.getKey().getBlock()));
        }
        return hiddenSingles;
    }

    private Map<Cell, Integer> findHiddenSinglesInGroup(Map<Cell, Set<Integer>> cellsCandidates, Group group) {
        Map<Integer, List<Cell>> candidateCellsForNumber = new HashMap<>();
        group.getCells()
                .stream()
                .filter(Cell::isEmpty)
                .forEach(cell -> {
                    for (Integer candidate : cellsCandidates.get(cell)) {
                        candidateCellsForNumber.computeIfAbsent(candidate, k -> new ArrayList<>()).add(cell);
                    }
                });
        Map<Cell, Integer> hiddenSingles = new HashMap<>();
        candidateCellsForNumber.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .forEach(entry -> hiddenSingles.put(entry.getValue().get(0), entry.getKey()));
        return hiddenSingles;
    }
}
