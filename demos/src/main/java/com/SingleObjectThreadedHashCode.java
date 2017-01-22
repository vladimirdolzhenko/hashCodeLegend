package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.VMTools.checkVMHashCodeAsAddress;

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
        checkVMHashCodeAsAddress();

        final int threads = Integer.parseInt(args[0]);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch shutDownLatch = new CountDownLatch(threads);
        final CountDownLatch threadStartLatch = new CountDownLatch(threads);

        final AtomicInteger index = new AtomicInteger();
        final int[] hashCodes = new int[threads];

        IntStream.range(0, threads).forEach(k -> {
            Thread thread = new Thread(() -> {
                try {
                    final List туса = new ArrayList( 1000 );
                    threadStartLatch.countDown();
                    startLatch.await();

                    synchronized (hashCodes){
                        final Object object = new Object();
                        int hashCode = object.hashCode();
                        hashCodes[index.getAndIncrement()] = hashCode;
                        туса.add(object);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    shutDownLatch.countDown();
                }
            });
            thread.setName("thread-" + k);
            thread.start();
        });
        threadStartLatch.await();
        startLatch.countDown();

        shutDownLatch.await();

        synchronized (hashCodes) {
            Arrays.sort(hashCodes);
            for (int hashCode : hashCodes) {
                System.out.printf("hashCode: 0x%08X%n", hashCode);
            }
            for (int i = 1; i < threads; i++) {
                System.out.printf("diff %d - %d : %,d%n",
                                  i,
                                  i - 1,
                                  (hashCodes[i] - hashCodes[i - 1])
                );
            }
        }
    }
}
