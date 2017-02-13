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
 * Benchmark                                  Mode  Cnt   Score   Error  Units
 * StringBufferPerfTest.buffer                avgt    5   8.884 ± 1.585  ns/op
 * StringBufferPerfTest.bufferWithIdHashCode  avgt    5  24.509 ± 0.549  ns/op
 * <p>
 * -XX:BiasedLockingStartupDelay=0 -XX:-UseBiasedLocking
 * Benchmark                                  Mode  Cnt   Score   Error  Units
 * StringBufferPerfTest.buffer                avgt    5  24.409 ± 4.486  ns/op
 * StringBufferPerfTest.bufferWithIdHashCode  avgt    5  23.753 ± 1.696  ns/op
 * </code>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 1, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(value = 1, jvmArgsAppend = {"-XX:BiasedLockingStartupDelay=0"})
@State(Scope.Benchmark)
public class StringBufferPerfTest {

    StringBuffer buffer, bufferWithIdHashCode;

    @Setup
    public void setup(Blackhole bh) {
        buffer = new StringBuffer();
        bufferWithIdHashCode = new StringBuffer();
        bh.consume(System.identityHashCode(bufferWithIdHashCode));
    }

    @Benchmark
    public String buffer() {
        return buffer.toString();
    }

    @Benchmark
    public String bufferWithIdHashCode() {
        return bufferWithIdHashCode.toString();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringBufferPerfTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}