package rootheart.codes.sudoku.solver

import rootheart.codes.sudoku.game.Board
import spock.lang.Specification

class SolverPerformanceTest extends Specification {
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

    def 'Test performance with extremely difficult Sudoku'() {
        given:
        def jdkSolver = new Solver()
        def sudokuToSolve = mediumSudoku

        def warmUpCount = 1_00
        def benchmarkCount = 1_00

        when:
        5.times {
            warmUpCount.times {
                def board = new Board(sudokuToSolve)
                jdkSolver.solve(board)
            }

            long s = System.nanoTime()
            benchmarkCount.times {
                def board = new Board(sudokuToSolve)
                jdkSolver.solve(board)
            }
            long e = System.nanoTime()

            println "solver took ${(e - s) / 1000} microseconds for $benchmarkCount iterations"
        }

        then:
        true
    }
}
