package com.vmlens.examples.tests;

import org.junit.Test;
import com.vmlens.api.AllInterleavings;

/**
 * 
 * This example shows a data race because the access to the variable i is not correctly synchronized.
 * It is <a href="https://vmlens.com/help/manual/#find_data_races">described here.</a>
 * 
 * 
 * @author thomas
 *
 */

public class TestInconsistentSynchronized {
	private static final Object LOCK_1 = new Object();
	private static final Object LOCK_2 = new Object();
	int i = 0;
	@Test
	public void test() throws InterruptedException {
		try (AllInterleavings allInterleavings = 
				new AllInterleavings
				("TestInconsistentSynchronized");) {
			while (allInterleavings.hasNext()) {
				Thread first = new Thread(() -> {
					synchronized (LOCK_1) {
						i++;
					}
				});
				Thread second = new Thread(() -> {
					synchronized (LOCK_2) {
						i++;
					}
				});
				first.start();
				second.start();

				first.join();
				second.join();
			}
		}
	}
}
