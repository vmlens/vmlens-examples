package com.vmlens.presentation.deterministic;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class H_DeadlockTest {

    private final Object LOCK_A = new Object();
    private final Object LOCK_B = new Object();

    @Disabled
    @Test
    public void readWrite() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("examples.deadlock")) {
            while (allInterleavings.hasNext()) {
                Thread first = new Thread(() -> {
                    synchronized (LOCK_A) {
                        synchronized (LOCK_B) {
                        }
                    }
                });
                first.start();
                synchronized (LOCK_B) {
                    synchronized (LOCK_A) {
                    }
                }
                first.join();
            }
        }
    }

}
