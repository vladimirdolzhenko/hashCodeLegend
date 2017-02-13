package com;

/*
Benchmark                             Mode  Cnt    Score    Error  Units
MallocPerfTest.casAllocator           avgt   10   74.721 ±  0.192  ns/op
MallocPerfTest.javaAllocation         avgt   10    9.878 ±  2.676  ns/op
MallocPerfTest.javaObjectAndHashCode  avgt   10   60.270 ± 12.318  ns/op
MallocPerfTest.simpleAllocator        avgt   10    2.836 ±  0.285  ns/op
MallocPerfTest.syncAllocator          avgt   10  186.672 ± 21.195  ns/op
MallocPerfTest.tlabAllocator          avgt   10    8.506 ±  1.849  ns/op
*/

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class MallocPerfTest {

    public interface Allocator {
        long malloc(long size);
    }

    public static class SimpleAllocator implements Allocator {
        private long memoryPointer;

        @Override
        public long malloc(long size) {
            return memoryPointer += size;
        }
    }

    public static class SyncAllocator implements Allocator {
        private long memoryPointer;

        @Override
        public long malloc(long size) {
            synchronized (this) {
                long old = memoryPointer;
                memoryPointer += size;
                return old;
            }
        }
    }

    public static class CASAllocator implements Allocator {
        private final AtomicLong memoryPointer = new AtomicLong();

        @Override
        public long malloc(long size) {
            return memoryPointer.getAndAdd(size);
        }
    }

    static class AddressHolder {
        long value, maxValue;
    }

    public static class TLABLikeAllocator implements Allocator {
        private final AtomicLong memoryPointer = new AtomicLong();
        private static final ThreadLocal<AddressHolder> THREAD_LOCAL = ThreadLocal.withInitial(() -> new AddressHolder());
        private final long tlabSize;

        public TLABLikeAllocator(long tlabSize) {
            this.tlabSize = tlabSize;
        }

        public TLABLikeAllocator() {
            this(1L << 20);
        }

        @Override
        public long malloc(long size) {
            AddressHolder addressHolder = THREAD_LOCAL.get();

            while(true) {
                if (addressHolder.value + size <= addressHolder.maxValue) {
                    long old = addressHolder.value;
                    addressHolder.value += size;
                    return old;
                }
                long value = memoryPointer.getAndAdd( tlabSize );
                addressHolder.value = value;
                addressHolder.maxValue = value + tlabSize;
            }
        }
    }

    Allocator simpleAllocator, syncAllocator, casAllocator, tlabAllocator;

    @Setup
    public void setup() {
        simpleAllocator = new SimpleAllocator();
        syncAllocator = new SyncAllocator();
        casAllocator = new CASAllocator();
        tlabAllocator = new TLABLikeAllocator();
    }

    @Benchmark
    @Threads(1)
    public long simpleAllocator() {
        return simpleAllocator.malloc(16);
    }

    @Benchmark
    @Threads(4)
    public long syncAllocator() {
        return syncAllocator.malloc(16);
    }

    @Benchmark
    @Threads(4)
    public long casAllocator() {
        return casAllocator.malloc(16);
    }

    @Benchmark
    @Threads(4)
    public long tlabAllocator() {
        return tlabAllocator.malloc(16);
    }

    @Benchmark
    @Threads(4)
    public Object javaAllocation() {
        return new Object();
    }

    @Benchmark
    @Threads(4)
    public Object javaObjectAndHashCode() {
        Object object = new Object();
        object.hashCode();
        return object;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MallocPerfTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}