package rootheart.codes.sudoku.solver

import rootheart.codes.sudoku.game.Board
import spock.lang.Specification

class SolverBoardSpec extends Specification {
    def 'Test that getting cells in same block in other rows works for cell index #cellIndex'() {
        given:
        def board = new Board("0" * 81)
        def solverBoard = new SolverBoard(board)

        when:
        def cells = solverBoard.solverCellMap.get(board.getCell(cellIndex)).getEmptyCellsInSameBlockInOtherRows().cells

        then:
        cells.collect { it.cell } as Set == otherCellsIndices.collect { board.getCell(it) } as Set

        where:
        cellIndex || otherCellsIndices
        0         || [9, 10, 11, 18, 19, 20]
        10        || [0, 1, 2, 18, 19, 20]
        20        || [0, 1, 2, 9, 10, 11]
        6         || [15, 16, 17, 24, 25, 26]
    }

    def 'Test that getting cells in same row in other blocks works for cell index #cellIndex'() {
        given:
        def board = new Board("0" * 81)
        def solverBoard = new SolverBoard(board)

        when:
        def cells = solverBoard.solverCellMap.get(board.getCell(cellIndex)).getEmptyCellsInSameRowInOtherBlocks().cells

        then:
        cells.collect { it.cell } as Set == otherCellsIndices.collect { board.getCell(it) } as Set

        where:
        cellIndex || otherCellsIndices
        0         || [3, 4, 5, 6, 7, 8]
        10        || [12, 13, 14, 15, 16, 17]
        21        || [18, 19, 20, 24, 25, 26]
        6         || [0, 1, 2, 3, 4, 5]
    }

}
