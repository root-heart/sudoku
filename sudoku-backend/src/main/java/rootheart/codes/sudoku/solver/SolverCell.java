package rootheart.codes.sudoku.solver;

import lombok.Getter;
import org.eclipse.collections.api.iterator.MutableIntIterator;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class SolverCell {
    private final Cell cell;
    private final MutableIntSet candidates = IntSets.mutable.empty();
    private final SolverCellCollection otherCellsInColumn = new SolverCellCollection();
    private final SolverCellCollection otherCellsInRow = new SolverCellCollection();
    private final SolverCellCollection otherCellsInBlock = new SolverCellCollection();
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
        forAllOtherCells(SolverCellCollection::initializationComplete);
        emptyCellsInSameBlockInOtherRows = new SolverCellCollection(otherCellsInBlock
                .streamEmptyCellsWhere(this::rowDiffers)
                .collect(Collectors.toSet()));
        emptyCellsInSameRowInOtherBlocks = new SolverCellCollection(otherCellsInRow
                .streamEmptyCellsWhere(this::blockDiffers)
                .collect(Collectors.toSet()));
        emptyCellsInSameBlockInOtherColumns = new SolverCellCollection(otherCellsInBlock
                .streamEmptyCellsWhere(this::columnDiffers)
                .collect(Collectors.toSet()));
        emptyCellsInSameColumnInOtherBlocks = new SolverCellCollection(otherCellsInColumn
                .streamEmptyCellsWhere(this::blockDiffers)
                .collect(Collectors.toSet()));
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
        forAllOtherCells(g -> g.getNumbers().forEach(candidates::remove));
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

    public boolean hasOneCandidate() {
        return candidates.size() == 1;
    }

    public int getFirstCandidate() {
        return candidates.intIterator().next();
    }

    public boolean isEmpty() {
        return cell.isEmpty();
    }

    private void revealHiddenSingle() {
        int hiddenSingle = 0;
        MutableIntIterator mutableIntIterator = candidates.intIterator();
        while (mutableIntIterator.hasNext()) {
            int candidate = mutableIntIterator.next();
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
            forAllOtherCells(otherCells -> otherCells
                    .findSingleCellWithCandidates(candidates)
                    .map(otherCells::streamAllOtherEmptyCells)
                    .orElse(Stream.empty())
                    .forEach(it -> it.getCandidates().removeAll(candidates)));
        }
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
