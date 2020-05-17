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
        eliminateNumbersPresentInBuddyCells(cellsCandidates);
        eliminateLockedCandidates(cellsCandidates);

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

    private void eliminateNumbersPresentInBuddyCells(Map<Cell, Set<Integer>> cellsCandidates) {
        cellsCandidates.forEach((cell, candidates) -> forAllBuddyCells(cell, candidates::remove));
    }

    private void eliminateLockedCandidates(Map<Cell, Set<Integer>> cellsCandidates) {
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
