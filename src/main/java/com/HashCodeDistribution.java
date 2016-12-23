package com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * <code>-XX:hashCode=4</code>
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-27
 */
public class HashCodeDistribution {

	public static void main(String[] args) throws IOException, InterruptedException {
		final int count = 10;
		final int perThread = 1_000_000;
		final Collection list = new ArrayList(perThread * count);
		final File file = new File(args[0]);
		try (final FileWriter writer = new FileWriter(file)) {

			writer.append("hashCode").append("\n");

			final CountDownLatch startLatch = new CountDownLatch(1);
			final CountDownLatch latch = new CountDownLatch(count);

			for(int k = 0; k < count; k++) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							startLatch.await();
							for (int i = 0; i < perThread; i++) {
								final Object o = new Object();
								int code = System.identityHashCode(o);
								String csq = Integer.toString(code);
								synchronized (writer) {
									list.add(csq);
									writer.append(csq).append("\n");
								}
							}
						} catch (Throwable e) {
							e.printStackTrace();
						} finally {
							latch.countDown();
						}
					}
				}, "thread-" + k);

				thread.start();

			}
			startLatch.countDown();

			latch.await();
		}
	}
}
