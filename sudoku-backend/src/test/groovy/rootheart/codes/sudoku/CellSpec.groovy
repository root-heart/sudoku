package rootheart.codes.sudoku

import rootheart.codes.sudoku.game.Board
import spock.lang.Specification
import spock.lang.Unroll

class CellSpec extends Specification {
    @Unroll
    def 'Test that getting cells in same block in other rows works for cell #cellColumnIndex #cellRowIndex'() {
        given:
        def board = new Board("0" * 81)

        when:
        def cells = board.cell(cellColumnIndex, cellRowIndex).cellsInSameBlockInOtherRows.cells

        then:
        cells.size() == 6
        def otherCells = otherColumnIndexes.collect { column ->
            otherRowIndexes.collect { row ->
                board.cell(column, row)
            }
        }
        otherCells.flatten() as Set == cells as Set

        where:
        cellColumnIndex | cellRowIndex || otherColumnIndexes | otherRowIndexes
        1               | 1            || (0..2)             | [0, 2]
        1               | 6            || (0..2)             | [7, 8]
        1               | 7            || (0..2)             | [6, 8]
        5               | 5            || (3..5)             | [3, 4]
        5               | 0            || (3..5)             | [1, 2]
        7               | 8            || (6..8)             | [6, 7]
    }

    @Unroll
    def 'Test that getting cells in same block in other column works for cell #cellColumnIndex #cellRowIndex'() {
        given:
        def board = new Board("0" * 81)

        when:
        def cells = board.cell(cellColumnIndex, cellRowIndex).cellsInSameBlockInOtherColumns.cells

        then:
        cells.size() == 6
        def otherCells = otherColumnIndexes.collect { column ->
            otherRowIndexes.collect { row ->
                board.cell(column, row)
            }
        }
        otherCells.flatten() as Set == cells as Set

        where:
        cellColumnIndex | cellRowIndex || otherColumnIndexes | otherRowIndexes
        1               | 1            || [0, 2]             | (0..2)
        1               | 6            || [0, 2]             | (6..8)
        1               | 7            || [0, 2]             | (6..8)
        5               | 5            || [3, 4]             | (3..5)
        5               | 0            || [3, 4]             | (0..2)
        7               | 8            || [6, 8]             | (6..8)
    }

    @Unroll
    def 'Test that getting cells in same row in other blocks works for cell #cellColumnIndex #cellRowIndex'() {
        given:
        def board = new Board("0" * 81)

        when:
        def cells = board.cell(cellColumnIndex, cellRowIndex).cellsInSameRowInOtherBlocks.cells

        then:
        cells.size() == 6
        def otherCells = otherColumnIndexes.collect { column ->
            board.cell(column, cellRowIndex)
        }
        otherCells.flatten() as Set == cells as Set


        where:
        cellColumnIndex | cellRowIndex || otherColumnIndexes
        1               | 1            || (3..8)
        1               | 6            || (3..8)
        1               | 7            || (3..8)
        5               | 5            || (0..2) + (6..8)
        5               | 0            || (0..2) + (6..8)
        7               | 8            || (0..5)
    }

    @Unroll
    def 'Test that getting cells in same column in other blocks works for cell #cellColumnIndex #cellRowIndex'() {
        given:
        def board = new Board("0" * 81)

        when:
        def cells = board.cell(cellColumnIndex, cellRowIndex).cellsInSameColumnInOtherBlocks.cells

        then:
        cells.size() == 6
        def otherCells = otherRowIndexes.collect { row ->
            board.cell(cellColumnIndex, row)
        }
        otherCells.flatten() as Set == cells as Set


        where:
        cellColumnIndex | cellRowIndex || otherRowIndexes
        1               | 1            || (3..8)
        1               | 6            || (0..5)
        1               | 7            || (0..5)
        5               | 5            || (0..2) + (6..8)
        5               | 0            || (3..8)
        7               | 8            || (0..5)
    }
}
