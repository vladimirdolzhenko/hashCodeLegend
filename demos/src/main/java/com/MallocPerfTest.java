package com;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(10)
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
                return memoryPointer += size;
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

        @Override
        public long malloc(long size) {
            AddressHolder addressHolder = THREAD_LOCAL.get();

            while(true) {
                if (addressHolder.value + size < addressHolder.maxValue) {
                    return addressHolder.value += size;
                }
                long value = memoryPointer.getAndAdd( 1L << 20 );
                addressHolder.value = value;
                addressHolder.maxValue = value + ( 1L << 20 );
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

//    @Benchmark
//    public long simpleAllocator() {
//        return simpleAllocator.malloc(16);
//    }

    @Benchmark
    public long syncAllocator() {
        return syncAllocator.malloc(16);
    }

    @Benchmark
    public long casAllocator() {
        return casAllocator.malloc(16);
    }

    @Benchmark
    public long tlabAllocator() {
        return tlabAllocator.malloc(16);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MallocPerfTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}