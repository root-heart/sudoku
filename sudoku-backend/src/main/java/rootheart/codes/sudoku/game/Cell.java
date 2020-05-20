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

    private final GroupCells cellsInColumn = new ColumnCells();
    private final GroupCells cellsInRow = new RowCells();
    private final GroupCells cellsInBlock = new BlockCells();
    @Getter
    private final NumberSet candidates = new NumberSet();
    private final CellList cellsInSameBlockInOtherRows = new CellList(10);
    private final CellList cellsInSameRowInOtherBlocks = new CellList(10);
    private final CellList cellsInSameBlockInOtherColumns = new CellList(10);
    private final CellList cellsInSameColumnInOtherBlocks = new CellList(10);

    public boolean isEmpty() {
        return number == 0;
    }

    public void updateBuddyCells() {
        addCellsFromGroup(cellsInColumn);
        addCellsFromGroup(cellsInRow);
        addCellsFromGroup(cellsInBlock);
    }

    public void setNumber() {
        setNumber(candidates.getFirst());
    }

    private void addCellsFromGroup(GroupCells groupCells) {
        for (Cell groupCell : groupCells.getGroup().getCells()) {
            if (groupCell != this) {
                if (groupCell.isEmpty()) {
                    groupCells.add(groupCell);
                } else {
                    candidates.remove(groupCell.getNumber());
                }
            }
        }
    }


    public void eliminateImpossibleCandidates() {
        revealHiddenSingle();
        eliminateLockedCandidates();
        eliminateNakedTwins();
        if (candidates.hasOneNumber()) {
            // TODO here some cells will be updated multiple times
            cellsInColumn.getCells().forEach(c -> c.candidates.removeAll(candidates));
            cellsInRow.getCells().forEach(c -> c.candidates.removeAll(candidates));
            cellsInBlock.getCells().forEach(c -> c.candidates.removeAll(candidates));
        }
    }

    // slowest 8-10µs
    void eliminateLockedCandidates() {
        // 4-7µs
        cellsInSameBlockInOtherRows.updateCandidates();
        cellsInSameBlockInOtherColumns.updateCandidates();
        cellsInSameColumnInOtherBlocks.updateCandidates();
        cellsInSameRowInOtherBlocks.updateCandidates();

        // Für jeden Kandidaten schauen, ob er in einer Zelle einer anderen Zeile/Spalte in diesem Block existiert.
        // Falls nein, den Kandidaten für alle Zellen dieser Zeile/Spalte in anderen Blöcken löschen
        candidates.forEach(candidate -> {
            if (!cellsInSameBlockInOtherRows.getCandidates().contains(candidate)) {
                cellsInSameRowInOtherBlocks.removeCandidate(candidate);
            }
            if (!cellsInSameRowInOtherBlocks.getCandidates().contains(candidate)) {
                cellsInSameBlockInOtherRows.removeCandidate(candidate);
            }
            if (!cellsInSameBlockInOtherColumns.getCandidates().contains(candidate)) {
                cellsInSameColumnInOtherBlocks.removeCandidate(candidate);
            }
            if (!cellsInSameColumnInOtherBlocks.getCandidates().contains(candidate)) {
                cellsInSameBlockInOtherColumns.removeCandidate(candidate);
            }
        });
    }

    // medium 1-4µs
    void revealHiddenSingle() {
        cellsInColumn.updateCandidates();
        cellsInRow.updateCandidates();
        cellsInBlock.updateCandidates();
        NumberSet n = new NumberSet(candidates);
        n.removeAll(cellsInColumn.getCandidates());
        n.removeAll(cellsInRow.getCandidates());
        n.removeAll(cellsInBlock.getCandidates());
        if (n.getCount() == 1) {
            candidates.removeAllAndAdd(n.getFirst());
        } else if (n.getCount() > 1) {
            throw new NoSolutionException("multiple values can only exist in this cell, this is not possible");
        }
    }

    // fastest <2µs
    void eliminateNakedTwins() {
        if (candidates.getCount() == 2) {
            removeCandidatesFromAllCellsIfATwinExists(cellsInColumn);
            removeCandidatesFromAllCellsIfATwinExists(cellsInRow);
            removeCandidatesFromAllCellsIfATwinExists(cellsInBlock);
        }
    }

    private void removeCandidatesFromAllCellsIfATwinExists(CellList cells) {
        Cell twin = cells.findExactlyOneCellWithCandidates(candidates);
        if (twin != null) {
            for (Cell otherCell : cells.getCells()) {
                if (otherCell != twin) {
                    otherCell.candidates.removeAll(candidates);
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

    private abstract static class GroupCells extends CellList {
        public GroupCells() {
            super(10);
        }

        public abstract Group getGroup();
    }

    private class ColumnCells extends GroupCells {
        @Override
        public Group getGroup() {
            return column;
        }

        @Override
        public void add(Cell cell) {
            super.add(cell);
            if (blockDiffers(cell)) {
                cellsInSameColumnInOtherBlocks.add(cell);
            }
        }
    }

    private class RowCells extends GroupCells {
        @Override
        public Group getGroup() {
            return row;
        }

        @Override
        public void add(Cell cell) {
            super.add(cell);
            if (blockDiffers(cell)) {
                cellsInSameRowInOtherBlocks.add(cell);
            }
        }
    }


    private class BlockCells extends GroupCells {
        @Override
        public Group getGroup() {
            return block;
        }

        @Override
        public void add(Cell cell) {
            super.add(cell);
            if (rowDiffers(cell)) {
                cellsInSameBlockInOtherRows.add(cell);
            }
            if (columnDiffers(cell)) {
                cellsInSameBlockInOtherColumns.add(cell);
            }
        }
    }
}
