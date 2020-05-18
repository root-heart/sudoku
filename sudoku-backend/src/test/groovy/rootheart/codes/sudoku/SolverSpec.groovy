package rootheart.codes.sudoku

import rootheart.codes.sudoku.game.Board
import rootheart.codes.sudoku.solver.EclipseCollectionsSolver
import rootheart.codes.sudoku.solver.Solver
import spock.lang.Ignore
import spock.lang.Specification

import java.util.stream.Collectors

class SolverSpec extends Specification {
    private String mediumSudoku = "975002130" +
            "000600000" +
            "030500000" +
            "000006090" +
            "009000010" +
            "000005078" +
            "740200069" +
            "000003000" +
            "020760084"
    private String extremeDifficultSudoku = "900000000" +
            "000700016" +
            "064000205" +
            "240080507" +
            "000076000" +
            "000000000" +
            "005130940" +
            "002008070" +
            "000007100"

    def 'Test the very simplest Sudoku'() {
        given:
        def board = new Board("1234" + "3412" + "2143" + "4320")

        when:
        new Solver().solve(board)

        then:
        board.toString() == "1234\n" + "3412\n" + "2143\n" + "4321\n"
    }

    def 'Test one very simple Sudoku'() {
        given:
        def board = new Board("1234" + "3412" + "2143" + "0000")

        when:
        new Solver().solve(board)

        then:
        board.toString() == "1234\n" + "3412\n" + "2143\n" + "4321\n"
    }

    def 'Test one very simple but ambiguous Sudoku'() {
        given:
        def board = new Board("1234" + "3412" + "0001" + "0000")

        when:
        new Solver().solve(board)

        then:
        thrown(Solver.MultipleSolutionsException)
    }



    def 'Test medium Sudoku'() {
        given:
        def solver = new Solver()

        when:
        def board = new Board(mediumSudoku)
        solver.solve(board)

        then:
        noExceptionThrown()
        board.toString() == "975842136\n" +
                "482631957\n" +
                "136597842\n" +
                "257186493\n" +
                "869374215\n" +
                "314925678\n" +
                "743218569\n" +
                "698453721\n" +
                "521769384\n"
    }

    def 'Test that getting cells in same block in other rows works'() {
        given:
        def solver = new Solver()
        def board = new Board("0" * 81)

        when:
        def cells = solver.getEmptyCellsInSameBlockInOtherRows(board.getCell(cellIndex))

        then:
        cells.collect(Collectors.toSet()) == otherCellsIndices.collect { board.getCell(it) } as Set

        where:
        cellIndex || otherCellsIndices
        0         || [9, 10, 11, 18, 19, 20]
        10        || [0, 1, 2, 18, 19, 20]
        20        || [0, 1, 2, 9, 10, 11]
        6         || [15, 16, 17, 24, 25, 26]
    }

    def 'Test that getting cells in same row in other blocks works'() {
        given:
        def solver = new Solver()
        def board = new Board("0" * 81)

        when:
        def cells = solver.getEmptyCellsInSameRowInOtherBlocks(board.getCell(cellIndex))

        then:
        cells.collect(Collectors.toSet()) == otherCellsIndices.collect { board.getCell(it) } as Set

        where:
        cellIndex || otherCellsIndices
        0         || [3, 4, 5, 6, 7, 8]
        10        || [12, 13, 14, 15, 16, 17]
        21        || [18, 19, 20, 24, 25, 26]
        6         || [0, 1, 2, 3, 4, 5]
    }

    def 'Test that locked candidates are eliminated'() {
        given:
        def solver = new Solver()
        def board = new Board("000000000" + "000789000" + "123000000" + "000000000" * 6)

        when:
        def candidates = solver.createCandidates(board)
        solver.eliminateCandidatesAreSetInBuddyCells(candidates)
        solver.eliminateLockedCandidates(candidates)

        then:
        candidates[board.getCell(6)] == [4, 5, 6] as Set
        candidates[board.getCell(7)] == [4, 5, 6] as Set
        candidates[board.getCell(8)] == [4, 5, 6] as Set

        when:
        board = new Board("100000000" + "200000000" + "300000000" + "070000000" + "080000000" + "090000000" + "000000000" * 3)
        candidates = solver.createCandidates(board)
        solver.eliminateCandidatesAreSetInBuddyCells(candidates)
        solver.eliminateLockedCandidates(candidates)

        then:
        candidates[board.getCell(56)] == [4, 5, 6] as Set
        candidates[board.getCell(65)] == [4, 5, 6] as Set
        candidates[board.getCell(74)] == [4, 5, 6] as Set

        when:
        board = new Board("000102000" + "000000000" + "000000300" + "000030000" + "000000000" * 5)
        candidates = solver.createCandidates(board)
        solver.eliminateCandidatesAreSetInBuddyCells(candidates)
        solver.eliminateLockedCandidates(candidates)

        then:
        candidates[board.getCell(0)].contains(3)
        candidates[board.getCell(1)].contains(3)
        candidates[board.getCell(2)].contains(3)
        !candidates[board.getCell(9)].contains(3)
        !candidates[board.getCell(10)].contains(3)
        !candidates[board.getCell(11)].contains(3)
    }

    def 'Test that naked twins are eliminated'() {
        given:
        def solver = new Solver()
        def board = new Board("100000000" + "046000000" + "057000000" + "065000000" + "074000000" + "089000000" + "098000000" + "000000000" * 2)

        when:
        def candidates = solver.createCandidates(board)
        solver.eliminateCandidatesAreSetInBuddyCells(candidates)
        solver.eliminateNakedTwins(candidates)

        then:
        candidates[board.getCell(1)] == [2, 3] as Set
        candidates[board.getCell(2)] == [2, 3] as Set
        candidates[board.getCell(3)] == [4, 5, 6, 7, 8, 9] as Set
        candidates[board.getCell(4)] == [4, 5, 6, 7, 8, 9] as Set
        candidates[board.getCell(5)] == [4, 5, 6, 7, 8, 9] as Set
        candidates[board.getCell(6)] == [4, 5, 6, 7, 8, 9] as Set
        candidates[board.getCell(7)] == [4, 5, 6, 7, 8, 9] as Set
        candidates[board.getCell(8)] == [4, 5, 6, 7, 8, 9] as Set

    }

    def 'Test hard Sudoku'() {
        given:
        def solver = new Solver()

        when:
        def board = new Board(
                "001000000" +
                        "003602008" +
                        "000087400" +
                        "240700080" +
                        "609408000" +
                        "030091000" +
                        "000000000" +
                        "000000602" +
                        "090050730")
        solver.solve(board)

        then:
        noExceptionThrown()
        println board
    }

    def 'Test extreme difficult Sudoku'() {
        given:
        def board = new Board("002400000" +
                "000000000" +
                "900000056" +
                "000300000" +
                "000056000" +
                "009000870" +
                "500000000" +
                "000200100" +
                "300009200")

        when:
        new Solver().solve(board)

        then:
        noExceptionThrown()
        println board
    }

    def 'Test another extreme difficult Sudoku'() {

        given:
        def board = new Board(extremeDifficultSudoku)

        when:
        new Solver().solve(board)

        then:
        noExceptionThrown()
        println board
    }

    def 'Test performance with extremely difficult Sudoku'() {
        given:
        def jdkSolver = new Solver()
        def eclipseCollectionsSolver = new EclipseCollectionsSolver()

        def warmUpCount = 1_000_000
        def benchmarkCount = 1_000_000

        when:
        def board = new Board(extremeDifficultSudoku)
        warmUpCount.times { jdkSolver.solve(board) }
        warmUpCount.times { eclipseCollectionsSolver.solve(board) }

        and:
        5.times {
            long s = System.nanoTime()
            benchmarkCount.times { jdkSolver.solve(board) }
            long e = System.nanoTime()

            long s2 = System.nanoTime()
            benchmarkCount.times { eclipseCollectionsSolver.solve(board) }
            long e2 = System.nanoTime()

            println "default JDK implementation ${(e - s) / 1000} microseconds"
            println "eclipse collections implementation ${(e2 - s2) / 1000} microseconds"
        }

        then:
        true
    }

    def 'Recreate failing test'() {
        given:
        def board = new Board(
                "001305900" +
                "973602508" +
                "000987400" +

                "245700189" +
                "619428300" +
                "030591200" +

                "000000800" +
                "000000602" +
                "090050730")

        when:
        new Solver().solve(board)

        then:
        thrown Solver.NoSolutionException
    }
}
