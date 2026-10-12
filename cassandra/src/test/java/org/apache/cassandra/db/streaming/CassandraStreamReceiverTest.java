
package org.apache.cassandra.db.streaming;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.cassandra.db.ColumnFamilyStore;
import org.apache.cassandra.streaming.IncomingStream;
import org.apache.cassandra.streaming.StreamSession;

public class CassandraStreamReceiverTest {

    @Test
    public void testUpdate() throws InterruptedException {
        ColumnFamilyStore cfs = Mockito.mock(ColumnFamilyStore.class);
        StreamSession session = Mockito.mock(StreamSession.class);

        IncomingStream stream1 = Mockito.mock(CassandraIncomingFile.class);
        IncomingStream stream2 = Mockito.mock(CassandraIncomingFile.class);
        ColumnFamilyStore cfsParam = Mockito.mock(ColumnFamilyStore.class);

        new AtomicTestBuilder<>(() -> new CassandraStreamReceiver(cfs, session, Collections.emptyList(), 1))
            .addWrite(receiver -> receiver.received(stream1))
            .addWrite(receiver -> receiver.received(stream2))
            .addWrite(receiver -> receiver.discardStream(stream1))
            .addWrite(receiver -> receiver.discardStream(stream2))
            .addWrite(CassandraStreamReceiver::abort)
            .addWrite(CassandraStreamReceiver::finishTransaction)
            .addWrite(CassandraStreamReceiver::finished)
            .addWrite(CassandraStreamReceiver::cleanup)
            .addReadOnly(receiver -> receiver.requiresWritePath(cfsParam))
            .runTests();
    }
}
