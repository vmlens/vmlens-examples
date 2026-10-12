
package com.google.common.util.concurrent;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class TrustedListenableFutureTaskTest {

    //@Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<TrustedListenableFutureTask<String>>(() -> 
            TrustedListenableFutureTask.create(() -> "test"))
            // run() changes state and returns void. No arguments.
            .addWrite(task -> task.run())
            
            // cancel(boolean) changes state and returns boolean. Has argument.
            .addUpdate(task -> task.cancel(true))
            .addUpdate(task -> task.cancel(false))
            
            // addListener(Runnable, Executor) changes state and returns void. Has arguments.
            .addWrite(task -> task.addListener(() -> {}, Runnable::run))
            .addWrite(task -> task.addListener(() -> { int i = 0; }, Runnable::run))
            
            // Read-only methods
            .addReadOnly(task -> task.isDone())
            .addReadOnly(task -> task.isCancelled())
            .addReadOnly(task -> task.toString())
            
            .runTests();
    }
}
