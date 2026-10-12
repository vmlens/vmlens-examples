
package org.apache.cassandra.service.paxos.uncommitted;

import java.io.IOException;
import java.nio.file.Files;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.cassandra.io.util.File;
import org.apache.cassandra.service.paxos.Ballot;
import org.junit.jupiter.api.Test;

public class PaxosBallotTrackerTest {

    @Test
    public void testUpdate() throws InterruptedException {
        Ballot b1 = Ballot.atUnixMicrosWithLsb(1000, 0, Ballot.Flag.NONE);
        Ballot b2 = Ballot.atUnixMicrosWithLsb(2000, 0, Ballot.Flag.LOCAL);
        Ballot b3 = Ballot.atUnixMicrosWithLsb(3000, 0, Ballot.Flag.GLOBAL);
        Ballot b4 = Ballot.atUnixMicrosWithLsb(4000, 0, Ballot.Flag.NONE);

        new AtomicTestBuilder<>(() -> {
            try {
                File dir = new File(Files.createTempDirectory("ballot_tracker_test").toFile());
                dir.deleteOnExit();
                return PaxosBallotTracker.load(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })
        .addWrite(tracker -> {
            try {
                tracker.flush();
            } catch (Exception e) {
                // ignore
            }
        })
        .addWrite(PaxosBallotTracker::truncate)
        .addWrite(tracker -> tracker.updateHighBound(b1))
        .addWrite(tracker -> tracker.updateHighBound(b2))
        .addWrite(tracker -> tracker.updateHighBoundUnsafe(b1, b2))
        .addWrite(tracker -> tracker.updateHighBoundUnsafe(b2, b3))
        .addWrite(tracker -> {
            try {
                tracker.updateLowBound(b3);
            } catch (Exception e) {
                // ignore
            }
        })
        .addWrite(tracker -> {
            try {
                tracker.updateLowBound(b4);
            } catch (Exception e) {
                // ignore
            }
        })
        .addReadOnly(PaxosBallotTracker::getDirectory)
        .addReadOnly(PaxosBallotTracker::getHighBound)
        .addReadOnly(PaxosBallotTracker::getLowBound)
        .runTests();
    }
}
