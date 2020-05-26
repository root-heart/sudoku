package rootheart.codes.sudoku;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import rootheart.codes.sudoku.solver.binaryoptimized.JavaScriptTranslatedSolver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(1)
@Measurement(iterations = 3)
@Warmup(iterations = 3)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Solve17CluesBenchmark {

    private JavaScriptTranslatedSolver solver;
    private List<String> puzzles;

    @Setup(Level.Invocation)
    public void setup() throws IOException, URISyntaxException {
        solver = new JavaScriptTranslatedSolver();
        URL url = getClass().getResource("/all_17_clue_sudokus.txt");
        puzzles = Files.readAllLines(Path.of(url.toURI()));
        puzzles.removeIf(s -> s.length() != 81);
    }

    @Benchmark
    public void solveBenchmark() {
        for (String puzzle : puzzles) {
            solver.solve(puzzle);
        }
    }
}
