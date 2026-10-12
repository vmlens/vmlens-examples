package org.apache.lucene.index;

import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;

public class DocumentsWriterStallControlTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(DocumentsWriterStallControl::new)
                .addWrite(c -> c.updateStalled(true))
                .addWrite(c -> c.updateStalled(false))
                .addWrite(c -> c.waitIfStalled())
                .addReadOnly(c -> c.anyStalledThreads())
                .addReadOnly(c -> c.hasBlocked())
                .addReadOnly(c -> c.getNumWaiting())
                .addReadOnly(c -> c.isHealthy())
                .addReadOnly(c -> c.isThreadQueued(Thread.currentThread()))
                .addReadOnly(c -> c.wasStalled())
                .runTests();
    }
}