package com;

/*
 * java 6 (not a mac)
 * Benchmark                                 (size)  Mode  Cnt         Score         Error  Units
 * UsernameCollisionLoadTest.fillMap              1  avgt    5             9.962 ±           0.044  ns/op
 * UsernameCollisionLoadTest.fillMap           1000  avgt    5       1931634.601 ±      113897.195  ns/op
 * UsernameCollisionLoadTest.fillMap          10000  avgt    5     172056271.763 ±    12730995.953  ns/op
 * UsernameCollisionLoadTest.fillMap          25000  avgt    5    1124975538.440 ±     9191370.897  ns/op
 * UsernameCollisionLoadTest.fillMap          50000  avgt    5    4580893238.500 ±    49921354.673  ns/op
 * UsernameCollisionLoadTest.fillMap          75000  avgt    5   10977656808.000 ±    69502110.803  ns/op
 * UsernameCollisionLoadTest.fillMap         100000  avgt    5   22019384033.000 ±   407383199.753  ns/op
 * UsernameCollisionLoadTest.fillMap         150000  avgt    5   60411107851.200 ±  5564735513.024  ns/op
 * UsernameCollisionLoadTest.fillMap         200000  avgt    5  109694806276.400 ± 10469215538.308  ns/op
 *
 * java 7
 * Benchmark                          (size)  Mode  Cnt            Score            Error  Units
 * UsernameCollisionLoadTest.fillMap       1  avgt    5           12.557 ±          0.325  ns/op
 * UsernameCollisionLoadTest.fillMap      50  avgt    5         5588.936 ±         96.353  ns/op
 * UsernameCollisionLoadTest.fillMap     100  avgt    5        21259.873 ±        594.770  ns/op
 * UsernameCollisionLoadTest.fillMap     500  avgt    5       584719.878 ±      23920.273  ns/op
 * UsernameCollisionLoadTest.fillMap    1000  avgt    5      2361111.117 ±     103313.001  ns/op
 * UsernameCollisionLoadTest.fillMap   10000  avgt    5    194573507.692 ±    4796333.527  ns/op
 * UsernameCollisionLoadTest.fillMap   25000  avgt    5   1247632160.000 ±   29788933.078  ns/op
 * UsernameCollisionLoadTest.fillMap   50000  avgt    5   5029241000.000 ±   45118305.719  ns/op
 * UsernameCollisionLoadTest.fillMap   75000  avgt    5  12220774600.000 ± 1037376125.903  ns/op
 * UsernameCollisionLoadTest.fillMap  100000  avgt    5  22579315600.000 ±  465110007.717  ns/op
 * UsernameCollisionLoadTest.fillMap  150000  avgt    5  54292533200.000 ±  544912668.712  ns/op
 * UsernameCollisionLoadTest.fillMap  200000  avgt    5  99357551800.000 ± 2454618823.386  ns/op
 *
 * java 8
 * Benchmark                          (size)  Mode  Cnt          Score         Error  Units
 * UsernameCollisionLoadTest.fillMap       1  avgt    5         11.579 ±       0.555  ns/op
 * UsernameCollisionLoadTest.fillMap      50  avgt    5       4321.073 ±      98.726  ns/op
 * UsernameCollisionLoadTest.fillMap     100  avgt    5      10728.407 ±     193.052  ns/op
 * UsernameCollisionLoadTest.fillMap     500  avgt    5      71925.047 ±    1464.926  ns/op
 * UsernameCollisionLoadTest.fillMap    1000  avgt    5     178058.172 ±    6404.673  ns/op
 * UsernameCollisionLoadTest.fillMap   10000  avgt    5    2649472.992 ±   32398.127  ns/op
 * UsernameCollisionLoadTest.fillMap   25000  avgt    5    7861746.071 ±  252674.713  ns/op
 * UsernameCollisionLoadTest.fillMap   50000  avgt    5   18756168.751 ±  879683.760  ns/op
 * UsernameCollisionLoadTest.fillMap   75000  avgt    5   29293273.679 ±  948687.190  ns/op
 * UsernameCollisionLoadTest.fillMap  100000  avgt    5   40904931.254 ± 1682045.361  ns/op
 * UsernameCollisionLoadTest.fillMap  150000  avgt    5   70209460.365 ± 5197427.422  ns/op
 * UsernameCollisionLoadTest.fillMap  200000  avgt    5  100019022.533 ± 2835663.126  ns/op
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static com.UsernameCollisionPerfTest.loadKeysFromFile;

/**
 * @author vladimir.dolzhenko
 * @since 2017-01-07
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@State(Scope.Benchmark)
public class UsernameCollisionLoadTest {

    private String[] keys;

    @Param( {"1", "50", "100", "500", "1000", "10000", "25000", "50000", "75000", "100000", "150000", "200000"})
    private int size;
    private Map<String, String> map;

    @Setup
    public void setup() throws Exception {
        keys = loadKeysFromFile(size);
        map = new HashMap<String, String>(size);
    }

    @Benchmark
    public Map fillMap() {
        for (String key : keys) map.put(key, key);

        return map;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(UsernameCollisionLoadTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}