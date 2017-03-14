package com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.stream.IntStream;

import com.koloboke.collect.set.hash.HashIntSet;
import com.koloboke.collect.set.hash.HashIntSets;

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

    public static void main(String[] args) throws InterruptedException, IOException {
        checkVMHashCodeAsAddress();

        final int threads = Integer.parseInt(args[0]);
        final int count = Integer.parseInt(args[1]);
        final String prefix = "thread-";

        final CountDownLatch shutDownLatch = new CountDownLatch(threads);

        final List gcKeeper = new ArrayList(threads * 1_500_000);
        final int[][] hashCodesPerThread = new int[threads][count];

        System.gc();
        System.gc();
        System.gc();
        System.gc();

        final Phaser phaser = new Phaser(threads);

        IntStream.range(0, threads).forEach(threadNo -> { Thread thread = new Thread(() -> {
                try {
                    for(int index = 0; index < count; index++) {
                        phaser.arriveAndAwaitAdvance();

                        final Object obj = new Object();
                        gcKeeper.add(obj);
                        hashCodesPerThread[threadNo][index] = obj.hashCode();
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

            StringIntPair[] stringIntPairs = new StringIntPair[hashCodesPerThread.length];
            for (int i = 0; i < stringIntPairs.length; i++) {
                stringIntPairs[i] = new StringIntPair(prefix + i, hashCodesPerThread[i][count - 1]);
            }
            Arrays.sort(stringIntPairs);

            for (int i = 0; i < threads; i++) {
                System.out.printf("%s\t", prefix + i);
            }

        System.out.println();
        final HashIntSet uniqueCodes = HashIntSets.newMutableSet(threads * count);
        boolean found = false;
        Integer collisionIndex = null;
        Integer collisionValue = null;
        boolean out = false;
        for (int j = 0; j < count; j++) {
            for (int i = 0; i < threads; i++) {
                if (!found && !uniqueCodes.add(hashCodesPerThread[i][j])){
                    found = true;
                    collisionIndex = j;
                    collisionValue = hashCodesPerThread[i][j];
                }
                if (out)
                System.out.printf("%04X\t", hashCodesPerThread[i][j]);
            }
            if (out)
            System.out.println();
        }

        for (int i = 1; i < threads; i++) {
            System.out.printf("diff %s - %s : %,d\n",
                    stringIntPairs[i].string, stringIntPairs[i - 1].string,
                    stringIntPairs[i].value - stringIntPairs[i - 1].value);
        }

        System.out.println();
        System.out.println();

        System.out.println("Collision " + collisionValue + " @ " + collisionIndex);


        try(FileWriter fileWriter = new FileWriter(new File("hashCodes.csv"))) {
            fileWriter.append("index,thread,address\n");
            for (int j = 0; j < count; j++) {
                for (int i = 0; i < threads; i++) {
                    fileWriter.append((j + 1) + "," + (prefix + i) + ","
                            + (hashCodesPerThread[i][j] != 0 ? hashCodesPerThread[i][j] : "")
                            + "\n");
                }
                if (j >= 500){
                    j += 99;
                }
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
