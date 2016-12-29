package com;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author vladimir.dolzhenko
 * @since 2017-01-15
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 4, time = 2500, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@State(Scope.Benchmark)
public class MultPerfTest {

    @Param(value = {"1", "10", "100", "1000"})
    int limit;

    @Benchmark
    public void mult31(Blackhole blackhole) {
        for(int i = 0; i < limit; i++)
            blackhole.consume(i * 31);
    }

    @Benchmark
    public void shift4(Blackhole blackhole) {
        for(int i = 0; i < limit; i++)
            blackhole.consume(i << 4);
    }

    @Benchmark
    public void shift4AndSub(Blackhole blackhole) {
        for(int i = 0; i < limit; i++)
            blackhole.consume((i << 4) - i);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MultPerfTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
