package com;

import java.util.ArrayList;
import java.util.List;

import static com.ObjectMemoryDump.print;
import static com.ObjectMemoryDump.printValues;
import static com.VMTools.getTID;

/**
 * Run with <code>-XX:BiasedLockingStartupDelay=0</code>
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-12-21
 */
public class BiasedLockingDump {
    public static void main(String[] args) throws Exception {
        Object object = new Object();

        String[] dump0 = dump(object);

        final List list = new ArrayList();

        synchronized (object) {
            list.add(object);
        }

        final long tid = getTID(Thread.currentThread().getName());

        String[] dump1 = dump(object);

        int hashCode = System.identityHashCode(object);

        String[] dump2 = dump(object);

        synchronized (object) {
            list.add(object);
        }

        System.out.printf(
                "Thread id:\t\t\t%016X\n"
                + "System hashCode:\t%08X\n", tid, hashCode);
        printValues(new String[][]{
                print(null),
                dump0,
                dump1,
                dump2
        });

        for(int i = 0; i < 10_000_000; i++){
            list.add(new Object());
        }
    }

    //    tid:              0x00000000 02 13 80 00
    //    System hashCode:  02530C12
    //    class     address        next addr      id hashcode    size    dump
    //    Object    0x76B038730    0x76B038740    N/A            16      05 00 00 00 00 00 00 00 E5 01 00 F8 00 00 00 00
    //    Object    0x76B038730    0x76B038740    N/A            16      05 80 13 02 00 00 00 00 E5 01 00 F8 00 00 00 00
    //    Object    0x76B038730    0x76B038740    N/A            16      01 12 0C 53 02 00 00 00 E5 01 00 F8 00 00 00 00


    public static String[] dump(Object o){
        return print(o, false);
    }
}
