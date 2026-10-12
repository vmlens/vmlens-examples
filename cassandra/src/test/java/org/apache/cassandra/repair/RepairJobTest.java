
package org.apache.cassandra.repair;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import static org.mockito.Mockito.mock;

public class RepairJobTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<RepairJob>(() -> mock(RepairJob.class))
            .addWrite(RepairJob::run)
            .addWrite(RepairJob::runRepair)
            .addWrite(job -> job.abort(new RuntimeException("Reason 1")))
            .addWrite(job -> job.abort(new RuntimeException("Reason 2")))
            .addUpdate(job -> job.executeTasks(Collections.emptyList()))
            .addUpdate(job -> job.executeTasks(Collections.singletonList(mock(SyncTask.class))))
            .addReadOnly(RepairJob::getNowInSeconds)
            .runTests();
    }
}
