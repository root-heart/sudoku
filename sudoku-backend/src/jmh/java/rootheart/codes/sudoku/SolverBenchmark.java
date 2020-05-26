package rootheart.codes.sudoku;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.RunnerException;
import rootheart.codes.sudoku.solver.binaryoptimized.JavaScriptTranslatedSolver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(1)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SolverBenchmark {
    private final JavaScriptTranslatedSolver s = new JavaScriptTranslatedSolver();
    private final String mediumSudoku = "975002130" +
            "000600000" +
            "030500000" +
            "000006090" +
            "009000010" +
            "000005078" +
            "740200069" +
            "000003000" +
            "020760084";

    private final String hardSudokuSolvableByLogic = "002400000" +
            "000000000" +
            "900000056" +
            "000300000" +
            "000056000" +
            "009000870" +
            "500000000" +
            "000200100" +
            "300009200";

    private final String extremeDifficultSudokuCurrentlyOnlySolvableByBruteForce = "900000000" +
            "000700016" +
            "064000205" +
            "240080507" +
            "000076000" +
            "000000000" +
            "005130940" +
            "002008070" +
            "000007100";

    @Benchmark
    public String solveMediumSudoku() {
        return s.solve(mediumSudoku);
    }

    @Benchmark
    public String solveHardSudoku() {
        return s.solve(hardSudokuSolvableByLogic);
    }

    @Benchmark
    public String solveVeryHardSudoku() {
        return s.solve(extremeDifficultSudokuCurrentlyOnlySolvableByBruteForce);
    }
}