package com;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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

        final TIntSet уникальныеКоды = new TIntHashSet();
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch threadStartLatch = new CountDownLatch(threads);
        final CountDownLatch shutDownLatch = new CountDownLatch(threads);

        final AtomicInteger totalSize = new AtomicInteger();

        IntStream.range(0, threads).forEach(k -> {
            Thread thread = new Thread(() -> {
                try {
                    System.out.printf("%s запущена\n", Thread.currentThread().getName());
                    final List туса = new ArrayList();
                    threadStartLatch.countDown();
                    startLatch.await();

                    while (true) {
                        synchronized (уникальныеКоды) {
                            final Object чувак = new Object();
                            туса.add(чувак);
                            int hashCode = чувак.hashCode();
                            if (!уникальныеКоды.add(hashCode)) {
                                totalSize.addAndGet(туса.size());
                                throw new RuntimeException(
                                        String.format("\n%s: Драка по hashcode 0x%04X после набега %,d чуваков",
                                                Thread.currentThread().getName(),
                                                hashCode,
                                                туса.size()));
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    System.out.printf("%s завершена\n", Thread.currentThread().getName());
                    shutDownLatch.countDown();
                }
            });
            thread.setName("НагнетательТусы-" + k);
            thread.start();
        });
        threadStartLatch.await();
        startLatch.countDown();

        shutDownLatch.await();
        System.out.printf("total objects: %,d\n", totalSize.get());
    }
}
