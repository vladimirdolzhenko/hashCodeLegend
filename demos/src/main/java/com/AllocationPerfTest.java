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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * -XX:+UseTLAB
 * Benchmark                    Mode  Cnt  Score   Error  Units
 * AllocationPerfTest.allocate  avgt    5  6.361 ± 0.058  ns/op
 * <p>
 * -XX:-UseTLAB
 * Benchmark                    Mode  Cnt  Score   Error  Units
 * AllocationPerfTest.allocate  avgt    5  306.099 ± 17.275  ns/op
 *
 * @author vladimir.dolzhenko
 * @since 2016-11-30
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(4)
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
