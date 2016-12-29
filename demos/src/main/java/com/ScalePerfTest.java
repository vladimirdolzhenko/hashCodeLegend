package com;

/*

Benchmark          (troll)  Mode  Cnt      Score      Error  Units
ScalePerfTest.and    false  avgt    3   3306.160 ±  268.469  ns/op
ScalePerfTest.and     true  avgt    3   4382.391 ±  419.087  ns/op
ScalePerfTest.mod    false  avgt    3  23621.903 ± 5087.981  ns/op
ScalePerfTest.mod     true  avgt    3  29476.691 ± 3059.900  ns/op

 `and` is degraded in 32 %
 `mod` is degraded in 24 %
 */

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author vladimir.dolzhenko
 * @since 2016-12-30
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@State(Scope.Benchmark)
public class ScalePerfTest {

    @Param({"false", "true"})
    public boolean troll;

    public int[] array;

    @Setup
    public void setup() {
        array = new int[1 << 13];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }

        if (troll) {
            final CountDownLatch latch = new CountDownLatch(1);

            AtomicLong v = new AtomicLong();
            for (int i = 0; i < 7; i++) {
                Thread thread = new Thread(() -> {
                    try {
                        latch.await();
                        System.out.println(Thread.currentThread().getName() + " is running...");

                        double k = 1000.0;

                        while (true) {
                            k *= 8.0 / 7.0;
                            //v.getAndAdd((long) k);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + " completed.");
                });
                thread.setName("troll-" + i);
                thread.setDaemon(true);
                thread.start();
            }

            latch.countDown();
        }
    }

    @Benchmark
    public int mod(){
        int s = 0;
        for(int i = 0, len = array.length; i < len; i++) {
            s += array[i] % array.length;
        }
        return s;
    }

    @Benchmark
    public int and(){
        int s = 0;
        for(int i = 0, len = array.length; i < len; i++) {
            s += array[i] & (array.length - 1);
        }
        return s;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ScalePerfTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
