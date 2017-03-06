package com;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import static com.VMTools.checkVMHashCodeAsAddress;

/**
 * <code>-XX:hashCode=4 -Xms256m -Xmx256m -XX:+UseSerialGC</code>
 * <p>
 * expect to see hash collision after ~ 2mio objects
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class IdentityHashCodeCollision {

    public static void main(String[] args) {
        checkVMHashCodeAsAddress();

        final int maxCollisions = Integer.parseInt(args[0]);
        final int[] collisions = new int[maxCollisions];

        final List gcKeeper = new ArrayList();
        final TIntSet uniqueHashCodes = new TIntHashSet();

        for (int collisionNo = 0; collisionNo < maxCollisions; ) {
            final Object obj = new Object();
            gcKeeper.add(obj);

            int hashCode = obj.hashCode();

            if (!uniqueHashCodes.add(hashCode)) {
                collisions[collisionNo++] = hashCode;
            }
        }

        System.out.printf("after %,d allocation:\n", gcKeeper.size());
        for (int i = 0; i < collisions.length; i++) {
            System.out.printf("hash code collision at 0x%04X\n", collisions[i]);
        }

    }
}
