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
        final int threads = Integer.parseInt(args[0]);
        final String prefix = "Нагнетатель-";

        final CountDownLatch shutDownLatch = new CountDownLatch(threads);

        final List туса = new ArrayList(2_000_000);
        final TIntSet уникальныеКоды = new TIntHashSet(2_000_000);
        final int[] hashCodesНагнетателей = new int[threads];

        final Phaser этапщик = new Phaser(threads);
        final AtomicBoolean драка = new AtomicBoolean(false);

        IntStream.range(0, threads).forEach(t -> { Thread thread = new Thread(() -> {
                try {
                    while (!драка.get()) {
                        этапщик.arriveAndAwaitAdvance();

                        synchronized (уникальныеКоды) {
                            final Object чувак = new Object();
                            туса.add(чувак);
                            hashCodesНагнетателей[t] = чувак.hashCode();
                            драка.compareAndSet(false, !уникальныеКоды.add(чувак.hashCode()));
                        }
                    }
                } finally {
                    этапщик.arriveAndDeregister();
                    shutDownLatch.countDown();
                }
            });
            thread.setName(prefix + t);
            thread.start();
        });

        shutDownLatch.await();
        synchronized (уникальныеКоды) {
            System.out.printf("размер тусы: %,d\n", туса.size());

            StringIntPair[] stringIntPairs = new StringIntPair[hashCodesНагнетателей.length];
            for (int i = 0; i < stringIntPairs.length; i++) {
                stringIntPairs[i] = new StringIntPair(prefix + i, hashCodesНагнетателей[i]);
            }
            Arrays.sort(stringIntPairs);

            for (int i = 1; i < threads; i++) {
                System.out.printf("разница %s - %s : %,d\n",
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
