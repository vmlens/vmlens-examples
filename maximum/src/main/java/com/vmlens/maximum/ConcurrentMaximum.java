package com.vmlens.maximum;

import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentMaximum {

    private final AtomicInteger maximum = new AtomicInteger();

    public void update(int newMax) {
        int current = maximum.get();
        if (current >= newMax) {
            return;
        }
        while (!maximum.compareAndSet(current,newMax) ) {
            current = maximum.get();
            if (current >= newMax) {
               return;
            }
        }
    }

    public int get() {
        return maximum.get();
    }
}
