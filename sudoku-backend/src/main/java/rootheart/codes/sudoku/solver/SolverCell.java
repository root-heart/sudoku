package rootheart.codes.sudoku.solver;

import lombok.Getter;
import org.eclipse.collections.api.iterator.IntIterator;
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
    @Getter
    private final NumberSet candidates = new NumberSet();
    private SolverCellCollection emptyCellsInSameBlockInOtherRows;
    private SolverCellCollection emptyCellsInSameRowInOtherBlocks;
    private SolverCellCollection emptyCellsInSameBlockInOtherColumns;
    private SolverCellCollection emptyCellsInSameColumnInOtherBlocks;

    private final Board board;

    public SolverCell(Cell cell, Board board) {
        this.cell = cell;
        this.board = board;
        if (cell.isEmpty()) {
            board.getPossibleValues().forEach(candidates::add);
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
        if (candidates.hasOneNumber()) {
            forAllOtherCells(g -> g.removeCandidates(this));
        }
    }

    public boolean isEmpty() {
        return cell.isEmpty();
    }

    private void eliminateCandidatesThatAreSetInBuddyCells() {
        forAllOtherCells(g -> candidates.removeAll(g.getNumbers()));
    }

    private void eliminateLockedCandidates() {
        // Für jeden Kandidaten schauen, ob er in einer Zelle einer anderen Zeile/Spalte in diesem Block existiert.
        // Falls nein, den Kandidaten für alle Zellen dieser Zeile/Spalte in anderen Blöcken löschen
        board.getPossibleValues().forEach(candidate -> {
            if (candidates.contains(candidate)) {
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
            }
        });
    }

    private void revealHiddenSingle() {
        int hiddenSingle = 0;
        IntIterator it = board.getPossibleValues().intIterator();
        while (it.hasNext()) {
            int candidate = it.next();
            if (!candidates.contains(candidate)) {
                continue;
            }
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
            candidates.removeAllAndAdd(hiddenSingle);
        }
    }

    private void eliminateNakedTwins() {
        if (candidates.getCount() == 2) {
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
                    otherCell.candidates.removeAll(candidates);
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
