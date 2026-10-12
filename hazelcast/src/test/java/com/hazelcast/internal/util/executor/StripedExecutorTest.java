
package com.hazelcast.internal.util.executor;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LogEvent;
import org.junit.jupiter.api.Test;
import java.util.logging.Level;

import static org.mockito.Mockito.mock;

public class StripedExecutorTest {

    //@Test
    public void testUpdate() throws InterruptedException {
        ILogger logger = mock(ILogger.class);

        new AtomicTestBuilder<>(() -> new StripedExecutor(logger, "test-thread", 2, 100))
            .addWrite(executor -> executor.execute(new Runnable() {
                @Override
                public void run() {
                }
            }))
            .addWrite(executor -> executor.execute(new StripedRunnable() {
                @Override
                public int getKey() {
                    return 42;
                }
                @Override
                public void run() {
                }
            }))
            .addWrite(executor -> executor.shutdown())
            .addReadOnly(executor -> executor.getWorkQueueSize())
            .addReadOnly(executor -> executor.processedCount())
            .addReadOnly(executor -> executor.isLive())
            .addReadOnly(executor -> executor.getTaskQueues())
            .runTests();
    }
}
