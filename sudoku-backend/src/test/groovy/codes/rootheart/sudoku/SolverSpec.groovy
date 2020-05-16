package codes.rootheart.sudoku

import codes.rootheart.sudoku.game.Board
import codes.rootheart.sudoku.solver.Solver
import spock.lang.Specification

class SolverSpec extends Specification {
    def 'Test'() {
        given:
        def solver = new Solver()

        when:
        solver.solve(new Board(
                "036000507" +
                        "000007080" +
                        "201000069" +
                        "000000000" +
                        "042695000" +
                        "000080902" +
                        "000039040" +
                        "054870031" +
                        "060012800"))

        then:
        noExceptionThrown()

        when:
        solver.solve(new Board(
                "975002130" +
                        "000600000" +
                        "030500000" +
                        "000006090" +
                        "009000010" +
                        "000005078" +
                        "740200069" +
                        "000003000" +
                        "020760084"))


        then:
        noExceptionThrown()
    }
}
