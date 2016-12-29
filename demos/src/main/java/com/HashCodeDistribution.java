package com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * <code>-XX:hashCode=4</code>
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class HashCodeDistribution {

    public static void main(String[] args) throws IOException, InterruptedException {
        final int count = 10;
        final int perThread = 1_000_000;
        final File file = new File(args[0]);

        final AtomicIntegerArray hashCodes = new AtomicIntegerArray(count * perThread);
        final AtomicInteger index = new AtomicInteger();

        try (final FileWriter writer = new FileWriter(file)) {

            writer.append("hashCode").append("\n");

            final CountDownLatch startLatch = new CountDownLatch(1);
            final CountDownLatch latch = new CountDownLatch(count);

            for (int k = 0; k < count; k++) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Object[] objects = new Object[perThread];
                        try {
                            startLatch.await();
                            for (int i = 0; i < perThread; i++) {
                                final Object o = new Object();
                                objects[i] = 0;
                                int code = o.hashCode();
                                hashCodes.set(index.getAndIncrement(), code);

                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            latch.countDown();
                        }
                    }
                }, "thread-" + k);

                thread.start();
            }
            startLatch.countDown();

            latch.await();

            for (int i = 0; i < hashCodes.length(); i++) {
                int code = hashCodes.get(i);
                if (code < 0)
                    System.out.println(code);
                String csq = Integer.toString(code);
                writer.append(csq).append("\n");
            }
        }
    }
}
