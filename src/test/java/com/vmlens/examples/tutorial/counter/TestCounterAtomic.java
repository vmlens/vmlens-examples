package com.vmlens.examples.tutorial.counter;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.vmlens.api.AllInterleavings;

public class TestCounterAtomic {
	AtomicInteger i = new AtomicInteger();
	@Test
	public void test() throws InterruptedException {
		try (AllInterleavings allInterleavings = 
		   new AllInterleavings
				("tutorial.counter.TestCounterAtomic");) {
			while (allInterleavings.hasNext()) {
				i.set(0);
				Thread first = new Thread(() -> {
					i.incrementAndGet();
				});
				Thread second = new Thread(() -> {
					i.incrementAndGet();
				});
				first.start();
				second.start();

				first.join();
				second.join();
				
				assertEquals(2,i.get());
				
			}
		}
	}
}
