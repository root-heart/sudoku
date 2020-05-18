package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class SolverCell {
    private final Cell cell;
    private final Set<Integer> candidates = new HashSet<>();
    private final Set<SolverCell> otherCellsInColumn = new HashSet<>();
    private final Set<SolverCell> otherCellsInRow = new HashSet<>();
    private final Set<SolverCell> otherCellsInBlock = new HashSet<>();

    public SolverCell(Cell cell, Board board) {
        this.cell = cell;
        if (cell.isEmpty()) {
            candidates.addAll(board.getPossibleValues());
        }
    }

    public void eliminateImpossibilities() {
        eliminateCandidatesThatAreSetInBuddyCells();
        revealHiddenSingle();
        eliminateLockedCandidates();
        eliminateNakedTwins();
        if (hasOneCandidate()) {
            otherCellsInColumn.forEach(otherCell -> otherCell.candidates.removeAll(candidates));
            otherCellsInRow.forEach(otherCell -> otherCell.candidates.removeAll(candidates));
            otherCellsInBlock.forEach(otherCell -> otherCell.candidates.removeAll(candidates));
        }
    }

    public void eliminateCandidatesThatAreSetInBuddyCells() {
        otherCellsInColumn.stream().map(SolverCell::getCell).map(Cell::getNumber).forEach(candidates::remove);
        otherCellsInRow.stream().map(SolverCell::getCell).map(Cell::getNumber).forEach(candidates::remove);
        otherCellsInBlock.stream().map(SolverCell::getCell).map(Cell::getNumber).forEach(candidates::remove);
    }

    public void eliminateLockedCandidates() {
        // Für jeden Kandidaten schauen, ob er in einer Zelle einer anderen Zeile/Spalte in diesem Block existiert.
        // Falls nein, den Kandidaten für alle Zellen dieser Zeile/Spalte in anderen Blöcken löschen
        for (Integer candidate : getCandidates()) {
            if (getEmptyCellsInSameBlockInOtherRows()
                    .noneMatch(otherCell -> otherCell.candidates.contains(candidate))) {
                getEmptyCellsInSameRowInOtherBlocks()
                        .forEach(otherCell -> otherCell.candidates.remove(candidate));
            }
            if (getEmptyCellsInSameBlockInOtherColumns()
                    .noneMatch(otherCell -> otherCell.candidates.contains(candidate))) {
                getEmptyCellsInSameColumnInOtherBlocks()
                        .forEach(otherCell -> otherCell.candidates.remove(candidate));
            }
            if (getEmptyCellsInSameRowInOtherBlocks()
                    .noneMatch(otherCell -> otherCell.candidates.contains(candidate))) {
                getEmptyCellsInSameBlockInOtherRows()
                        .forEach(otherCell -> otherCell.candidates.remove(candidate));
            }
            if (getEmptyCellsInSameColumnInOtherBlocks()
                    .noneMatch(otherCell -> otherCell.candidates.contains(candidate))) {
                getEmptyCellsInSameBlockInOtherColumns()
                        .forEach(otherCell -> otherCell.candidates.remove(candidate));
            }
        }
    }

    public boolean hasOneCandidate() {
        return candidates.size() == 1;
    }

    public Integer getFirstCandidate() {
        return candidates.iterator().next();
    }

    public void revealHiddenSingle() {
        List<Integer> hiddenSingles = new ArrayList<>();
        for (Integer candidate : candidates) {
            if (!isPresentInOtherCells(candidate, otherCellsInColumn)
                    || !isPresentInOtherCells(candidate, otherCellsInRow)
                    || !isPresentInOtherCells(candidate, otherCellsInBlock)) {
                hiddenSingles.add(candidate);
            }
        }
        if (hiddenSingles.size() > 1) {
            throw new NoSolutionException("multiple values can only exist in this cell, this is not possible");
        }
        if (hiddenSingles.size() == 1) {
            candidates.clear();
            candidates.add(hiddenSingles.get(0));
        }
    }

    public void eliminateNakedTwins() {
        if (candidates.size() == 2) {
            eliminateNakedTwinsInGroup(otherCellsInRow);
            eliminateNakedTwinsInGroup(otherCellsInColumn);
            eliminateNakedTwinsInGroup(otherCellsInBlock);
        }
    }

    private boolean isPresentInOtherCells(Integer candidate, Set<SolverCell> otherCells) {
        return otherCells
                .stream()
                .anyMatch(otherCell -> otherCell.getCandidates().contains(candidate));
    }

    private void eliminateNakedTwinsInGroup(Set<SolverCell> otherCellsInGroup) {
        findNakedTwinInGroup(otherCellsInGroup)
                .ifPresent(otherCell -> otherCellsInGroup.forEach(it -> {
                    if (it != otherCell) {
                        it.getCandidates().removeAll(candidates);
                    }
                }));
    }

    private Optional<SolverCell> findNakedTwinInGroup(Set<SolverCell> otherCellsInGroup) {
        List<SolverCell> nakedTwins = findOtherCellsWithSameCandidates(otherCellsInGroup);
        if (nakedTwins.size() > 1) {
            throw new NoSolutionException("more than two cells only allow the same two numbers, this is not possible");
        }
        return nakedTwins.size() == 0 ? Optional.empty() : Optional.of(nakedTwins.get(0));
    }

    private List<SolverCell> findOtherCellsWithSameCandidates(Set<SolverCell> otherCellsInGroup) {
        return otherCellsInGroup.stream()
                .filter(otherCell -> otherCell.getCandidates().equals(candidates))
                .collect(Collectors.toList());
    }

    private Stream<SolverCell> getEmptyCellsInSameBlockInOtherRows() {
        return otherCellsInBlock.stream()
                .filter(SolverCell::isEmpty)
                .filter(this::rowDiffers);
    }

    private Stream<SolverCell> getEmptyCellsInSameRowInOtherBlocks() {
        return otherCellsInRow.stream()
                .filter(SolverCell::isEmpty)
                .filter(this::blockDiffers);
    }

    private Stream<SolverCell> getEmptyCellsInSameBlockInOtherColumns() {
        return otherCellsInBlock.stream()
                .filter(SolverCell::isEmpty)
                .filter(this::columnDiffers);
    }

    private Stream<SolverCell> getEmptyCellsInSameColumnInOtherBlocks() {
        return otherCellsInColumn.stream()
                .filter(SolverCell::isEmpty)
                .filter(this::blockDiffers);
    }

    private boolean columnDiffers(SolverCell otherCell) {
        return cell.getColumn() != otherCell.cell.getColumn();
    }

    private boolean rowDiffers(SolverCell otherCell) {
        return cell.getRow() != otherCell.cell.getRow();
    }

    private boolean blockDiffers(SolverCell otherCell) {
        return cell.getBlock() != otherCell.cell.getBlock();
    }

    public boolean isEmpty() {
        return cell.isEmpty();
    }
}
