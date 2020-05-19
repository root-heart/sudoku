package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.function.Consumer;

public class SolverCell {
    @Getter
    private final Cell cell;
    private final SolverCellCollection emptyCellsInColumn = new SolverCellCollection();
    private final SolverCellCollection emptyCellsInRow = new SolverCellCollection();
    private final SolverCellCollection emptyCellsInBlock = new SolverCellCollection();
    @Getter
    private final NumberSet candidates = new NumberSet();
    private final SolverCellCollection emptyCellsInSameBlockInOtherRows = new SolverCellCollection();
    private final SolverCellCollection emptyCellsInSameRowInOtherBlocks = new SolverCellCollection();
    private final SolverCellCollection emptyCellsInSameBlockInOtherColumns = new SolverCellCollection();
    private final SolverCellCollection emptyCellsInSameColumnInOtherBlocks = new SolverCellCollection();

    public SolverCell(Cell cell, Board board) {
        this.cell = cell;
        if (cell.isEmpty()) {
            candidates.addAll(board.getPossibleValues());
        }
    }

    public void addOtherCellInColumn(SolverCell cell) {
        if (cell.isEmpty()) {
            emptyCellsInColumn.add(cell);
            if (blockDiffers(cell)) {
                emptyCellsInSameColumnInOtherBlocks.add(cell);
            }
        } else {
            candidates.remove(cell.getCell().getNumber());
        }
    }

    public void addOtherCellInRow(SolverCell cell) {
        if (cell.isEmpty()) {
            emptyCellsInRow.add(cell);
            if (blockDiffers(cell)) {
                emptyCellsInSameRowInOtherBlocks.add(cell);
            }
        } else {
            candidates.remove(cell.getCell().getNumber());
        }
    }

    public void addOtherCellInBlock(SolverCell cell) {
        if (cell.isEmpty()) {
            emptyCellsInBlock.add(cell);
            if (rowDiffers(cell)) {
                emptyCellsInSameBlockInOtherRows.add(cell);
            }
            if (columnDiffers(cell)) {
                emptyCellsInSameBlockInOtherColumns.add(cell);
            }
        } else {
            candidates.remove(cell.getCell().getNumber());
        }
    }

    public void eliminateImpossibleCandidates() {
        if (!isEmpty()) {
            return;
        }
        revealHiddenSingle();
        eliminateLockedCandidates();
        eliminateNakedTwins();
        if (candidates.hasOneNumber()) {
            // TODO here some cells will be updated multiple times
            forAllOtherCells(g -> g.removeCandidates(this));
        }
    }

    public boolean isEmpty() {
        return cell.isEmpty();
    }

    void eliminateLockedCandidates() {
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

    void revealHiddenSingle() {
        int hiddenSingle = 0;
        NumberSet n = new NumberSet();
        n.addAll(candidates);
        for (SolverCell otherCell : emptyCellsInColumn.getEmptyCells()) {
            n.removeAll(otherCell.candidates);
        }
        for (SolverCell otherCell : emptyCellsInRow.getEmptyCells()) {
            n.removeAll(otherCell.candidates);
        }
        for (SolverCell otherCell : emptyCellsInBlock.getEmptyCells()) {
            n.removeAll(otherCell.candidates);
        }
        if (n.getCount() > 1) {
            throw new NoSolutionException("multiple values can only exist in this cell, this is not possible");
        }
        if (n.getCount() == 1) {
            hiddenSingle = n.getFirst();
        }

        if (hiddenSingle != 0) {
            candidates.removeAllAndAdd(hiddenSingle);
        }
    }

    void eliminateNakedTwins() {
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
        consumer.accept(emptyCellsInColumn);
        consumer.accept(emptyCellsInRow);
        consumer.accept(emptyCellsInBlock);
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
