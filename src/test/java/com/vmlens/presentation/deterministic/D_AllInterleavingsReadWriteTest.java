package com.vmlens.presentation.deterministic;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

public class D_AllInterleavingsReadWriteTest {

    private volatile int j = 0;

    @Test
    public void testReadWrite() throws InterruptedException {
        int run = 0;
        try(AllInterleavings allInterleavings =
                    new AllInterleavings("xtremej.nondeterministic.readwrite")) {
            while (allInterleavings.hasNext()) {
                j = 0;
                Thread first = new Thread(() -> j = 5);
                first.start();
                int i = j;
                System.out.println("run:" + run + ", variable j:"+i);
                first.join();
                run++;
            }
        }
    }
}
