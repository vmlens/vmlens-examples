package com.vmlens.examples.tutorial.counter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vmlens.api.AllInterleavings;

public class TestCounterNonVolatile {
	int i = 0;
	@Test
	public void test() throws InterruptedException {
		try (AllInterleavings allInterleavings = 
				new AllInterleavings("tutorial.counter.TestCounterNonVolatile");) {
			while (allInterleavings.hasNext()) {
				i = 0;
				Thread first = new Thread(() -> {
					i++;
				});
				Thread second = new Thread(() -> {
					i++;
				});
				first.start();
				second.start();

				first.join();
				second.join();
				
				assertEquals(2,i);
				
			}
		}
	}
}
