package rootheart.codes.sudoku.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import rootheart.codes.sudoku.solver.NoSolutionException;
import rootheart.codes.sudoku.solver.NumberSet;

@Getter
@RequiredArgsConstructor
public class Cell {
    private final Group column;
    private final Group row;
    private final Group block;
    @Setter
    private int number;

    private final NumberSet candidatesInColumn = new NumberSet();
    private final NumberSet candidatesInRow = new NumberSet();
    private final NumberSet candidatesInBlock = new NumberSet();

    @Getter
    private final NumberSet candidates = new NumberSet();
    private final CellsInSameBlockInOtherColumns cellsInSameBlockInOtherColumns = new CellsInSameBlockInOtherColumns();
    private final CellsInSameBlockInOtherRows cellsInSameBlockInOtherRows = new CellsInSameBlockInOtherRows();
    private final CellsInSameColumnInOtherBlocks cellsInSameColumnInOtherBlocks = new CellsInSameColumnInOtherBlocks();
    private final CellsInSameRowInOtherBlocks cellsInSameRowInOtherBlocks = new CellsInSameRowInOtherBlocks();

    public boolean isEmpty() {
        return number == 0;
    }

    public void setNumber() {
        setNumber(candidates.getFirst());
    }

    public void eliminateImpossibleCandidates() {
        revealHiddenSingle();
        eliminateNakedTwins();
        eliminateLockedCandidates();
        if (candidates.hasOneNumber()) {
            // TODO here some cells will be updated multiple times
            removeThisCandidatesInAll(column);
            removeThisCandidatesInAll(row);
            removeThisCandidatesInAll(block);
        }
    }

    private void removeThisCandidatesInAll(Group group) {
        for (Cell cell : group.getCells()) {
            if (this != cell) {
                cell.candidates.removeAll(candidates);
            }
        }
    }

    private abstract class OtherCells {
        final NumberSet otherCellsCandidates = new NumberSet();

        abstract boolean test(Cell cell);

        abstract Group getGroup();

        abstract void removeCandidateInOtherGroup(int c);

        boolean cellIsAnother(Cell cell) {
            return cell != Cell.this && cell.isEmpty() && test(cell);
        }

        void removeCandidateFromCells(int c) {
            for (Cell cell : getGroup().getCells()) {
                if (cellIsAnother(cell)) {
                    cell.candidates.remove(c);
                }
            }
        }

        void updateCandidates() {
            otherCellsCandidates.clear();
            for (Cell cell : getGroup().getCells()) {
                if (cellIsAnother(cell)) {
                    otherCellsCandidates.addAll(cell.getCandidates());
                }
            }
        }
    }

    private class CellsInSameBlockInOtherRows extends OtherCells {
        @Override
        boolean test(Cell cell) {
            return rowDiffers(cell);
        }

        @Override
        Group getGroup() {
            return block;
        }

        @Override
        void removeCandidateInOtherGroup(int c) {
            cellsInSameRowInOtherBlocks.removeCandidateFromCells(c);
        }
    }

    private class CellsInSameBlockInOtherColumns extends OtherCells {
        @Override
        boolean test(Cell cell) {
            return columnDiffers(cell);
        }

        @Override
        Group getGroup() {
            return block;
        }

        @Override
        void removeCandidateInOtherGroup(int c) {
            cellsInSameColumnInOtherBlocks.removeCandidateFromCells(c);
        }
    }

    private class CellsInSameRowInOtherBlocks extends OtherCells {
        @Override
        boolean test(Cell cell) {
            return blockDiffers(cell);
        }

        @Override
        Group getGroup() {
            return row;
        }

        @Override
        void removeCandidateInOtherGroup(int c) {
            cellsInSameBlockInOtherRows.removeCandidateFromCells(c);
        }
    }

    private class CellsInSameColumnInOtherBlocks extends OtherCells {
        @Override
        boolean test(Cell cell) {
            return blockDiffers(cell);
        }

        @Override
        Group getGroup() {
            return column;
        }

        @Override
        void removeCandidateInOtherGroup(int c) {
            cellsInSameBlockInOtherColumns.removeCandidateFromCells(c);
        }
    }

    private final NumberSet candidateClone = new NumberSet();

    void eliminateLockedCandidates() {
        cellsInSameBlockInOtherRows.updateCandidates();
        cellsInSameBlockInOtherColumns.updateCandidates();
        cellsInSameColumnInOtherBlocks.updateCandidates();
        cellsInSameRowInOtherBlocks.updateCandidates();
        candidates.forEach(candidate -> {
            if (!cellsInSameBlockInOtherRows.otherCellsCandidates.contains(candidate)) {
                cellsInSameBlockInOtherRows.removeCandidateInOtherGroup(candidate);
            }
            if (!cellsInSameRowInOtherBlocks.otherCellsCandidates.contains(candidate)) {
                cellsInSameRowInOtherBlocks.removeCandidateInOtherGroup(candidate);
            }
            if (!cellsInSameBlockInOtherColumns.otherCellsCandidates.contains(candidate)) {
                cellsInSameBlockInOtherColumns.removeCandidateInOtherGroup(candidate);
            }
            if (!cellsInSameColumnInOtherBlocks.otherCellsCandidates.contains(candidate)) {
                cellsInSameColumnInOtherBlocks.removeCandidateInOtherGroup(candidate);
            }
        });
    }

    void revealHiddenSingle() {
        updateCandidates(candidatesInColumn, column);
        updateCandidates(candidatesInRow, row);
        updateCandidates(candidatesInBlock, block);
        candidateClone.set(candidates);
        candidateClone.removeAll(candidatesInColumn);
        candidateClone.removeAll(candidatesInRow);
        candidateClone.removeAll(candidatesInBlock);
        if (candidateClone.hasOneNumber()) {
            candidates.removeAllAndAdd(candidateClone.getFirst());
        } else if (!candidateClone.isEmpty()) {
            throw new NoSolutionException("multiple values can only exist in this cell, this is not possible");
        }
    }

    private void updateCandidates(NumberSet candidates, Group group) {
        candidates.clear();
        for (Cell cell : group.getCells()) {
            if (this != cell) {
                candidates.addAll(cell.candidates);
            }
        }
    }

    void eliminateNakedTwins() {
        if (candidates.getCount() == 2) {
            removeTwinFromOthers(column);
            removeTwinFromOthers(row);
            removeTwinFromOthers(block);
        }
    }

    private void removeTwinFromOthers(Group group) {
        Cell twin = null;
        for (Cell cell : group.getCells()) {
            if (cell != this && cell.getCandidates().equals(candidates)) {
                if (twin != null) {
                    throw new NoSolutionException("more than two cells only allow the same two numbers, this is not possible");
                }
                twin = cell;
            }
        }
        if (twin != null) {
            for (Cell cell : group.getCells()) {
                if (cell != this && cell != twin) {
                    cell.candidates.removeAll(candidates);
                }
            }
        }
    }

    private boolean columnDiffers(Cell otherCell) {
        return getColumn() != otherCell.getColumn();
    }

    private boolean rowDiffers(Cell otherCell) {
        return getRow() != otherCell.getRow();
    }

    private boolean blockDiffers(Cell otherCell) {
        return getBlock() != otherCell.getBlock();
    }
}
