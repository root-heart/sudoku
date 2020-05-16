package rootheart.codes.sudoku

import rootheart.codes.sudoku.game.Board
import rootheart.codes.sudoku.solver.EclipseCollectionsSolver
import rootheart.codes.sudoku.solver.Solver
import spock.lang.Ignore
import spock.lang.Specification

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

    @Ignore("The solver is not able to solve this yet")
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
    }

    def 'Test performance with medium Sudoku'() {
        given:
        def jdkSolver = new Solver()
        def eclipseCollectionsSolver = new EclipseCollectionsSolver()

        def warmUpCount = 1_000_000
        def benchmarkCount = 1_000_000

        when:
        def board = new Board(mediumSudoku)
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
}
