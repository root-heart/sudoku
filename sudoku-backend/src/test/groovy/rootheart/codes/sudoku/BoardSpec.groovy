package rootheart.codes.sudoku

import rootheart.codes.sudoku.game.Board
import rootheart.codes.sudoku.solver.NumberSet
import spock.lang.Specification

class BoardSpec extends Specification {
    def 'Test almost complete board of size 2'() {
        given:
        def board = new Board("1234" + "3412" + "2143" + "4320")

        expect:
        board.cell(0, 0).number == 1
        board.cell(1, 0).number == 2
        board.cell(2, 0).number == 3
        board.cell(3, 0).number == 4
        board.cell(0, 1).number == 3
        board.cell(1, 1).number == 4
        board.cell(2, 1).number == 1
        board.cell(3, 1).number == 2
        board.cell(0, 2).number == 2
        board.cell(1, 2).number == 1
        board.cell(2, 2).number == 4
        board.cell(3, 2).number == 3
        board.cell(0, 3).number == 4
        board.cell(1, 3).number == 3
        board.cell(2, 3).number == 2
        board.cell(3, 3).number == 0
    }

    def 'Test cell creation'() {
        given:
        def board = new Board(String.format("%081d", 0))

        expect:
        for (int i = 0; i < 81; i++) {
            def columnIndex = i % 9
            def rowIndex = (int) (i / 9)
            def blockIndex = blockIndex(columnIndex, rowIndex)
            def blockCellIndex = blockCellIndex(columnIndex, rowIndex)

            println "col:row = $columnIndex:$rowIndex => block:index = $blockIndex:$blockCellIndex"

            def columnCell = board.getColumn(columnIndex).getCell(rowIndex)
            def rowCell = board.getRow(rowIndex).getCell(columnIndex)
            def blockCell = board.getBlock(blockIndex).getCell(blockCellIndex)

            assert columnCell.is(rowCell)
            assert blockCell.is(columnCell)
            assert blockCell.is(rowCell)
        }

        board.rows[0].getCell(0) == board.columns[0].getCell(0)
    }

    int blockIndex(int column, int row) {
        if (column in [0, 1, 2]) {
            if (row in [0, 1, 2]) return 0
            if (row in [3, 4, 5]) return 3
            if (row in [6, 7, 8]) return 6
        }
        if (column in [3, 4, 5]) {
            if (row in [0, 1, 2]) return 1
            if (row in [3, 4, 5]) return 4
            if (row in [6, 7, 8]) return 7
        }
        if (column in [6, 7, 8]) {
            if (row in [0, 1, 2]) return 2
            if (row in [3, 4, 5]) return 5
            if (row in [6, 7, 8]) return 8
        }
        return -1
    }

    int blockCellIndex(int column, int row) {
        if (column in [0, 3, 6]) {
            if (row in [0, 3, 6]) return 0
            if (row in [1, 4, 7]) return 3
            if (row in [2, 5, 8]) return 6
        } else if (column in [1, 4, 7]) {
            if (row in [0, 3, 6]) return 1
            if (row in [1, 4, 7]) return 4
            if (row in [2, 5, 8]) return 7
        } else if (column in [2, 5, 8]) {
            if (row in [0, 3, 6]) return 2
            if (row in [1, 4, 7]) return 5
            if (row in [2, 5, 8]) return 8
        }
        return -1
    }

    def 'Test board of size 3'() {
        given:
        def board = new Board(String.format("%081d", 0))

        when:
        board.cell(1, 0).number = 1

        then:
        board.cell(1, 0).number == 1

        when:
        board.cell(1, 1).number = 2

        then:
        board.cell(1, 1).number == 2
    }


    def 'Test that candidates are eliminated correctly'() {
        given:
        def board = new Board("000000000" + "000789000" + "123000000" + "000000000" * 6)

        when:
        board.eliminateImpossibleCandidates()

        then:
        board.cell(3, 0).candidates.containsAll(4, 5, 6)
        board.cell(4, 0).candidates.containsAll(4, 5, 6)
        board.cell(5, 0).candidates.containsAll(4, 5, 6)

//        when:
//        board = new Board("100000000" + "200000000" + "300000000" + "070000000" + "080000000" + "090000000" + "000000000" * 3)
//        solverBoard = new SolverBoard(board)
//
//        eliminateCandidatesThatAreSetInBuddyCells(solverBoard);
//        eliminateLockedCandidates(solverBoard)
//
//        then:
//        solverBoard.solverCellMap[board.getCell(56)].candidates == [4, 5, 6] as IntHashSet
//        solverBoard.solverCellMap[board.getCell(65)].candidates == [4, 5, 6] as IntHashSet
//        solverBoard.solverCellMap[board.getCell(74)].candidates == [4, 5, 6] as IntHashSet
//
//        when:
//        board = new Board("000102000" + "000000000" + "000000300" + "000030000" + "000000000" * 5)
//        solverBoard = new SolverBoard(board)
//
//        eliminateCandidatesThatAreSetInBuddyCells(solverBoard);
//        eliminateLockedCandidates(solverBoard)
//
//        then:
//        solverBoard.solverCellMap[board.getCell(0)].candidates.contains(3)
//        solverBoard.solverCellMap[board.getCell(1)].candidates.contains(3)
//        solverBoard.solverCellMap[board.getCell(2)].candidates.contains(3)
//        !solverBoard.solverCellMap[board.getCell(9)].candidates.contains(3)
//        !solverBoard.solverCellMap[board.getCell(10)].candidates.contains(3)
//        !solverBoard.solverCellMap[board.getCell(11)].candidates.contains(3)
    }

    def 'Test that naked twins are eliminated'() {
        given:
        def board = new Board("000789456" + "000000000" + "000000000" + "410000000" + "500000000" + "600000000" + "001000000" + "000000000" * 2)

        when:
        board.eliminateImpossibleCandidates()

        then:
        board.cell(0, 0).candidates == new NumberSet(1)
        board.cell(1, 0).candidates == new NumberSet(2, 3)
        board.cell(2, 0).candidates == new NumberSet(2, 3)
    }
}
