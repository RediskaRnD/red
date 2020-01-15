package benchmark;

import com.jusski.Vector;
import com.jusski.optimize.array.TieredVectorArray;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import rediska.TieredVector;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 2)
public class BenchmarkLoop {
    private TieredVector vec;
    private com.jusski.TieredVector vec2;
    private TieredVectorArray vec3;
    static int[] random;

    int expectedSize;
    int blockSize;
    int halfSize = expectedSize / 2;
    Random rand;
    int addIndex;
    int removeIndex;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(BenchmarkLoop.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        expectedSize = 200_000;
        blockSize = 512;
        random = new int[expectedSize];
        vec = new TieredVector(expectedSize, blockSize);
        vec2 = new com.jusski.TieredVector(expectedSize, blockSize);
        vec3 = new TieredVectorArray(expectedSize, blockSize);

        for (int i = 0; i < expectedSize; i++) {
            vec.add(i);
            vec2.add(i);
            vec3.add(i);
        }
        rand = new Random(123);
        doTearDown();
    }

    @Benchmark
    public void test() {
        vec.add(addIndex, 33);

    }

    @Benchmark
    public void test2() {
        vec3.add(addIndex, 33);
//        vec2.remove(removeIndex);
    }
//
//    @Benchmark
//    public void test3() {
//        vec3.add(addIndex, 33);
//        vec3.remove(removeIndex);
//    }

    @TearDown(Level.Invocation)
    public void doTearDown() {
        vec3.remove(removeIndex);
        addIndex = rand.nextInt(vec3.size());
        removeIndex = rand.nextInt(vec3.size() + 1);
    }
}