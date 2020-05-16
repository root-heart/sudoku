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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        Map<Cell, Integer> cellsNumbers = new HashMap<>();
        cellsNumbers.putAll(findNakedSingles(cellsCandidates));
        cellsNumbers.putAll(findHiddenSingles(cellsCandidates));
        return cellsNumbers;
    }

    private Map<Cell, Set<Integer>> createCandidates(Board board) {
        Set<Integer> allValues = IntStream
                .rangeClosed(1, board.getMaxValue())
                .boxed()
                .collect(Collectors.toSet());
        return Arrays.stream(board.getCells())
                .filter(Cell::isEmpty)
                .collect(Collectors.toMap(Function.identity(), c -> new HashSet<>(allValues)));
    }

    private void eliminateNumbersPresentInBuddyCells(Map<Cell, Set<Integer>> result) {
        result.forEach((cell, candidates) -> {
            Set<Integer> buddyValues = getBuddyCells(cell).stream().map(Cell::getNumber).collect(Collectors.toSet());
            candidates.removeAll(buddyValues);
        });
    }

    private Set<Cell> getBuddyCells(Cell cell) {
        Set<Cell> buddyCells = new HashSet<>();
        buddyCells.addAll(cell.getColumn().getCells());
        buddyCells.addAll(cell.getRow().getCells());
        buddyCells.addAll(cell.getBlock().getCells());
        buddyCells.remove(cell);
        return buddyCells;
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
        Map<Cell, Integer> hiddenSingles = new HashMap<>();
        Map<Integer, List<Cell>> candidateCellsForNumber = new HashMap<>();
        group.getCells()
                .stream()
                .filter(Cell::isEmpty)
                .forEach(cell -> {
                    for (Integer candidate : cellsCandidates.get(cell)) {
                        candidateCellsForNumber.computeIfAbsent(candidate, k -> new ArrayList<>()).add(cell);
                    }
                });
        candidateCellsForNumber.entrySet()
                .stream()
                .filter(e -> e.getValue().size() == 1)
                .forEach(e -> hiddenSingles.put(e.getValue().get(0), e.getKey()));
        return hiddenSingles;
    }

}
