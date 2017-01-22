package com;

import java.util.ArrayList;
import java.util.List;

import static com.ObjectMemoryDump.getAddress;

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
        final Object theObject = new Object();
        final long initialHashCode = theObject.hashCode();

        List gcKeeper = new ArrayList();
        gcKeeper.add(theObject);

        long currentHashCode = initialHashCode;
        while (initialHashCode == currentHashCode) {
            Object o = new Object();

            gcKeeper.add(o);

            currentHashCode = theObject.hashCode();
        }

        System.out.printf("HashCode has been changed: 0x%08X -> 0x%08X"
                        + "\nafter %,d allocations\n",
                initialHashCode, currentHashCode, gcKeeper.size());
    }

}
