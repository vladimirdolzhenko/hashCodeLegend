package com;

import java.util.ArrayList;
import java.util.List;

/**
 * Run with <code>-Xms256m -Xmx256m -XX:+UseSerialGC</code>
 * <p>
 * expect to see <code>java.lang.OutOfMemoryError: Java heap space</code>
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class ObjectHashReallocation {

    public static void main(String[] args) {
        final Object БАРУХ = new Object();
        final int hashCodeБаруха = БАРУХ.hashCode();

        List туса = new ArrayList();
        туса.add(БАРУХ);

        while (true) {
            Object чувак = new Object();

            туса.add(чувак);

            int инойHashCodeБаруха = БАРУХ.hashCode();

            if (hashCodeБаруха != инойHashCodeБаруха) {
                throw new RuntimeException(
                        String.format("\nHashCode Баруха переехал: с 0x%08X -> 0x%08X"
                                        + "\nпосле набега %,d чуваков\n",
                                hashCodeБаруха, инойHashCodeБаруха, туса.size()));
            }
        }
    }

}
