package com.vmlens.examples.config;

import org.junit.Test;

import com.vmlens.api.AllInterleavings;

public class TestSuppressArrayWithAtAndPattern {
	
	
	private String[] array = new String[1];
	
	
	public void updateArray() {
		array[0] = "1";
	}
	
	
	@Test
	public void test() throws InterruptedException {
		try (AllInterleavings allInterleavings = 
				new AllInterleavings
				("config.TestSuppressArrayWithAtAndPattern");) {
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
