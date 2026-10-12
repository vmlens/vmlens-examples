
package org.apache.cassandra.service.paxos;

import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;

public class PaxosCommitTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<PaxosCommit>(() -> null)
                .addWrite(paxosCommit -> PaxosCommit.setEnableDcLocalCommit(true))
                .addWrite(paxosCommit -> PaxosCommit.setEnableDcLocalCommit(false))
                .addReadOnly(paxosCommit -> PaxosCommit.getEnableDcLocalCommit())
                .runTests();
    }
}
