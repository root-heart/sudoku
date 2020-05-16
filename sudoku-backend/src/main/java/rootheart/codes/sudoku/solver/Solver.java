package rootheart.codes.sudoku.solver;

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.map.primitive.ObjectIntMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;
import rootheart.codes.sudoku.game.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solver {
    public void solve(Board board) {
        while (board.hasEmptyCells()) {
            ObjectIntMap<Cell> singleCandidates = calculateSingleCandidates(board);
            if (singleCandidates.isEmpty()) {
                throw new IllegalArgumentException("not solvable " + board);
            }
            singleCandidates.forEachKeyValue(Cell::setNumber);
        }
    }

    private ObjectIntMap<Cell> calculateSingleCandidates(Board board) {
        Map<Cell, MutableIntSet> cellsCandidates = createCandidates(board);
        eliminateNumbersPresentInBuddyCells(cellsCandidates);

        MutableObjectIntMap<Cell> singleCandidates = ObjectIntMaps.mutable.empty();
        singleCandidates.putAll(findNakedSingles(cellsCandidates));
        singleCandidates.putAll(findHiddenSingles(cellsCandidates));
        return singleCandidates;
    }

    private Map<Cell, MutableIntSet> createCandidates(Board board) {
        IntSet allValues = IntSets.immutable.ofAll(IntStream.rangeClosed(1, board.getMaxValue()));
        return Arrays.stream(board.getCells())
                .filter(Cell::isEmpty)
                .collect(Collectors.toMap(Function.identity(), c -> IntSets.mutable.ofAll(allValues)));
    }

    private void eliminateNumbersPresentInBuddyCells(Map<Cell, MutableIntSet> cellsCandidates) {
        cellsCandidates.forEach((cell, candidates) -> {
            IntStream intStream = getBuddyCells(cell).stream().mapToInt(Cell::getNumber);
            IntSet buddyValues = IntSets.mutable.ofAll(intStream);
            candidates.removeAll(buddyValues);
        });
    }

    private Set<Cell> getBuddyCells(Cell cell) {
        MutableSet<Cell> buddyCells = Sets.mutable.ofAll(cell.getColumn().getCells());
        buddyCells.addAll(cell.getRow().getCells());
        buddyCells.addAll(cell.getBlock().getCells());
        buddyCells.remove(cell);
        return buddyCells;
    }

    private ObjectIntMap<Cell> findNakedSingles(Map<Cell, MutableIntSet> cellsCandidates) {
        MutableObjectIntMap<Cell> nakedSingles = ObjectIntMaps.mutable.empty();
        cellsCandidates.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .forEach(entry -> nakedSingles.put(entry.getKey(), entry.getValue().intIterator().next()));
        return nakedSingles;
    }

    private ObjectIntMap<Cell> findHiddenSingles(Map<Cell, MutableIntSet> cellsCandidates) {
        MutableObjectIntMap<Cell> hiddenSingles = ObjectIntMaps.mutable.empty();
        for (Map.Entry<Cell, MutableIntSet> entry : cellsCandidates.entrySet()) {
            hiddenSingles.putAll(findHiddenSinglesInGroup(cellsCandidates, entry.getKey().getColumn()));
            hiddenSingles.putAll(findHiddenSinglesInGroup(cellsCandidates, entry.getKey().getRow()));
            hiddenSingles.putAll(findHiddenSinglesInGroup(cellsCandidates, entry.getKey().getBlock()));
        }
        return hiddenSingles;
    }

    private MutableObjectIntMap<Cell> findHiddenSinglesInGroup(Map<Cell, MutableIntSet> cellsCandidates, Group group) {
        MutableObjectIntMap<Cell> hiddenSingles = ObjectIntMaps.mutable.empty();
        Map<Integer, List<Cell>> candidateCellsForNumber = new HashMap<>();
        group.getCells()
                .stream()
                .filter(Cell::isEmpty)
                .forEach(cell -> {
                    cellsCandidates.get(cell).forEach(candidate -> {
                        candidateCellsForNumber.computeIfAbsent(candidate, k -> new ArrayList<>()).add(cell);
                    });
                });
        candidateCellsForNumber.entrySet()
                .stream()
                .filter(e -> e.getValue().size() == 1)
                .forEach(e -> hiddenSingles.put(e.getValue().get(0), e.getKey()));
        return hiddenSingles;
    }

}
