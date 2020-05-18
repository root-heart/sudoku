package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Getter
public class SolverCell {
    private final Cell cell;
    private final Set<Integer> candidates = new HashSet<>();
    private final SolverCellCollection otherCellsInColumn = new SolverCellCollection();
    private final SolverCellCollection otherCellsInRow = new SolverCellCollection();
    private final SolverCellCollection otherCellsInBlock = new SolverCellCollection();

    public SolverCell(Cell cell, Board board) {
        this.cell = cell;
        if (cell.isEmpty()) {
            candidates.addAll(board.getPossibleValues());
        }
    }

    public void eliminateImpossibleCandidates() {
        eliminateCandidatesThatAreSetInBuddyCells();
        revealHiddenSingle();
        eliminateLockedCandidates();
        eliminateNakedTwins();
        if (hasOneCandidate()) {
            forAllOtherCells(g -> g.removeCandidates(candidates));
        }
    }

    private void forAllOtherCells(Consumer<SolverCellCollection> consumer) {
        consumer.accept(otherCellsInColumn);
        consumer.accept(otherCellsInRow);
        consumer.accept(otherCellsInBlock);
    }

    private void eliminateCandidatesThatAreSetInBuddyCells() {
        forAllOtherCells(g -> g.streamNumbers().forEach(candidates::remove));
    }

    private void eliminateLockedCandidates() {
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

    public boolean isEmpty() {
        return cell.isEmpty();
    }

    private void revealHiddenSingle() {
        List<Integer> hiddenSingles = new ArrayList<>();
        for (Integer candidate : candidates) {
            if (!otherCellsInColumn.anyCellContainsCandidate(candidate)
                    || !otherCellsInRow.anyCellContainsCandidate(candidate)
                    || !otherCellsInBlock.anyCellContainsCandidate(candidate)) {
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

    private void eliminateNakedTwins() {
        if (candidates.size() == 2) {
            forAllOtherCells(otherCells -> otherCells
                    .findSingleCellWithCandidates(candidates)
                    .map(otherCells::streamEmptyCellsExcept)
                    .orElse(Stream.empty())
                    .forEach(it -> it.getCandidates().removeAll(candidates)));
        }
    }

    private Stream<SolverCell> getEmptyCellsInSameBlockInOtherRows() {
        return otherCellsInBlock.streamEmptyCellsWhere(this::rowDiffers);
    }

    private Stream<SolverCell> getEmptyCellsInSameRowInOtherBlocks() {
        return otherCellsInRow.streamEmptyCellsWhere(this::blockDiffers);
    }

    private Stream<SolverCell> getEmptyCellsInSameBlockInOtherColumns() {
        return otherCellsInBlock.streamEmptyCellsWhere(this::columnDiffers);
    }

    private Stream<SolverCell> getEmptyCellsInSameColumnInOtherBlocks() {
        return otherCellsInColumn.streamEmptyCellsWhere(this::blockDiffers);
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
}
