package com.vmlens.maximum;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class ConcurrentMaximumTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(ConcurrentMaximum::new)
                .addWrite((maximum) -> maximum.update(5))
                .addWrite((maximum) -> maximum.update(10) )
                .addReadOnly(ConcurrentMaximum::get)
                .runTests();
    }

}
