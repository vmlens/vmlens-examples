package com.vmlens.examples.deadlock;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

public class TestDeadlock {

    Object LOCK_A = new Object();
    Object LOCK_B = new Object();

    @Test
    public void readWrite() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("examples.deadlock")) {
            while (allInterleavings.hasNext()) {
                // When
                Thread first = new Thread() {
                    @Override
                    public void run() {
                      synchronized (LOCK_A) {
                          synchronized (LOCK_B) {

                          }
                      }
                    }
                };
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
