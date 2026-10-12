
package org.apache.cassandra.db.compaction;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.cassandra.db.ColumnFamilyStore;
import org.apache.cassandra.db.compaction.unified.Controller;
import org.apache.cassandra.io.sstable.format.SSTableReader;
import org.apache.cassandra.schema.TableMetadata;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnifiedCompactionStrategyTest {

    @Test
    public void testUpdate() throws InterruptedException {
        SSTableReader sstable1 = mock(SSTableReader.class);
        SSTableReader sstable2 = mock(SSTableReader.class);

        new AtomicTestBuilder<>(() -> {
            ColumnFamilyStore cfs = mock(ColumnFamilyStore.class);
            Controller controller = mock(Controller.class);
            TableMetadata metadata = mock(TableMetadata.class);
            when(cfs.metadata()).thenReturn(metadata);
            return new UnifiedCompactionStrategy(cfs, Collections.emptyMap(), controller);
        })
        .addWrite(strategy -> strategy.addSSTable(sstable1))
        .addWrite(strategy -> strategy.addSSTable(sstable2))
        .addWrite(strategy -> strategy.removeSSTable(sstable1))
        .addWrite(strategy -> strategy.removeSSTable(sstable2))
        .addReadOnly(UnifiedCompactionStrategy::getEstimatedRemainingTasks)
        .addReadOnly(UnifiedCompactionStrategy::getMaxSSTableBytes)
        .addReadOnly(UnifiedCompactionStrategy::getController)
        .addReadOnly(UnifiedCompactionStrategy::getMetadata)
        .addReadOnly(UnifiedCompactionStrategy::toString)
        .runTests();
    }
}
