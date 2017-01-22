package com;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * <code>
 * -XX:BiasedLockingStartupDelay=0 -XX:+UseBiasedLocking
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * StringBufferPerfTest.join1          avgt    5   22.162 ± 13.870  ns/op
 * StringBufferPerfTest.join2  avgt    5  56.597 ±  8.332  ns/op
 * <p>
 * -XX:BiasedLockingStartupDelay=0 -XX:-UseBiasedLocking
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * StringBufferPerfTest.join1          avgt    5  57.584 ± 8.817  ns/op
 * StringBufferPerfTest.join2  avgt    5  57.528 ± 8.925  ns/op
 * </code>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 1, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(value = 1, jvmArgsAppend = {"-XX:BiasedLockingStartupDelay=0"})
@State(Scope.Benchmark)
public class StringBufferPerfTest {

    StringBuffer buffer1, buffer2;

    @Setup
    public void setup(Blackhole bh) {
        buffer1 = new StringBuffer();
        buffer2 = new StringBuffer();
        bh.consume(System.identityHashCode(buffer2));
    }

    @Benchmark
    public String join1() {
        buffer1.setLength(0);
        return buffer1.append('a').toString();
    }

    @Benchmark
    public String join2() {
        buffer2.setLength(0);
        return buffer2.append('a').toString();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringBufferPerfTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}