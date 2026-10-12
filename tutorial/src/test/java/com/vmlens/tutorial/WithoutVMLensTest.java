package com.vmlens.tutorial;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WithoutVMLensTest {

    private volatile int j = 0;

    @Disabled
    @RepeatedTest(100000)
    public void testUpdate() throws InterruptedException {
            j = 0;
            Thread first = new Thread(() -> j++);
            first.start();
            j++;
            first.join();
            assertEquals(2,j);
    }

}
