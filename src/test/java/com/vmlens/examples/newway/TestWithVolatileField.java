package com.vmlens.examples.newway;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestWithVolatileField {

    private volatile int j = 0;

    @Test
    public void testIncrement() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("testWithVolatileField")) {
            while (allInterleavings.hasNext()) {
                j = 0;
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        j++;
                    }
                };
                first.start();
                j++;
                first.join();
                assertThat(j,is(2));
            }
        }
    }

}
