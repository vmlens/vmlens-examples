
package org.apache.cassandra.service.snapshot;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.vmlens.api.testbuilder.AtomicTestBuilder;

public class SnapshotManagerTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> new SnapshotManager(60, 60, new String[0]))
            // State-changing methods returning a value (addUpdate)
            .addUpdate(sm -> sm.start(true))
            .addUpdate(sm -> sm.start(false))
            .addUpdate(sm -> sm.takeSnapshot(SnapshotOptions.userSnapshot("tag1", Map.of(), "ks1")))
            .addUpdate(sm -> sm.takeSnapshot(SnapshotOptions.userSnapshot("tag2", Map.of(), "ks2")))

            // State-changing methods returning void without arguments (addWrite)
            .addWrite(SnapshotManager::close)
            .addWrite(SnapshotManager::restart)
            .addWrite(SnapshotManager::resumeSnapshotCleanup)
            .addWrite(SnapshotManager::clearAllSnapshots)
            .addWrite(SnapshotManager::clearEphemeralSnapshots)
            .addWrite(SnapshotManager::clearExpiredSnapshots)
            .addWrite(SnapshotManager::registerMBean)
            .addWrite(SnapshotManager::unregisterMBean)

            // State-changing methods returning void with arguments (addWrite x2)
            .addWrite(sm -> sm.shutdownAndWait(1, TimeUnit.SECONDS))
            .addWrite(sm -> sm.shutdownAndWait(2, TimeUnit.SECONDS))
            .addWrite(sm -> sm.restart(true))
            .addWrite(sm -> sm.restart(false))
            .addWrite(sm -> sm.clearSnapshots("tag1", "ks1"))
            .addWrite(sm -> sm.clearSnapshots("tag2", "ks2"))
            .addWrite(sm -> sm.clearSnapshot("ks1", "tbl1", "tag1"))
            .addWrite(sm -> sm.clearSnapshot("ks2", "tbl2", "tag2"))
            .addWrite(sm -> sm.clearAllSnapshots("ks1", "tbl1"))
            .addWrite(sm -> sm.clearAllSnapshots("ks2", "tbl2"))
            .addWrite(sm -> sm.clearSnapshot(s -> true))
            .addWrite(sm -> sm.clearSnapshot(s -> false))
            .addWrite(sm -> sm.takeSnapshot("tag1", "ks1"))
            .addWrite(sm -> sm.takeSnapshot("tag2", "ks2"))
            .addWrite(sm -> {
                try {
                    sm.takeSnapshot("tag1", Map.of(), "ks1");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .addWrite(sm -> {
                try {
                    sm.takeSnapshot("tag2", Map.of(), "ks2");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .addWrite(sm -> sm.clearSnapshot("tag1", Map.of(), "ks1"))
            .addWrite(sm -> sm.clearSnapshot("tag2", Map.of(), "ks2"))
            .addWrite(sm -> sm.setSnapshotLinksPerSecond(100L))
            .addWrite(sm -> sm.setSnapshotLinksPerSecond(200L))
            .addWrite(sm -> sm.handleNotification(null, "sender1"))
            .addWrite(sm -> sm.handleNotification(null, "sender2"))

            // Read-only methods (addReadOnly)
            .addReadOnly(SnapshotManager::getSnapshots)
            .addReadOnly(sm -> sm.getSnapshots("ks1"))
            .addReadOnly(sm -> sm.getSnapshots(true, false))
            .addReadOnly(sm -> sm.getSnapshots(s -> true))
            .addReadOnly(sm -> sm.getSnapshot("ks1", "tbl1", "tag1"))
            .addReadOnly(sm -> sm.exists("ks1", "tbl1", "tag1"))
            .addReadOnly(sm -> sm.exists(s -> true))
            .addReadOnly(sm -> sm.listSnapshots(Map.of()))
            .addReadOnly(SnapshotManager::getTrueSnapshotSize)
            .addReadOnly(sm -> sm.getTrueSnapshotsSize("ks1"))
            .addReadOnly(sm -> sm.getTrueSnapshotsSize("ks1", "tbl1"))
            .addReadOnly(sm -> sm.getTrueSnapshotsSize("ks1", "tbl1", "tag1"))
            .addReadOnly(SnapshotManager::getSnapshotLinksPerSecond)
            .runTests();
    }
}
