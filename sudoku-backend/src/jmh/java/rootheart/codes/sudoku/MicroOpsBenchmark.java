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
import rootheart.codes.sudoku.solver.binaryoptimized.Board;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(1)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MicroOpsBenchmark {

    private Board board;

    @Setup(Level.Invocation)
    public void setup() {
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            boardString.append((int) (Math.random() * 10));
        }
        board = new Board(boardString.toString());
    }

//    @Benchmark
    public int testArrayAccess() {
        int sum = 0;
        for (int i = 0; i < 81; i++) {
            sum += board.getBinaryEncodedCandidates(i);
        }
        return sum;
    }
}
