package com;

import java.util.ArrayList;
import java.util.List;

import static com.ObjectMemoryDump.getAddress;
import static com.ObjectMemoryDump.*;

/**
 * Run with <code>-XX:-UseCompressedOops</code>
 *
 * <code>-verbose:gc -XX:+PrintGC -XX:+PrintGCDetails</code>
 * expect to see <code>GC (Allocation Failure)</code>
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class ObjectReallocation {

	public static void main(String[] args) {
		objectIsMoved();
	}

	private static void objectIsMoved() {
		Object object = new Object();

		List list = new ArrayList();
		long address = getAddress(object);
		int hashCode = object.hashCode();
		list.add(object);

		for(int i = 0;;i++){
			Object o1 = new Object();
			list.add(o1);
			long address2 = getAddress(object);
			if (address != address2) {
				int hashCode2 = object.hashCode();
				throw new RuntimeException(
						String.format("\nreallocated: from 0x%08X -> 0x%08X "
										+ "after %d new object allocation(s)\n"
								+ "hashCode: 0x%08X %s 0x%08X",
						address, address2, i,
						hashCode,
						hashCode != hashCode2 ? "->" : "==",
						hashCode2));
			}
		}
	}
}
