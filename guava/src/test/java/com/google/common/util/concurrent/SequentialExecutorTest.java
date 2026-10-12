
package com.google.common.util.concurrent;

import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;

public class SequentialExecutorTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> new SequentialExecutor(command -> command.run()))
                .addWrite(executor -> executor.execute(() -> {
                    // Task A: Simple empty task
                }))
                .addWrite(executor -> executor.execute(() -> {
                    // Task B: Different task to satisfy the two-call requirement
                    int i = 0;
                    i++;
                }))
                .addReadOnly(executor -> executor.toString())
                .runTests();
    }
}
