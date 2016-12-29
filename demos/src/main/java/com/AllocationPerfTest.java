package com;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * -XX:+UseTLAB
 * Benchmark                    Mode  Cnt  Score   Error  Units
 * AllocationPerfTest.allocate  avgt    5  3.211 ± 0.045  ns/op
 * <p>
 * -XX:-UseTLAB
 * Benchmark                    Mode  Cnt  Score   Error  Units
 * AllocationPerfTest.allocate  avgt    5  9.766 ± 0.160  ns/op
 *
 * @author vladimir.dolzhenko
 * @since 2016-11-30
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(10)
public class AllocationPerfTest {

    @Benchmark
    public Object allocate() {
        return new Object();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AllocationPerfTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
