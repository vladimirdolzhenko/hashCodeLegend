package com;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;

/**
 * @author vladimir.dolzhenko@gmail.com
 * @since 2017-01-23
 */
public class BiasedThreadDump {
    public static void main(String[] args) throws Exception {
        final Object lock = new Object();

        System.out.printf("0x%08X", lock.hashCode());

        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                synchronized (lock){
                    System.out.println("waiting for ....");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "lock").start();

        latch.await();

        synchronized (lock){
            lock.notifyAll();
        }

        for(int i = 20; i >= 0; i--) {
            Thread.sleep(1_000);
            System.out.println(i + "... ");
        }

        System.out.printf("0x%08X", lock.hashCode());
    }

}
