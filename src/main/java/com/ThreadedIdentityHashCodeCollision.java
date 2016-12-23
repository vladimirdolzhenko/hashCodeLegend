package com;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * <code>-XX:hashCode=4</code>
 * <p>
 * <code>-XX:+PrintTLAB</code>
 * expect to see last TLAB: <code>slow allocs: > 0 and refills: > 1</code>
 * <p>
 * see https://blogs.oracle.com/jonthecollector/entry/the_real_thing
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class ThreadedIdentityHashCodeCollision {

    public static void main(String[] args) throws InterruptedException {
        final List list = new ArrayList();
        final Set<Integer> codes = new HashSet<>();

        final int count = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch latch = new CountDownLatch(1);

        for (int k = 0; k < count; k++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int i = 0; ; i++) {
                            final Object object = new Object();
                            synchronized (list) {
                                list.add(object);
                                int hashCode = object.hashCode();
                                if (!codes.add(hashCode)) {
                                    throw new RuntimeException(
                                            String.format("\n%s: hash code collision 0x%04x after %d iterations",
                                                    Thread.currentThread().getName(),
                                                    hashCode,
                                                    i));
                                }
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
            thread.setName("thread-" + k);
            thread.start();

        }
        startLatch.countDown();

        latch.await();
    }
}
