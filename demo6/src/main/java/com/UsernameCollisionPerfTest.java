package com;

/*
 *
 * java 8
 * Benchmark                                 (size)  Mode  Cnt    Score    Error  Units
 * UsernameCollisionPerfTest.lookupUsername       1  avgt    5    9.070 ±  0.541  ns/op
 * UsernameCollisionPerfTest.lookupUsername    1000  avgt    5   84.831 ±  0.582  ns/op
 * UsernameCollisionPerfTest.lookupUsername   10000  avgt    5  184.281 ±  4.288  ns/op
 * UsernameCollisionPerfTest.lookupUsername   25000  avgt    5  211.971 ±  3.597  ns/op
 * UsernameCollisionPerfTest.lookupUsername   50000  avgt    5  232.773 ±  1.349  ns/op
 * UsernameCollisionPerfTest.lookupUsername  100000  avgt    5  248.393 ±  1.775  ns/op
 * UsernameCollisionPerfTest.lookupUsername  200000  avgt    5  243.737 ±  3.394  ns/op
 * UsernameCollisionPerfTest.lookupUsername  300000  avgt    5  248.642 ±  7.362  ns/op
 * UsernameCollisionPerfTest.lookupUsername  499331  avgt    5  268.519 ± 15.633  ns/op
 *
 *
 * java 6
 * Benchmark                                 (size)  Mode  Cnt        Score        Error  Units
 * UsernameCollisionPerfTest.lookupUsername       1  avgt    5        8.849 ±      0.067  ns/op
 * UsernameCollisionPerfTest.lookupUsername    1000  avgt    5     3700.700 ±     86.543  ns/op
 * UsernameCollisionPerfTest.lookupUsername   10000  avgt    5    38709.093 ±    607.156  ns/op
 * UsernameCollisionPerfTest.lookupUsername   25000  avgt    5    96871.919 ±    572.208  ns/op
 * UsernameCollisionPerfTest.lookupUsername   50000  avgt    5   205613.766 ±   7159.370  ns/op
 * UsernameCollisionPerfTest.lookupUsername  100000  avgt    5   737791.697 ±  25354.688  ns/op
 * UsernameCollisionPerfTest.lookupUsername  200000  avgt    5  1741905.956 ± 160165.619  ns/op
 * UsernameCollisionPerfTest.lookupUsername  300000  avgt    5  2824051.166 ± 167913.181  ns/op
 * UsernameCollisionPerfTest.lookupUsername  499331  avgt    5  4684490.532 ± 444621.603  ns/op
 *
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
public class UsernameCollisionPerfTest {

    private Map<String, String> map;

    @Param( {"1", "1000", "10000", "25000", "50000", "100000", "200000", "300000", "499331"})
    private int size;

    @Setup
    public void setup() throws Exception {
        final String[] keys = load(size);
        map = new LinkedHashMap<String, String>(size);
        for (String key : keys) {
            map.put(key, key);
        }
    }

    static String[] load(int size) throws IOException {
        List<String> list = new ArrayList<String>(size);
        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream("username.txt"), "UTF-8"));
        try {
            for (int i = 0; i < size; i++) {
                final String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.hashCode() != "username".hashCode()) {
                    throw new RuntimeException(String.format("'%s' @ %d", line, i));
                }

                list.add(line);
            }
        } finally {
            bufferedReader.close();
        }

        return list.toArray(new String[list.size()]);
    }

    @Benchmark
    public String lookupUsername() {
        return map.get("username");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(UsernameCollisionPerfTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}