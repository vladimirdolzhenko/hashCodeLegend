package com;

import java.util.ArrayList;
import java.util.List;

import static com.ObjectMemoryDump.getAddress;

/**
 * Run with <code>-Xms256m -Xmx256m -XX:+UseSerialGC</code>
 * expect to see <code>Exception in thread "main" java.lang.RuntimeException</code>
 * <p>
 * <code>-verbose:gc -XX:+PrintGC -XX:+PrintGCDetails</code>
 * expect to see <code>GC (Allocation Failure)</code>
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class ObjectReallocation {

    public static void main(String[] args) {
        final Object БАРУХ = new Object();
        final long адресБаруха = getAddress(БАРУХ);

        List туса = new ArrayList();
        туса.add(БАРУХ);

        while (true) {
            Object чувак = new Object();

            туса.add(чувак);

            long инойАдресБаруха = getAddress(БАРУХ);

            if (адресБаруха != инойАдресБаруха) {
                throw new RuntimeException(
                        String.format("\nБарух переехал: с 0x%08X -> 0x%08X"
                                        + "\nпосле набега %,d чуваков\n",
                                адресБаруха, инойАдресБаруха, туса.size()));
            }
        }
    }

}
