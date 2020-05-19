package rootheart.codes.sudoku.solver;

import lombok.Getter;
import org.eclipse.collections.api.iterator.IntIterator;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.function.Consumer;

public class SolverCell {
    @Getter
    private final Cell cell;
    @Getter
    private final SolverCellCollection otherCellsInColumn = new SolverCellCollection();
    @Getter
    private final SolverCellCollection otherCellsInRow = new SolverCellCollection();
    @Getter
    private final SolverCellCollection otherCellsInBlock = new SolverCellCollection();

    private final MutableIntSet candidates = IntSets.mutable.empty();
    private SolverCellCollection emptyCellsInSameBlockInOtherRows;
    private SolverCellCollection emptyCellsInSameRowInOtherBlocks;
    private SolverCellCollection emptyCellsInSameBlockInOtherColumns;
    private SolverCellCollection emptyCellsInSameColumnInOtherBlocks;

    public SolverCell(Cell cell, Board board) {
        this.cell = cell;
        if (cell.isEmpty()) {
            candidates.addAll(board.getPossibleValues());
        }
    }

    public void initializationComplete() {
        emptyCellsInSameBlockInOtherRows = otherCellsInBlock.createNewWithFilteredEmptyCells(this::rowDiffers);
        emptyCellsInSameRowInOtherBlocks = otherCellsInRow.createNewWithFilteredEmptyCells(this::blockDiffers);
        emptyCellsInSameBlockInOtherColumns = otherCellsInBlock.createNewWithFilteredEmptyCells(this::columnDiffers);
        emptyCellsInSameColumnInOtherBlocks = otherCellsInColumn.createNewWithFilteredEmptyCells(this::blockDiffers);
    }

    public void eliminateImpossibleCandidates() {
        if (!isEmpty()) {
            return;
        }
        eliminateCandidatesThatAreSetInBuddyCells();
        revealHiddenSingle();
        eliminateLockedCandidates();
        eliminateNakedTwins();
        if (hasOneCandidate()) {
            forAllOtherCells(g -> g.removeCandidates(candidates));
        }
    }

    public boolean hasOneCandidate() {
        return candidates.size() == 1;
    }

    public int getFirstCandidate() {
        return candidates.intIterator().next();
    }

    public boolean isEmpty() {
        return cell.isEmpty();
    }

    public void removeCandidate(int candidate) {
        candidates.remove(candidate);
    }

    public void removeCandidates(IntSet candidates) {
        this.candidates.removeAll(candidates);
    }

    public int getCandidateCount() {
        return candidates.size();
    }

    public boolean containsCandidate(int candidate) {
        return candidates.contains(candidate);
    }

    private void eliminateCandidatesThatAreSetInBuddyCells() {
        forAllOtherCells(g -> g.getNumbers().forEach(this::removeCandidate));
    }

    private void eliminateLockedCandidates() {
        // Für jeden Kandidaten schauen, ob er in einer Zelle einer anderen Zeile/Spalte in diesem Block existiert.
        // Falls nein, den Kandidaten für alle Zellen dieser Zeile/Spalte in anderen Blöcken löschen
        candidates.forEach(candidate -> {
            if (emptyCellsInSameBlockInOtherRows.noCellContainsCandidate(candidate)) {
                emptyCellsInSameRowInOtherBlocks.removeCandidate(candidate);
            }
            if (emptyCellsInSameRowInOtherBlocks.noCellContainsCandidate(candidate)) {
                emptyCellsInSameBlockInOtherRows.removeCandidate(candidate);
            }
            if (emptyCellsInSameBlockInOtherColumns.noCellContainsCandidate(candidate)) {
                emptyCellsInSameColumnInOtherBlocks.removeCandidate(candidate);
            }
            if (emptyCellsInSameColumnInOtherBlocks.noCellContainsCandidate(candidate)) {
                emptyCellsInSameBlockInOtherColumns.removeCandidate(candidate);
            }
        });
    }

    private void revealHiddenSingle() {
        int hiddenSingle = 0;
        for (IntIterator it = candidates.intIterator(); it.hasNext(); ) {
            int candidate = it.next();
            if (otherCellsInColumn.noCellContainsCandidate(candidate)
                    || otherCellsInRow.noCellContainsCandidate(candidate)
                    || otherCellsInBlock.noCellContainsCandidate(candidate)) {
                if (hiddenSingle != 0) {
                    throw new NoSolutionException("multiple values can only exist in this cell, this is not possible");
                }
                hiddenSingle = candidate;
            }
        }

        if (hiddenSingle != 0) {
            candidates.clear();
            candidates.add(hiddenSingle);
        }
    }

    private void eliminateNakedTwins() {
        if (candidates.size() == 2) {
            forAllOtherCells(otherCells -> {
                SolverCell twin = findTwin(otherCells);
                removeCandidatesExceptFromTwin(otherCells, twin);
            });
        }
    }

    private SolverCell findTwin(SolverCellCollection cells) {
        SolverCell twin = null;
        for (SolverCell otherCell : cells.getEmptyCells()) {
            if (otherCell.candidates.equals(candidates)) {
                if (twin != null) {
                    throw new NoSolutionException("more than two cells only allow the same two numbers, this is not possible");
                }
                twin = otherCell;
            }
        }
        return twin;
    }

    private void removeCandidatesExceptFromTwin(SolverCellCollection otherCells, SolverCell twin) {
        if (twin != null) {
            for (SolverCell otherCell : otherCells.getEmptyCells()) {
                if (otherCell != twin) {
                    otherCell.removeCandidates(candidates);
                }
            }
        }
    }

    private void forAllOtherCells(Consumer<SolverCellCollection> consumer) {
        consumer.accept(otherCellsInColumn);
        consumer.accept(otherCellsInRow);
        consumer.accept(otherCellsInBlock);
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
