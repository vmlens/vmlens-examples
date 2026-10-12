
package com.hazelcast.jet.impl;

import com.hazelcast.jet.impl.execution.init.ExecutionPlan;
import com.hazelcast.logging.ILogger;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MasterSnapshotContextTest {

    //@Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> {
            MasterContext mc = mock(MasterContext.class);
            ILogger logger = mock(ILogger.class);
            JobExecutionRecord jer = mock(JobExecutionRecord.class);
            ExecutionPlan plan = mock(ExecutionPlan.class);

            when(mc.jobExecutionRecord()).thenReturn(jer);
            when(mc.getAnyExecutionPlan()).thenReturn(plan);
            when(mc.jobIdString()).thenReturn("test-job");

            return new MasterSnapshotContext(mc, logger);
        })
                // void enqueueSnapshot(String snapshotName, boolean isTerminal, CompletableFuture<Void> future)
                .addWrite(c -> c.enqueueSnapshot("snapshot-A", true, new CompletableFuture<>()))
                .addWrite(c -> c.enqueueSnapshot("snapshot-B", false, null))

                // CompletableFuture<Boolean> requestInitialSnapshotIfNeeded()
                .addUpdate(MasterSnapshotContext::requestInitialSnapshotIfNeeded)

                // void startScheduledSnapshot(long executionId)
                .addWrite(c -> c.startScheduledSnapshot(1001L))
                .addWrite(c -> c.startScheduledSnapshot(2002L))

                // void tryBeginSnapshot()
                .addWrite(MasterSnapshotContext::tryBeginSnapshot)

                // void tryBeginSnapshot(boolean allowStartingState)
                .addWrite(c -> c.tryBeginSnapshot(true))
                .addWrite(c -> c.tryBeginSnapshot(false))

                // void onExecutionStarted()
                .addWrite(MasterSnapshotContext::onExecutionStarted)

                // void onExecutionTerminated()
                .addWrite(MasterSnapshotContext::onExecutionTerminated)

                // CompletableFuture<Void> terminalSnapshotFuture()
                .addReadOnly(MasterSnapshotContext::terminalSnapshotFuture)

                // ILogger logger()
                .addReadOnly(MasterSnapshotContext::logger)

                .runTests();
    }
}
