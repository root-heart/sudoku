package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class SolverCell {
    private final Cell cell;
    private final Set<Integer> candidates;
    private final List<SolverCell> otherCellsInColumn = new ArrayList<>();
    private final List<SolverCell> otherCellsInRow = new ArrayList<>();
    private final List<SolverCell> otherCellsInBlock = new ArrayList<>();

    public SolverCell(Cell cell, Board board) {
        this.cell = cell;
        candidates = new HashSet<>();
        if (cell.isEmpty()) {
            candidates.addAll(board.getPossibleValues());
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

    public Stream<SolverCell> getEmptyCellsInSameBlockInOtherRows() {
        return otherCellsInBlock.stream()
                .filter(otherCell -> otherCell.cell.isEmpty())
                .filter(otherCell -> otherCell.cell.getRow() != cell.getRow());
    }

    public Stream<SolverCell> getEmptyCellsInSameRowInOtherBlocks() {
        return otherCellsInRow.stream()
                .filter(otherCell -> otherCell.cell.isEmpty())
                .filter(otherCell -> otherCell.cell.getBlock() != cell.getBlock());
    }

    public Stream<SolverCell> getEmptyCellsInSameBlockInOtherColumns() {
        return otherCellsInBlock.stream()
                .filter(otherCell -> otherCell.cell.isEmpty())
                .filter(otherCell -> otherCell.cell.getColumn() != cell.getColumn());
    }

    public Stream<SolverCell> getEmptyCellsInSameColumnInOtherBlocks() {
        return otherCellsInColumn.stream()
                .filter(otherCell -> otherCell.cell.isEmpty())
                .filter(otherCell -> otherCell.cell.getBlock() != cell.getBlock());
    }

    public boolean hasOneCandidate() {
        return candidates.size() == 1;
    }

    public Integer getFirstCandidate() {
        return candidates.iterator().next();
    }

    public Integer findHiddenSingle() {
        List<Integer> hiddenSingles = new ArrayList<>();
        for (Integer candidate : candidates) {
            if (!isPresentInOtherCells(candidate, otherCellsInColumn)
                    || !isPresentInOtherCells(candidate, otherCellsInRow)
                    || !isPresentInOtherCells(candidate, otherCellsInBlock)) {
                hiddenSingles.add(candidate);
            }
        }
        if (hiddenSingles.size() > 1) {
            throw new Solver.NoSolutionException("multiple values can only exist in this cell, this is not possible");
        }
        return hiddenSingles.size() == 0 ? null : hiddenSingles.get(0);
    }

    public void eliminateNakedTwins() {
        if (candidates.size() == 2) {
            eliminateNakedTwinsInGroup(otherCellsInRow);
            eliminateNakedTwinsInGroup(otherCellsInColumn);
            eliminateNakedTwinsInGroup(otherCellsInBlock);
        }
    }

    private boolean isPresentInOtherCells(Integer candidate, List<SolverCell> otherCells) {
        return otherCells
                .stream()
                .anyMatch(otherCell -> otherCell.getCandidates().contains(candidate));
    }

    private void eliminateNakedTwinsInGroup(List<SolverCell> otherCellsInGroup) {
        otherCellsInGroup
                .stream()
                .filter(otherCell -> otherCell.getCandidates().equals(candidates))
                .findAny()
                .ifPresent(otherCell -> {
                    otherCellsInGroup
                            .stream()
                            .filter(x -> otherCell != x)
                            .forEach(x -> x.getCandidates().removeAll(candidates));
                });
    }
}
