package rootheart.codes.sudoku


import rootheart.codes.sudoku.game.Board
import rootheart.codes.sudoku.game.Cell
import rootheart.codes.sudoku.game.Group
import spock.lang.Specification

class BoardSpec extends Specification {
//    def 'Test board of size 2'() {
//        given:
//        def board = new Board(String.format("%016d", 0))
//
//        expect:
//        board.cell(0, 0).possibleValues as List == [1, 2, 3, 4]
//
//        when:
//        board.cell(1, 1).setNumber(2)
//
//        then:
//        board.cell(0, 0).possibleValues as List == [1, 3, 4]
//    }

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
//        board.cell(3, 3).possibleValues as List == [1]
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

            assert columnCell == rowCell
            assert blockCell == columnCell
            assert blockCell == rowCell
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

        expect:
//        board.rows*.cells*.every { it.possibleValues as List == [1, 2, 3, 4, 5, 6, 7, 8, 9] }

        when:
        board.cell(1, 0).number = 1

        then:
        board.cell(1, 0).number == 1
//        [board.rows[0], board.columns[1], board.blocks[0]].every {
//            allCellsHavePossibleValues(it, [2, 3, 4, 5, 6, 7, 8, 9])
//        }

        when:
        board.cell(1, 1).number = 2

        then:
        board.cell(1, 1).number == 2
//        [0, 2].each {
//            assert board.rows[1].cells[it].possibleValues == [3, 4, 5, 6, 7, 8, 9] as Set
//        }
//        (3..8).each {
//            assert board.rows[1].cells[it].possibleValues == [1, 3, 4, 5, 6, 7, 8, 9] as Set
//        }
//        board.columns[1].cells.every { cellsPossibleValuesAre(it, [3, 4, 5, 6, 7, 8, 9]) }
//        board.blocks[0].cells.every { cellsPossibleValuesAre(it, [3, 4, 5, 6, 7, 8, 9]) }

//        when:
//        board.cell(7, 0).number = 5
//
//        then:
//        board.cell(0, 0).possibleValues as List == [3, 4, 6, 7, 8, 9]

//        when:
//        board.cell(8, 8).number = 7
//
//        then:
//        board.cell(0, 0).possibleValues as List == [3, 4, 6, 7, 8, 9]
    }

//    static allCellsHavePossibleValues(Group group, List<Integer> possibleValues) {
//        return group.cells.every { it.number != 0 || it.possibleValues == possibleValues as Set }
//    }
//
//    static cellsPossibleValuesAre(Cell cell, List<Integer> possibleValues) {
//        cell.number != 0 || cell.possibleValues == possibleValues as Set
//    }

//    def 'Test a real world example'() {
//        given:
//        def board = new Board(
//                "000000000" * 6 +
//                        "740200069" +
//                        "000000000" +
//                        "000000084")
//
//        expect:
//        board.cell(8, 6).possibleValues.empty
//    }
}
