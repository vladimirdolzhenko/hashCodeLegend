package com;

/*
Benchmark                             Mode  Cnt    Score    Error  Units
MallocPerfTest.casAllocator           avgt    5  118.679 ±  2.456  ns/op
MallocPerfTest.javaAllocation         avgt    5    6.350 ±  0.022  ns/op
MallocPerfTest.javaObjectAndHashCode  avgt    5   80.181 ±  1.759  ns/op
MallocPerfTest.readHashCode           avgt    5    4.094 ±  0.137  ns/op
MallocPerfTest.simpleAllocator        avgt    5    3.553 ±  0.125  ns/op
MallocPerfTest.syncAllocator          avgt    5  160.631 ± 24.677  ns/op
MallocPerfTest.tlabAllocator          avgt    5    8.083 ±  0.143  ns/op
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
@Fork(4)
@Warmup(iterations = 2, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
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
    Object theObject;

    @Setup
    public void setup() {
        simpleAllocator = new SimpleAllocator();
        syncAllocator = new SyncAllocator();
        casAllocator = new CASAllocator();
        tlabAllocator = new TLABLikeAllocator();
        theObject = new Object();
    }

//    @Benchmark
//    @Threads(1)
//    public long simpleAllocator() {
//        return simpleAllocator.malloc(16);
//    }

//    @Benchmark
//    @Threads(4)
//    public long syncAllocator() {
//        return syncAllocator.malloc(16);
//    }

//    @Benchmark
//    @Threads(4)
//    public long casAllocator() {
//        return casAllocator.malloc(16);
//    }

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

//    @Benchmark
//    @Threads(4)
//    public Object javaObjectAndHashCode() {
//        Object object = new Object();
//        object.hashCode();
//        return object;
//    }
//
//    @Benchmark
//    @Threads(4)
//    public int readHashCode() {
//        return theObject.hashCode();
//    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MallocPerfTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}