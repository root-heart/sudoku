package rootheart.codes.sudoku.solver

import rootheart.codes.sudoku.game.Board
import rootheart.codes.sudoku.solver.binaryoptimized.JavaScriptTranslatedSolver
import spock.lang.Specification

class SolverSpec extends Specification {
    private String mediumSudoku =   "975002130" +
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
        def solution = new Solver().solve(board)

        then:
        solution.solvedBoard.toString() == "1234\n" + "3412\n" + "2143\n" + "4321\n"
    }

    def 'Test one very simple Sudoku'() {
        given:
        def board = new Board("1234" + "3412" + "2143" + "0000")

        when:
        def solution = new Solver().solve(board)

        then:
        solution.solvedBoard.toString() == "1234\n" + "3412\n" + "2143\n" + "4321\n"
    }

    def 'Test one very simple but ambiguous Sudoku'() {
        given:
        def board = new Board("1234" + "3412" + "0001" + "0000")

        when:
        def solution = new Solver().solve(board)

        then:
        solution == BoardSolution.MULTIPLE
    }


    def 'Test medium Sudoku'() {
        given:
        def solver = new Solver()

        when:
        def board = new Board(mediumSudoku)
        def solution = solver.solve(board)

        then:
        noExceptionThrown()
        solution.solvedBoard.toString() == "975842136\n" +
                "482631957\n" +
                "136597842\n" +
                "257186493\n" +
                "869374215\n" +
                "314925678\n" +
                "743218569\n" +
                "698453721\n" +
                "521769384\n"
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
        def solution = solver.solve(board)

        then:
        solution.solution == BoardSolution.Solution.ONE
        println solution.solvedBoard
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
        def solution = new Solver().solve(board)

        then:
        solution.solvedBoard.boardString == "762495318" +
                "185673942" +
                "934812756" +
                "241387569" +
                "873956421" +
                "659124873" +
                "528731694" +
                "497268135" +
                "316549287"

        println board
    }

    def 'Test another extreme difficult Sudoku'() {
        given:
        def board = new Board(extremeDifficultSudoku)

        when:
        def solution = new Solver().solve(board)

        then:
        solution.solution == BoardSolution.Solution.ONE
        println solution.solvedBoard
    }


    def 'Test that a board without a solution is not solved by the solver'() {
        given:
        def board = new Board("001305900" +
                "973602508" +
                "000987400" +

                "245700189" +
                "619428300" +
                "030591200" +

                "000000800" +
                "000000602" +
                "090050730")

        when:
        def solution = new Solver().solve(board)

        then:
        solution == BoardSolution.NONE
    }

    def 'Test that trying to solve an empty board results the appropriate return type'() {
        given:
        def board = new Board("0" * 81)

        when:
        def solution = new Solver().solve(board)

        then:
        solution == BoardSolution.MULTIPLE
    }

    def 'test binary optimized solver'() {
        given:
        def s = new JavaScriptTranslatedSolver()

        when:
        def solution = s.solve(mediumSudoku)

        Arrays.stream(rootheart.codes.sudoku.solver.binaryoptimized.Board.class.getDeclaredFields())
                .filter(f -> f.getName().startsWith("count_"))
                .forEach(f -> {
                    try {
                        f.setAccessible(true);
                        System.out.println(f.getName() + " -> " + f.get(null));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        then:
        solution == "975842136" +
                "482631957" +
                "136597842" +
                "257186493" +
                "869374215" +
                "314925678" +
                "743218569" +
                "698453721" +
                "521769384"
    }
}
