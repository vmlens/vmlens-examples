package com.vmlens.presentation.deterministic;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CounterWithVMLensTest {

    AtomicInteger integer = new AtomicInteger();

    @Test
    public void testIncrement() throws InterruptedException {
        try(AllInterleavings allInterleavings =
                    new AllInterleavings("yavaconf")) {
            while (allInterleavings.hasNext()) {
                integer.set(0);
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        integer.incrementAndGet();
                    }
                };
                first.start();
                integer.incrementAndGet();
                first.join();
                assertThat( integer.get(), is(2));
            }
        }
    }

}
