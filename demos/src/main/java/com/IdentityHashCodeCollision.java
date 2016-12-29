package com;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.List;

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
		final List туса = new ArrayList(2_000_000);
		final TIntSet уникальныеКоды = new TIntHashSet(2_000_000);

		while (true) {
			final Object чувак = new Object();
			туса.add(чувак);

			int hashCode = чувак.hashCode();

			if (!уникальныеКоды.add(hashCode)) {
				throw new RuntimeException(
						String.format("\nДрака по hash code 0x%04x после набега %,d чуваков",
								hashCode, туса.size()));
			}
		}
	}
}
