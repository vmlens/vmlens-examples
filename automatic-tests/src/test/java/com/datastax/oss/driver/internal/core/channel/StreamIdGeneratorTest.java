package com.datastax.oss.driver.internal.core.channel;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;



public class StreamIdGeneratorTest {

    @Test
    public void testAtomic() {
        new AtomicTestBuilder<>(
                () ->
                     new StreamIdGenerator(5))
                .addWrite(StreamIdGenerator::preAcquire)
                .runTests();
    }

}
