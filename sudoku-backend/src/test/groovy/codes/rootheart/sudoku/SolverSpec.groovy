package codes.rootheart.sudoku

import codes.rootheart.sudoku.game.Board
import codes.rootheart.sudoku.solver.Solver
import spock.lang.Specification

class SolverSpec extends Specification {
    def 'Test'() {
        given:
        def solver = new Solver()

        when:
        def board = new Board(
                "975002130" +
                        "000600000" +
                        "030500000" +
                        "000006090" +
                        "009000010" +
                        "000005078" +
                        "740200069" +
                        "000003000" +
                        "020760084")
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
}
