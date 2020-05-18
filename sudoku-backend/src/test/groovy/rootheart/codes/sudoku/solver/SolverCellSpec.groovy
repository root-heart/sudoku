package rootheart.codes.sudoku.solver


import org.eclipse.collections.impl.factory.primitive.IntSets
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet
import rootheart.codes.sudoku.game.Board
import spock.lang.Specification

class SolverCellSpec extends Specification {

    def 'Test that locked candidates are eliminated'() {
        given:
        def board = new Board("000000000" + "000789000" + "123000000" + "000000000" * 6)
        def solverBoard = new SolverBoard(board)

        when:
        eliminateCandidatesThatAreSetInBuddyCells(solverBoard);
        eliminateLockedCandidates(solverBoard)

        then:
        solverBoard.solverCellMap[board.getCell(6)].candidates == [4, 5, 6] as IntHashSet
        solverBoard.solverCellMap[board.getCell(7)].candidates == [4, 5, 6] as IntHashSet
        solverBoard.solverCellMap[board.getCell(8)].candidates == [4, 5, 6] as IntHashSet

        when:
        board = new Board("100000000" + "200000000" + "300000000" + "070000000" + "080000000" + "090000000" + "000000000" * 3)
        solverBoard = new SolverBoard(board)

        eliminateCandidatesThatAreSetInBuddyCells(solverBoard);
        eliminateLockedCandidates(solverBoard)

        then:
        solverBoard.solverCellMap[board.getCell(56)].candidates == [4, 5, 6] as IntHashSet
        solverBoard.solverCellMap[board.getCell(65)].candidates == [4, 5, 6] as IntHashSet
        solverBoard.solverCellMap[board.getCell(74)].candidates == [4, 5, 6] as IntHashSet

        when:
        board = new Board("000102000" + "000000000" + "000000300" + "000030000" + "000000000" * 5)
        solverBoard = new SolverBoard(board)

        eliminateCandidatesThatAreSetInBuddyCells(solverBoard);
        eliminateLockedCandidates(solverBoard)

        then:
        solverBoard.solverCellMap[board.getCell(0)].candidates.contains(3)
        solverBoard.solverCellMap[board.getCell(1)].candidates.contains(3)
        solverBoard.solverCellMap[board.getCell(2)].candidates.contains(3)
        !solverBoard.solverCellMap[board.getCell(9)].candidates.contains(3)
        !solverBoard.solverCellMap[board.getCell(10)].candidates.contains(3)
        !solverBoard.solverCellMap[board.getCell(11)].candidates.contains(3)
    }

    def 'Test that naked twins are eliminated'() {
        given:
        def board = new Board("000789456" + "000000000" + "000000000" + "410000000" + "500000000" + "600000000" + "001000000" + "000000000" * 2)
        def solverBoard = new SolverBoard(board)

        when:
        solverBoard.solverCellMap.values().forEach(SolverCell::eliminateCandidatesThatAreSetInBuddyCells);
        solverBoard.solverCellMap[board.getCell(0)].eliminateNakedTwins()
        solverBoard.solverCellMap[board.getCell(1)].eliminateNakedTwins()
        solverBoard.solverCellMap[board.getCell(2)].eliminateNakedTwins()

        then:
        solverBoard.solverCellMap[board.getCell(0)].candidates == IntSets.mutable.of(1)
        solverBoard.solverCellMap[board.getCell(1)].candidates == IntSets.mutable.of(2, 3)
        solverBoard.solverCellMap[board.getCell(2)].candidates == IntSets.mutable.of(2, 3)
    }

    private static eliminateCandidatesThatAreSetInBuddyCells(SolverBoard solverBoard) {
        solverBoard.solverCellMap.values().forEach(SolverCell::eliminateCandidatesThatAreSetInBuddyCells)
    }


    private static eliminateLockedCandidates(SolverBoard solverBoard) {
        solverBoard.solverCellMap.values().forEach(SolverCell::eliminateLockedCandidates);
    }

    private static eliminateNakedTwins(SolverBoard solverBoard) {
        solverBoard.solverCellMap.values().forEach(SolverCell::eliminateNakedTwins);
    }
}
