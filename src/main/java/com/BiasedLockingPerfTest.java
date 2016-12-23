package com;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * <code>
 * -XX:BiasedLockingStartupDelay=0 -XX:+UseBiasedLocking
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * BiasedLockingPerfTest.increment          avgt    5   5.799 ± 0.148  ns/op
 * BiasedLockingPerfTest.incrementWithSysIdHashCode  avgt    5  25.279 ± 1.054  ns/op
 * <p>
 * -XX:BiasedLockingStartupDelay=0 -XX:-UseBiasedLocking
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * BiasedLockingPerfTest.increment          avgt    5  24.994 ± 0.895  ns/op
 * BiasedLockingPerfTest.incrementWithSysIdHashCode  avgt    5  25.121 ± 0.976  ns/op
 * </code>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@State(Scope.Benchmark)
public class BiasedLockingPerfTest {

    public static class Counter {
        private long count = 0l;

        public synchronized long incAndGet() {
            return ++count;
        }
    }

    Counter counter, counterWithSysIdHashCode;

    @Setup
    public void setup(Blackhole bh) {
        counter = new Counter();
        counterWithSysIdHashCode = new Counter();
        bh.consume(System.identityHashCode(counterWithSysIdHashCode));
    }

    @Benchmark
    public long increment() {
        return counter.incAndGet();
    }

    @Benchmark
    public long incrementWithSysIdHashCode() {
        return counterWithSysIdHashCode.incAndGet();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BiasedLockingPerfTest.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }
}