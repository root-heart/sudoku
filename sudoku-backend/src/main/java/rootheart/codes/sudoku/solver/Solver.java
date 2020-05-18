package rootheart.codes.sudoku.solver;

import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;
import rootheart.codes.sudoku.game.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Solver {

    public static class NoSolutionException extends RuntimeException {
        public NoSolutionException(String message) {
            super(message);
        }
    }

    public static class MultipleSolutionsException extends RuntimeException {
        public MultipleSolutionsException(String message) {
            super(message);
        }
    }

    public static class BoardInvalidException extends RuntimeException {
    }

    public void solve(Board board) {
        while (board.hasEmptyCells()) {
            if (!isValid(board)) {
                throw new BoardInvalidException();
            }
            SolverBoard solverBoard = calculateCandidates(board);
            if (!solverBoard.hasSolution()) {
                throw new NoSolutionException("found no solution");
            }
            Board previousState = clone(board);

            Map<Cell, Integer> singleCandidates = findSingleCandidates(solverBoard);
            if (singleCandidates.isEmpty()) {
                System.out.println("XXX");
                Board boardToSetARandomNumberTo = clone(board);
                List<Board> solutions = new ArrayList<>();
                boardToSetARandomNumberTo.streamEmptyCells()
                        .findFirst()
                        .ifPresent(cell ->
                                boardToSetARandomNumberTo.getPossibleValues().forEach(numberToTry -> {
                                    cell.setNumber(numberToTry);
                                    if (isValid(boardToSetARandomNumberTo)) {
                                        if (System.currentTimeMillis() % 500 == 0) {
                                            System.out.println(previousState.getBoardString());
                                            int index = boardToSetARandomNumberTo.indexOf(cell);
                                            System.out.println(" ".repeat(Math.max(0, index)) + numberToTry);
                                        }
                                        Board boardToTryToSolve = clone(boardToSetARandomNumberTo);
                                        try {
                                            solve(boardToTryToSolve);
                                            solutions.add(boardToTryToSolve);
                                        } catch (NoSolutionException e) {
                                            // if trying this number did not end up with a solution, try the next one
                                        }
                                    }
                                }));
                if (solutions.size() == 0) {
                    throw new NoSolutionException("found no solution (2)");
                }
                if (solutions.size() > 1) {
                    throw new MultipleSolutionsException("found multiple solutions");
                }
                board.set(solutions.get(0).getBoardString());
            } else {
                Map.Entry<Cell, Integer> next = singleCandidates.entrySet().iterator().next();
                next.getKey().setNumber(next.getValue());
                System.out.println(previousState.getBoardString());
                int index = board.indexOf(next.getKey());
                System.out.println(" ".repeat(Math.max(0, index)) + next.getValue());
                if (!isValid(board)) {
                    throw new BoardInvalidException();
                }
            }
        }
    }

    private Board clone(Board board) {
        String boardString = board.getBoardString();
        return new Board(boardString);
    }

    private boolean isValid(Board board) {
        return Arrays.stream(board.getColumns()).allMatch(this::isValid)
                && Arrays.stream(board.getRows()).allMatch(this::isValid)
                && Arrays.stream(board.getBlocks()).allMatch(this::isValid);
    }

    private boolean isValid(Group group) {
        return group.streamCells()
                .filter(c -> !c.isEmpty())
                .collect(Collectors.groupingBy(Cell::getNumber, Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() == 1);
    }

    private SolverBoard calculateCandidates(Board board) {
        SolverBoard solverBoard = createSolverCells(board);
        solverBoard.eliminateCandidatesThatAreSetInBuddyCells();
        solverBoard.eliminateLockedCandidates();
//        eliminateNakedTwins(cellsCandidates);
        return solverBoard;
    }

    private Map<Cell, Integer> findSingleCandidates(SolverBoard solverBoard) {
        Map<Cell, Integer> singleCandidates = new HashMap<>();
        singleCandidates.putAll(solverBoard.findNakedSingles());
        singleCandidates.putAll(solverBoard.findHiddenSingles());
        return singleCandidates;
    }

//    private String statusString(Board board, Map<Cell, Set<Integer>> cellsCandidates) {
//        List<String> states = new ArrayList<>();
//        for (Cell cell : board.getCells()) {
//            states.add(cell.getNumber() + "(" + candidatesString(cellsCandidates.get(cell)) + ")");
//        }
//        StringBuilder sb = new StringBuilder();
//        for (int row = 0; row < board.getMaxValue(); row++) {
//            for (int column = 0; column < board.getMaxValue(); column++) {
//                sb.append(states.get(row * board.getMaxValue() + column));
//            }
//            sb.append("\n");
//        }
//        return sb.toString();
//    }
//
//    private String candidatesString(Set<Integer> candidates) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 1; i <= 9; i++) {
//            if (candidates != null && candidates.contains(i)) {
//                sb.append(i);
//            } else {
//                sb.append(" ");
//            }
//        }
//        return sb.toString();
//    }

    private SolverBoard createSolverCells(Board board) {
        return new SolverBoard(board);
    }

    private void eliminateNakedTwins(Map<Cell, Set<Integer>> cellsCandidates) {
        cellsCandidates.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 2)
                .forEach(entry -> {
                    Cell cell = entry.getKey();
                    List<Cell> otherCellsInRow = getOtherCellsInGroup(cell, cell.getRow());
                    List<Cell> otherCellsInColumn = getOtherCellsInGroup(cell, cell.getColumn());
                    List<Cell> otherCellsInBlock = getOtherCellsInGroup(cell, cell.getBlock());
                    eliminateNakedTwinsInGroup(cellsCandidates, otherCellsInRow, entry.getValue());
                    eliminateNakedTwinsInGroup(cellsCandidates, otherCellsInColumn, entry.getValue());
                    eliminateNakedTwinsInGroup(cellsCandidates, otherCellsInBlock, entry.getValue());
                });
    }

    private List<Cell> getOtherCellsInGroup(Cell cell, Group group) {
        return group
                .streamEmptyCells()
                .filter(otherCell -> cell != otherCell)
                .collect(Collectors.toList());
    }

    private void eliminateNakedTwinsInGroup(Map<Cell, Set<Integer>> cellsCandidates, List<Cell> otherCellsInGroup, Set<Integer> candidates) {
        otherCellsInGroup
                .stream()
                .filter(otherCell -> cellsCandidates.get(otherCell).equals(candidates))
                .findAny()
                .ifPresent(otherCell -> {
                    otherCellsInGroup
                            .stream()
                            .filter(x -> otherCell != x)
                            .forEach(x -> cellsCandidates.get(x).removeAll(candidates));
                });
    }

    private void forAllCellsExcept(Group group, Cell exception, Consumer<Integer> consumer) {
        group.streamCells()
                .filter(cell -> cell != exception)
                .map(Cell::getNumber)
                .forEach(consumer);
    }


}
