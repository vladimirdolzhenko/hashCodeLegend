package com;

/*
 * java 6
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
 * Benchmark                                 (size)  Mode  Cnt         Score         Error  Units
 * UsernameCollisionLoadTest.fillMap              1  avgt    5             9.471 ±         0.135  ns/op
 * UsernameCollisionLoadTest.fillMap           1000  avgt    5       2049179.356 ±     28326.870  ns/op
 * UsernameCollisionLoadTest.fillMap          10000  avgt    5     194263597.062 ±   5029755.968  ns/op
 * UsernameCollisionLoadTest.fillMap          25000  avgt    5    1223330855.920 ±  20596750.532  ns/op
 * UsernameCollisionLoadTest.fillMap          50000  avgt    5    4738248388.800 ± 234238554.562  ns/op
 * UsernameCollisionLoadTest.fillMap          75000  avgt    5   13446065672.200 ±  58996456.901  ns/op
 * UsernameCollisionLoadTest.fillMap         100000  avgt    5   24496043250.200 ± 929146451.362  ns/op
 * UsernameCollisionLoadTest.fillMap         150000  avgt    5   61568261391.400 ± 322953060.361  ns/op
 * UsernameCollisionLoadTest.fillMap         200000  avgt    5  103814620471.000 ± 536345021.313  ns/op
 *
 * java 8
 * Benchmark                                 (size)  Mode  Cnt         Score         Error  Units
 * UsernameCollisionLoadTest.fillMap              1  avgt    5        11.440 ±       0.176  ns/op
 * UsernameCollisionLoadTest.fillMap           1000  avgt    5    163476.496 ±    2920.057  ns/op
 * UsernameCollisionLoadTest.fillMap          10000  avgt    5   2339982.587 ±  102194.633  ns/op
 * UsernameCollisionLoadTest.fillMap          25000  avgt    5   7775987.507 ±  691068.451  ns/op
 * UsernameCollisionLoadTest.fillMap          50000  avgt    5  16999481.954 ±  552311.650  ns/op
 * UsernameCollisionLoadTest.fillMap          75000  avgt    5  25960890.208 ± 1125405.867  ns/op
 * UsernameCollisionLoadTest.fillMap         100000  avgt    5  37741043.032 ± 1582403.620  ns/op
 * UsernameCollisionLoadTest.fillMap         150000  avgt    5  62764186.005 ± 1379224.960  ns/op
 * UsernameCollisionLoadTest.fillMap         200000  avgt    5  84648199.214 ± 2708582.451  ns/op
 */

import java.util.HashMap;
import java.util.List;
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

import static com.UsernameCollisionPerfTest.load;

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

    @Param( {"1", "1000", "10000", "25000", "50000", "75000", "100000", "150000", "200000"})
    private int size;
    private Map<String, String> map;

    @Setup
    public void setup() throws Exception {
        keys = load(size);
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