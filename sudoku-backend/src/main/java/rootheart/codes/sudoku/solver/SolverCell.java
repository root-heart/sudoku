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

    private int candidates;
    private SolverCellCollection emptyCellsInSameBlockInOtherRows;
    private SolverCellCollection emptyCellsInSameRowInOtherBlocks;
    private SolverCellCollection emptyCellsInSameBlockInOtherColumns;
    private SolverCellCollection emptyCellsInSameColumnInOtherBlocks;

    private final Board board;

    public SolverCell(Cell cell, Board board) {
        this.cell = cell;
        this.board = board;
        if (cell.isEmpty()) {
            board.getPossibleValues().forEach(v -> candidates |= 1 << v);
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
            forAllOtherCells(g -> g.removeCandidates(this));
        }
    }

    public boolean hasOneCandidate() {
        return (candidates & (candidates - 1)) == 0;
    }

    public int getFirstCandidate() {
        if (candidates == 0) {
            return 0;
        }

        for (int candidateTest = 1; candidateTest < 32; candidateTest++) {
            if (containsCandidate(candidateTest)) {
                return candidateTest;
            }
        }
        return 0;
    }

    public boolean isEmpty() {
        return cell.isEmpty();
    }

    public void removeCandidate(int candidate) {
        candidates &= ~(1 << candidate);
    }

    private void removeBinaryEncodedCandidates(int binaryEncodedNumbers) {
        candidates &= ~binaryEncodedNumbers;
    }

    public void removeCandidates(SolverCell otherCell) {
        removeBinaryEncodedCandidates(otherCell.candidates);
    }

    public int getCandidateCount() {
        int count = 0;
        int n = candidates;
        while (n > 0) {
            count += n & 1;
            n >>= 1;
        }
        return count;
    }

    public boolean containsCandidate(int candidate) {
        return (candidates & 1 << candidate) > 0;
    }

    private void eliminateCandidatesThatAreSetInBuddyCells() {
        forAllOtherCells(g -> removeBinaryEncodedCandidates(g.getNumbers()));
    }

    private void eliminateLockedCandidates() {
        // Für jeden Kandidaten schauen, ob er in einer Zelle einer anderen Zeile/Spalte in diesem Block existiert.
        // Falls nein, den Kandidaten für alle Zellen dieser Zeile/Spalte in anderen Blöcken löschen
        board.getPossibleValues().forEach(candidate -> {
            if (containsCandidate(candidate)) {
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
            if (!containsCandidate(candidate)) {
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
            candidates = 1 << hiddenSingle;
        }
    }

    private void eliminateNakedTwins() {
        if (getCandidateCount() == 2) {
            forAllOtherCells(otherCells -> {
                SolverCell twin = findTwin(otherCells);
                removeCandidatesExceptFromTwin(otherCells, twin);
            });
        }
    }

    private SolverCell findTwin(SolverCellCollection cells) {
        SolverCell twin = null;
        for (SolverCell otherCell : cells.getEmptyCells()) {
            if (otherCell.candidates == candidates) {
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
                    otherCell.removeBinaryEncodedCandidates(candidates);
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
