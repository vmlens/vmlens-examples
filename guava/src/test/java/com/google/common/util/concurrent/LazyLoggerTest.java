
package com.google.common.util.concurrent;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class LazyLoggerTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> new LazyLogger(LazyLoggerTest.class))
                .addUpdate(LazyLogger::get)
                .runTests();
    }
}
