package com.vmlens.tutorial;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldTest {

    private volatile int j = 0;

    //@Disabled
    @Test
    public void testUpdate() throws InterruptedException {
        try (AllInterleavings testUpdate = new AllInterleavings("FieldTest")) {
            while (testUpdate.hasNext()) {
                j = 0;
                Thread first = new Thread(() -> j++);
                first.start();
                j++;
                first.join();
                assertEquals(2, j);
            }
        }
    }
}
