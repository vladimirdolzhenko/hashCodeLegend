package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import static com.VMTools.checkVMHashCodeAsAddress;

/**
 * <code>-XX:hashCode=4 -Xms256m -Xmx256m -XX:+UseSerialGC</code>
 * <p>
 * expect to see hash collisions with big diff
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class ThreadedIdentityHashCodeCollision {

    public static void main(String[] args) throws InterruptedException {
        checkVMHashCodeAsAddress();

        final int threads = Integer.parseInt(args[0]);
        final String prefix = "thread-";

        final CountDownLatch shutDownLatch = new CountDownLatch(threads);

        final List gcKeeper = new ArrayList(2_000_000);
        final TIntSet uniqueHashCodes = new TIntHashSet(2_000_000);
        final int[] objectsPerThread = new int[threads];
        final int[] hashCodesPerThread = new int[threads];

        final Phaser phaser = new Phaser(threads);
        final AtomicBoolean collision = new AtomicBoolean(false);

        IntStream.range(0, threads).forEach(threadNo -> { Thread thread = new Thread(() -> {
                try {
                    while (!collision.get()) {
                        phaser.arriveAndAwaitAdvance();

                        synchronized (uniqueHashCodes) {
                            final Object obj = new Object();
                            gcKeeper.add(obj);
                            hashCodesPerThread[threadNo] = obj.hashCode();
                            objectsPerThread[threadNo]++;
                            collision.compareAndSet(false,
                                    !uniqueHashCodes.add(obj.hashCode()));
                        }
                    }
                } finally {
                    phaser.arriveAndDeregister();
                    shutDownLatch.countDown();
                }
            });
            thread.setName(prefix + threadNo);
            thread.start();
        });

        shutDownLatch.await();
        synchronized (uniqueHashCodes) {
            System.out.printf("size: %,d\n", gcKeeper.size());
            Arrays.sort(objectsPerThread);
            System.out.printf("objects in thread: %s\n", Arrays.toString(objectsPerThread));

            StringIntPair[] stringIntPairs = new StringIntPair[hashCodesPerThread.length];
            for (int i = 0; i < stringIntPairs.length; i++) {
                stringIntPairs[i] = new StringIntPair(prefix + i, hashCodesPerThread[i]);
            }
            Arrays.sort(stringIntPairs);

            for (int i = 1; i < threads; i++) {
                System.out.printf("diff %s - %s : %,d\n",
                        stringIntPairs[i].string, stringIntPairs[i - 1].string,
                        stringIntPairs[i].value - stringIntPairs[i - 1].value);
            }
        }
    }

    private static class StringIntPair implements Comparable<StringIntPair> {
        final String string;
        final int value;

        public StringIntPair(String string, int value) {
            this.string = string;
            this.value = value;
        }

        @Override
        public int compareTo(StringIntPair o) {
            return Integer.compare(value, o.value);
        }
    }
}
