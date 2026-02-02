package com.datastax.oss.driver.internal.core.channel;

import com.datastax.oss.driver.api.core.config.DriverConfig;
import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.internal.core.time.AtomicTimestampGenerator;
import com.vmlens.api.atomic.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
