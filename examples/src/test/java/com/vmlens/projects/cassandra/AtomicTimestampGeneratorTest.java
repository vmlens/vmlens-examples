package com.vmlens.projects.cassandra;

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

public class AtomicTimestampGeneratorTest {

    @Test
    public void testAtomic() {
        new AtomicTestBuilder<>(
                () ->
                {   DriverContext context = mock(DriverContext.class);
                    DriverConfig driverConfig = mock(DriverConfig.class);
                    DriverExecutionProfile config = mock(DriverExecutionProfile.class);

                    when(context.getConfig()).thenReturn(driverConfig);
                    when(driverConfig.getDefaultProfile()).thenReturn(config);
                    when(config.getDuration(any())).thenReturn(Duration.ofMillis(100));
                    return new AtomicTimestampGenerator(context);
                }

                      )
                .addWrite(AtomicTimestampGenerator::next)
                .runTests();

    }

}
