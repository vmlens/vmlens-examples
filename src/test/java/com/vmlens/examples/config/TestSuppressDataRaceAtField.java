package com.vmlens.examples.config;

import org.junit.Test;

import com.vmlens.api.AllInterleavings;

public class TestSuppressDataRaceAtField {
	private static final Object LOCK_1 = new Object();
	private static final Object LOCK_2 = new Object();
	int i = 0;
	@Test
	public void test() throws InterruptedException {
		try (AllInterleavings allInterleavings = 
				new AllInterleavings
				("config.TestSuppressDataRaceAtField");) {
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
