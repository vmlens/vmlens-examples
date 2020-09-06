package com.vmlens.examples.tutorial.counter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vmlens.api.AllInterleavings;

public class TestCounterSynchronized {

	 final Object LOCK = new Object(); 
	 int i = 0;

	@Test
	public void test() throws InterruptedException {
		try (AllInterleavings allInterleavings = new AllInterleavings("tutorial.counter.TestCounterSynchronized");) {
			while (allInterleavings.hasNext()) {
				i = 0;
				Thread first = new Thread(() -> {
				synchronized(LOCK)
				{
					i++;
				}
					
				});
				Thread second = new Thread(() -> {
					synchronized(LOCK)
					{
						i++;
					}
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
