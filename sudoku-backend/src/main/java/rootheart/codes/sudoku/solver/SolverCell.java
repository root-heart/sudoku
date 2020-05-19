package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

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
        candidates.addAll(board.getPossibleValues());
    }

    public void addOtherCellInColumn(SolverCell cell) {
        emptyCellsInColumn.add(cell);
        if (blockDiffers(cell)) {
            emptyCellsInSameColumnInOtherBlocks.add(cell);
        }
    }

    public void addOtherCellInRow(SolverCell cell) {
        emptyCellsInRow.add(cell);
        if (blockDiffers(cell)) {
            emptyCellsInSameRowInOtherBlocks.add(cell);
        }
    }

    public void addOtherCellInBlock(SolverCell cell) {
        emptyCellsInBlock.add(cell);
        if (rowDiffers(cell)) {
            emptyCellsInSameBlockInOtherRows.add(cell);
        }
        if (columnDiffers(cell)) {
            emptyCellsInSameBlockInOtherColumns.add(cell);
        }
    }

    public void eliminateImpossibleCandidates() {
        revealHiddenSingle();
        eliminateLockedCandidates();
        eliminateNakedTwins();
        if (candidates.hasOneNumber()) {
            // TODO here some cells will be updated multiple times
            emptyCellsInColumn.getEmptyCells().forEach(c -> c.getCandidates().removeAll(candidates));
            emptyCellsInRow.getEmptyCells().forEach(c -> c.getCandidates().removeAll(candidates));
            emptyCellsInBlock.getEmptyCells().forEach(c -> c.getCandidates().removeAll(candidates));
        }
    }

    void eliminateLockedCandidates() {
        emptyCellsInSameBlockInOtherRows.updateCandidates();
        emptyCellsInSameBlockInOtherColumns.updateCandidates();
        emptyCellsInSameColumnInOtherBlocks.updateCandidates();
        emptyCellsInSameRowInOtherBlocks.updateCandidates();

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
        emptyCellsInColumn.updateCandidates();
        emptyCellsInRow.updateCandidates();
        emptyCellsInBlock.updateCandidates();
        int hiddenSingle = 0;
        NumberSet n = new NumberSet();
        n.addAll(candidates);
        n.removeAll(emptyCellsInColumn.getCandidates());
        n.removeAll(emptyCellsInRow.getCandidates());
        n.removeAll(emptyCellsInBlock.getCandidates());
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
            removeCandidatesFromAllCellsIfATwinExists(emptyCellsInColumn);
            removeCandidatesFromAllCellsIfATwinExists(emptyCellsInRow);
            removeCandidatesFromAllCellsIfATwinExists(emptyCellsInBlock);
        }
    }

    private void removeCandidatesFromAllCellsIfATwinExists(SolverCellCollection cells) {
        SolverCell twin = cells.findExactlyOneCellWithCandidates(candidates);
        if (twin != null) {
            for (SolverCell otherCell : cells.getEmptyCells()) {
                if (otherCell != twin) {
                    otherCell.getCandidates().removeAll(candidates);
                }
            }
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
