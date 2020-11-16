package com.vmlens.examples.config;

import org.junit.Test;

import com.vmlens.api.AllInterleavings;

public class TestSuppressPrimitiveArrayWithAt {
	
	
	private int[] array = new int[1];
	
	
	public void updateArray() {
		array[0] = 1;
	}
	
	
	@Test
	public void test() throws InterruptedException {
		try (AllInterleavings allInterleavings = 
				new AllInterleavings
				("config.TestSuppressPrimitiveArrayWithAt");) {
			while (allInterleavings.hasNext()) {
				Thread first = new Thread(() -> {
					updateArray();
				});
				Thread second = new Thread(() -> {
					updateArray();
				});
				first.start();
				second.start();

				first.join();
				second.join();
			}
		}
	}
}
