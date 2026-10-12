
package org.apache.cassandra.service;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class BatchlogResponseHandlerTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> new BatchlogResponseHandler.BatchlogCleanup(100, () -> {}))
                .addUpdate(BatchlogResponseHandler.BatchlogCleanup::decrement)
                .addWrite(BatchlogResponseHandler.BatchlogCleanup::ackMutation)
                .addWrite(cleanup -> cleanup.setMutationsWaitingFor(10))
                .addWrite(cleanup -> cleanup.setMutationsWaitingFor(20))
                .addReadOnly(cleanup -> cleanup.mutationsWaitingFor)
                .runTests();
    }
}
