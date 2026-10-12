package com.vmlens.tutorial;

import com.vmlens.api.AllInterleavings;
import com.vmlens.maximum.ConcurrentMaximum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrentMaximumTest {

    private static final Object MAXIMUM_FOR_OBJECT = new Object();

    @Disabled
    @Test
    public void testCount() throws InterruptedException {

        try (AllInterleavings testUpdate = new AllInterleavings("ConcurrentMaximumTest")) {
            while (testUpdate.hasNext()) {
                ConcurrentHashMap<Object, ConcurrentMaximum> objectToMaximum = new ConcurrentHashMap<>();
                Thread first = new Thread(() -> {
                    ConcurrentMaximum maximum = objectToMaximum.computeIfAbsent(MAXIMUM_FOR_OBJECT, key -> new ConcurrentMaximum());
                    maximum.update(10);
                }
                );
                first.start();
                ConcurrentMaximum maximum = objectToMaximum.computeIfAbsent(MAXIMUM_FOR_OBJECT, key -> new ConcurrentMaximum());
                maximum.update(5);
                first.join();
                assertEquals(10, objectToMaximum.get(MAXIMUM_FOR_OBJECT).get());
            }
        }
    }

}
