package com;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class IdentityHashCodeCollision {

	public static void main(String[] args) {
		final List list = new ArrayList();
		final Set<Integer> codes = new HashSet<>();

		for (int i = 0; ; i++) {
			final Object obj = new Object();
			list.add(obj);

			int hashCode = System.identityHashCode(obj);

			if (!codes.add(hashCode)) {
				throw new RuntimeException(
						String.format("\nhash code collision 0x%04x after %d iterations",
								hashCode, i));
			}
		}
	}
}
