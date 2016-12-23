package com;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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
@Threads(1)
public class AllocationPerfTest {

    @Benchmark
    public Object allocate() {
        return new Object();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AllocationPerfTest.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }
}
