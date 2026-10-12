
package org.apache.cassandra.tools;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import javax.management.remote.JMXConnector;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.cassandra.service.StorageServiceMBean;
import org.apache.cassandra.utils.progress.ProgressEvent;
import org.apache.cassandra.utils.progress.ProgressEventType;

public class RepairRunnerTest {

    //@Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> {
            StorageServiceMBean ssProxy = mock(StorageServiceMBean.class);
            JMXConnector jmxc = mock(JMXConnector.class);
            when(ssProxy.getParentRepairStatus(1)).thenReturn(Collections.singletonList("COMPLETED"));

            RepairRunner.RepairCmd repairCmd = new RepairRunner.RepairCmd("keyspace1") {
                @Override
                public Integer start() {
                    return 1;
                }
            };

            PrintStream out = new PrintStream(new ByteArrayOutputStream());
            return new RepairRunner(out, jmxc, ssProxy, repairCmd);
        })
        .addWrite(runner -> {
            try {
                runner.start();
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.close();
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.run();
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.run(true);
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.run(false);
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.handleNotificationLost(1000L, "lost1");
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.handleNotificationLost(2000L, "lost2");
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.handleConnectionClosed(1000L, "closed1");
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.handleConnectionClosed(2000L, "closed2");
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.handleConnectionFailed(1000L, "failed1");
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.handleConnectionFailed(2000L, "failed2");
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.progress("tag1", new ProgressEvent(ProgressEventType.PROGRESS, 1, 100, "msg1"));
            } catch (Throwable ignored) {}
        })
        .addWrite(runner -> {
            try {
                runner.progress("tag2", new ProgressEvent(ProgressEventType.COMPLETE, 100, 100, "msg2"));
            } catch (Throwable ignored) {}
        })
        .addReadOnly(runner -> runner.isInterestedIn("repair:1"))
        .runTests();
    }
}
