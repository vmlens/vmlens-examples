
package com.google.common.util.concurrent;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class ExecutionListTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(ExecutionList::new)
                .addWrite(executionList -> executionList.add(() -> {}, runnable -> runnable.run()))
                .addWrite(executionList -> executionList.add(() -> { int i = 0; }, runnable -> { runnable.run(); }))
                .addWrite(ExecutionList::execute)
                .runTests();
    }
}
