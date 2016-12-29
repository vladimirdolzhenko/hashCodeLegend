package com;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * <code>-XX:hashCode=4 -Xms256m -Xmx256m -XX:+UseSerialGC</code>
 * <p>
 * <code>-XX:+UseTLAB</code> expect to see last diff ~1 Mb
 * <p>
 * <code>-XX:-UseTLAB</code> expect to see last diff: 16
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class SingleObjectThreadedHashCode {

    public static void main(String[] args) throws InterruptedException {
        final int threads = Integer.parseInt(args[0]);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch shutDownLatch = new CountDownLatch(threads);
        final CountDownLatch threadStartLatch = new CountDownLatch(threads);

        final AtomicInteger index = new AtomicInteger();
        final int[] hashCodes = new int[threads];

        IntStream.range(0, threads).forEach(k -> {
            Thread thread = new Thread(() -> {
                try {
                    //System.out.printf("%s запущена\n", Thread.currentThread().getName());

                    final List туса = new ArrayList( 1000 );
                    threadStartLatch.countDown();
                    startLatch.await();

                    synchronized (hashCodes){
                        final Object чувак = new Object();
                        int hashCode = чувак.hashCode();
                        hashCodes[index.getAndIncrement()] = hashCode;
                        туса.add(чувак);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    //System.out.printf("%s завершена\n", Thread.currentThread().getName());
                    shutDownLatch.countDown();
                }
            });
            thread.setName("НагнетательТусы-" + k);
            thread.start();
        });
        threadStartLatch.await();
        startLatch.countDown();

        shutDownLatch.await();

        synchronized (hashCodes) {
            Arrays.sort(hashCodes);
            for (int hashCode : hashCodes) {
                System.out.printf("hashCode: 0x%08X\n", hashCode);
            }
            for (int i = 1; i < threads; i++) {
                System.out.printf("разница между %d и %d : %,d\n",
                        i,
                        i - 1,
                        (hashCodes[i] - hashCodes[i - 1])
                );
            }
        }
    }
}
