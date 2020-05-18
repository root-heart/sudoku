package rootheart.codes.sudoku.solver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rootheart.codes.sudoku.game.Board;
import rootheart.codes.sudoku.game.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class SolverBoard {

    private final Map<Cell, SolverCell> solverCellMap;
    private final List<SolverCell> singleCandidates = new ArrayList<>();

    public SolverBoard(Board board) {
        solverCellMap = board.streamCells()
                .map(cell -> new SolverCell(cell, board))
                .collect(Collectors.toMap(SolverCell::getCell, Function.identity()));
        solverCellMap.forEach((cell, solverCell) -> {
            cell.getColumn().streamCells()
                    .filter(otherCell -> cell != otherCell)
                    .map(solverCellMap::get)
                    .forEach(c -> solverCell.getOtherCellsInColumn().add(c));
            cell.getRow().streamCells()
                    .filter(otherCell -> cell != otherCell)
                    .map(solverCellMap::get)
                    .forEach(c -> solverCell.getOtherCellsInRow().add(c));
            cell.getBlock().streamCells()
                    .filter(otherCell -> cell != otherCell)
                    .map(solverCellMap::get)
                    .forEach(c -> solverCell.getOtherCellsInBlock().add(c));
        });

        eliminateCandidatesThatAreSetInBuddyCells();
        revealHiddenSingles();
        eliminateLockedCandidates();
        eliminateNakedTwins();
        findNakedSingles();
    }

    public void eliminateCandidatesThatAreSetInBuddyCells() {
        solverCellMap.values().forEach(SolverCell::eliminateCandidatesThatAreSetInBuddyCells);
    }

    public void eliminateLockedCandidates() {
        solverCellMap.values().forEach(SolverCell::eliminateLockedCandidates);
    }

    public void eliminateNakedTwins() {
        solverCellMap.values().forEach(SolverCell::eliminateNakedTwins);
    }

    public boolean hasSolution() {
        return solverCellMap.entrySet().stream()
                .noneMatch(entry -> entry.getKey().isEmpty() && entry.getValue().getCandidates().size() == 0);
    }

    public void findNakedSingles() {
        solverCellMap.values()
                .stream()
                .filter(SolverCell::hasOneCandidate)
                .forEach(singleCandidates::add);
    }

    public void revealHiddenSingles() {
        solverCellMap.values().forEach(SolverCell::revealHiddenSingle);
    }
}
