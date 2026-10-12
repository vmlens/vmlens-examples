package com.vmlens.tutorial;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SynchronizedTest {

    private final Object LOCK_ONE = new Object();
    private final Object LOCK_TWO = new Object();
    private int j = 0;

    @Disabled
    @Test
    public void testWrongOrder() throws InterruptedException {
        AllInterleavings testUpdate = new AllInterleavings("SynchronizedTest");
        while (testUpdate.hasNext()) {
            j = 0;
            Thread first = new Thread(() -> lockOneToTwo() );
            first.start();
            lockOneToTwo();
            first.join();
            assertEquals(2,j);
        }
    }

    private void lockOneToTwo() {
        synchronized (LOCK_ONE) {
            synchronized (LOCK_TWO) {
                j++;
            }
        }
    }

    private void lockTwoToOne() {
        synchronized (LOCK_TWO) {
            synchronized (LOCK_ONE) {
                j++;
            }
        }
    }

}
