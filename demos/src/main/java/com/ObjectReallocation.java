package com;

import java.util.ArrayList;
import java.util.List;

import static com.ObjectMemoryDump.getAddress;

/**
 * Run with <code>-Xms256m -Xmx256m -XX:+UseSerialGC</code>
 * expect to see <code>Object has been reallocated</code>
 * <p>
 * <code>-verbose:gc -XX:+PrintGC -XX:+PrintGCDetails</code>
 * expect to see <code>GC (Allocation Failure)</code>
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class ObjectReallocation {

    public static void main(String[] args) {
        final Object theObject = new Object();
        final long initialAddress = getAddress(theObject);

        List gcKeeper = new ArrayList();
        gcKeeper.add(theObject);

        long currentAddress = initialAddress;
        while (initialAddress == currentAddress) {
            Object o = new Object();

            gcKeeper.add(o);

            currentAddress = getAddress(theObject);
        }

        System.out.printf("Object has been reallocated: 0x%08X -> 0x%08X"
                        + "\nafter %,d allocations\n",
                initialAddress, currentAddress, gcKeeper.size());
    }

}
