package rootheart.codes.sudoku.solver;

import lombok.Getter;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;
import rootheart.codes.sudoku.game.Group;

public class SolverCell {
//    private final Cell cell;
//    private final GroupCells cellsInColumn = new ColumnCells();
//    private final GroupCells cellsInRow = new RowCells();
//    private final GroupCells cellsInBlock = new BlockCells();
//    @Getter
//    private final NumberSet candidates = new NumberSet();
//    private final SolverCellCollection cellsInSameBlockInOtherRows = new SolverCellCollection();
//    private final SolverCellCollection cellsInSameRowInOtherBlocks = new SolverCellCollection();
//    private final SolverCellCollection cellsInSameBlockInOtherColumns = new SolverCellCollection();
//    private final SolverCellCollection cellsInSameColumnInOtherBlocks = new SolverCellCollection();
//
//    public SolverCell(Cell cell, Board board) {
//        this.cell = cell;
//        cell.setSolverCell(this);
//        candidates.addAll(board.getPossibleValues());
//    }
//
//    public void updateBuddyCells() {
//        addCellsFromGroup(cellsInColumn);
//        addCellsFromGroup(cellsInRow);
//        addCellsFromGroup(cellsInBlock);
//    }
//
//    public void setNumber() {
//        cell.setNumber(candidates.getFirst());
//    }
//
//    public void eliminateImpossibleCandidates() {
//        revealHiddenSingle();
//        eliminateLockedCandidates();
//        eliminateNakedTwins();
//        if (candidates.hasOneNumber()) {
//            // TODO here some cells will be updated multiple times
//            cellsInColumn.getCells().forEach(c -> c.candidates.removeAll(candidates));
//            cellsInRow.getCells().forEach(c -> c.candidates.removeAll(candidates));
//            cellsInBlock.getCells().forEach(c -> c.candidates.removeAll(candidates));
//        }
//    }
//
//    // slowest 8-10µs
//    void eliminateLockedCandidates() {
//        // 4-7µs
//        cellsInSameBlockInOtherRows.updateCandidates();
//        cellsInSameBlockInOtherColumns.updateCandidates();
//        cellsInSameColumnInOtherBlocks.updateCandidates();
//        cellsInSameRowInOtherBlocks.updateCandidates();
//
//        // Für jeden Kandidaten schauen, ob er in einer Zelle einer anderen Zeile/Spalte in diesem Block existiert.
//        // Falls nein, den Kandidaten für alle Zellen dieser Zeile/Spalte in anderen Blöcken löschen
//        candidates.forEach(candidate -> {
//            if (!cellsInSameBlockInOtherRows.getCandidates().contains(candidate)) {
//                cellsInSameRowInOtherBlocks.removeCandidate(candidate);
//            }
//            if (!cellsInSameRowInOtherBlocks.getCandidates().contains(candidate)) {
//                cellsInSameBlockInOtherRows.removeCandidate(candidate);
//            }
//            if (!cellsInSameBlockInOtherColumns.getCandidates().contains(candidate)) {
//                cellsInSameColumnInOtherBlocks.removeCandidate(candidate);
//            }
//            if (!cellsInSameColumnInOtherBlocks.getCandidates().contains(candidate)) {
//                cellsInSameBlockInOtherColumns.removeCandidate(candidate);
//            }
//        });
//    }
//
//    // medium 1-4µs
//    void revealHiddenSingle() {
//        cellsInColumn.updateCandidates();
//        cellsInRow.updateCandidates();
//        cellsInBlock.updateCandidates();
//        NumberSet n = new NumberSet(candidates);
//        n.removeAll(cellsInColumn.getCandidates());
//        n.removeAll(cellsInRow.getCandidates());
//        n.removeAll(cellsInBlock.getCandidates());
//        if (n.getCount() == 1) {
//            candidates.removeAllAndAdd(n.getFirst());
//        } else if (n.getCount() > 1) {
//            throw new NoSolutionException("multiple values can only exist in this cell, this is not possible");
//        }
//    }
//
//    // fastest <2µs
//    void eliminateNakedTwins() {
//        if (candidates.getCount() == 2) {
//            removeCandidatesFromAllCellsIfATwinExists(cellsInColumn);
//            removeCandidatesFromAllCellsIfATwinExists(cellsInRow);
//            removeCandidatesFromAllCellsIfATwinExists(cellsInBlock);
//        }
//    }
//
//    private void addCellsFromGroup(GroupCells groupCells) {
//        for (Cell groupCell : groupCells.getGroup().getCells()) { // 3µs
//            if (groupCell != cell) {
//                if (groupCell.isEmpty()) {
//                    groupCells.add(groupCell.getSolverCell());
//                } else {
//                    candidates.remove(groupCell.getNumber()); // <=1µs
//                }
//            }
//        }
//    }
//
//    private void removeCandidatesFromAllCellsIfATwinExists(SolverCellCollection cells) {
//        SolverCell twin = cells.findExactlyOneCellWithCandidates(candidates);
//        if (twin != null) {
//            for (SolverCell otherCell : cells.getCells()) {
//                if (otherCell != twin) {
//                    otherCell.candidates.removeAll(candidates);
//                }
//            }
//        }
//    }
//
//    private boolean columnDiffers(SolverCell otherCell) {
//        return cell.getColumn() != otherCell.cell.getColumn();
//    }
//
//    private boolean rowDiffers(SolverCell otherCell) {
//        return cell.getRow() != otherCell.cell.getRow();
//    }
//
//    private boolean blockDiffers(SolverCell otherCell) {
//        return cell.getBlock() != otherCell.cell.getBlock();
//    }
//
//    private abstract static class GroupCells extends SolverCellCollection {
//        public abstract Group getGroup();
//    }
//
//    private class ColumnCells extends GroupCells {
//        @Override
//        public Group getGroup() {
//            return cell.getColumn();
//        }
//
//        @Override
//        public void add(SolverCell cell) {
//            super.add(cell);
//            if (blockDiffers(cell)) {
//                cellsInSameColumnInOtherBlocks.add(cell);
//            }
//        }
//    }
//
//    private class RowCells extends GroupCells {
//        @Override
//        public Group getGroup() {
//            return cell.getRow();
//        }
//
//        @Override
//        public void add(SolverCell cell) {
//            super.add(cell);
//            if (blockDiffers(cell)) {
//                cellsInSameRowInOtherBlocks.add(cell);
//            }
//        }
//    }
//
//
//    private class BlockCells extends GroupCells {
//        @Override
//        public Group getGroup() {
//            return cell.getBlock();
//        }
//
//        @Override
//        public void add(SolverCell cell) {
//            super.add(cell);
//            if (rowDiffers(cell)) {
//                cellsInSameBlockInOtherRows.add(cell);
//            }
//            if (columnDiffers(cell)) {
//                cellsInSameBlockInOtherColumns.add(cell);
//            }
//        }
//    }
}
