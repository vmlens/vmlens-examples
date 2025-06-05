package com.vmlens.examples.newway;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestWithReentrantLock {

    private int j = 0;
    private final Lock lock = new ReentrantLock();

    @Test
    public void testIncrement() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("testWithReentrantLock")) {
            while (allInterleavings.hasNext()) {
                j = 0;
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        increment();
                    }
                };
                first.start();
                increment();
                first.join();
                assertThat(j,is(2));
            }
        }
    }
    private void increment() {
        lock.lock();
        try{
            j++;
        }
        finally {
            lock.unlock();
        }
    }
}
