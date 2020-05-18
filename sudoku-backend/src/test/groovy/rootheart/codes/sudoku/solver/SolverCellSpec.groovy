package rootheart.codes.sudoku.solver

import rootheart.codes.sudoku.game.Board
import spock.lang.Specification

class SolverCellSpec extends Specification {

    def 'Test that locked candidates are eliminated'() {
        given:
        def board = new Board("000000000" + "000789000" + "123000000" + "000000000" * 6)
        def solverBoard = new SolverBoard(board)

        when:
        solverBoard.eliminateCandidatesThatAreSetInBuddyCells()
        solverBoard.eliminateLockedCandidates()

        then:
        solverBoard.solverCellMap[board.getCell(6)].candidates == [4, 5, 6] as Set
        solverBoard.solverCellMap[board.getCell(7)].candidates == [4, 5, 6] as Set
        solverBoard.solverCellMap[board.getCell(8)].candidates == [4, 5, 6] as Set

        when:
        board = new Board("100000000" + "200000000" + "300000000" + "070000000" + "080000000" + "090000000" + "000000000" * 3)
        solverBoard = new SolverBoard(board)

        solverBoard.eliminateCandidatesThatAreSetInBuddyCells()
        solverBoard.eliminateLockedCandidates()

        then:
        solverBoard.solverCellMap[board.getCell(56)].candidates == [4, 5, 6] as Set
        solverBoard.solverCellMap[board.getCell(65)].candidates == [4, 5, 6] as Set
        solverBoard.solverCellMap[board.getCell(74)].candidates == [4, 5, 6] as Set

        when:
        board = new Board("000102000" + "000000000" + "000000300" + "000030000" + "000000000" * 5)
        solverBoard = new SolverBoard(board)

        solverBoard.eliminateCandidatesThatAreSetInBuddyCells()
        solverBoard.eliminateLockedCandidates()

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
        def board = new Board("104090068" + "956018034" + "008406951" + "510000086" + "800600012"
                + "640080097" + "781923645" + "495060823" + "060854179")
        def solverBoard = new SolverBoard(board)

        when:
        solverBoard.eliminateCandidatesThatAreSetInBuddyCells()
        solverBoard.eliminateNakedTwins()

        then:
        solverBoard.solverCellMap[board.getCell(47)].candidates == [2, 3] as Set
        solverBoard.solverCellMap[board.getCell(74)].candidates == [2, 3] as Set
        solverBoard.solverCellMap[board.getCell(38)].candidates == [7, 9] as Set
        solverBoard.solverCellMap[board.getCell(29)].candidates == [7, 9] as Set
    }
}
