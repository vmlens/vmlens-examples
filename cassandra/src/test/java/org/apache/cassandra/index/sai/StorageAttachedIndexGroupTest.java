package org.apache.cassandra.index.sai;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.cassandra.db.ColumnFamilyStore;
import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.db.RegularAndStaticColumns;
import org.apache.cassandra.db.WriteContext;
import org.apache.cassandra.db.filter.RowFilter;
import org.apache.cassandra.db.lifecycle.ILifecycleTransaction;
import org.apache.cassandra.db.memtable.Memtable;
import org.apache.cassandra.index.transactions.IndexTransaction;
import org.apache.cassandra.io.sstable.Descriptor;
import org.apache.cassandra.io.sstable.format.SSTableReader;
import org.apache.cassandra.notifications.INotification;
import org.apache.cassandra.schema.TableMetadata;

public class StorageAttachedIndexGroupTest
{
    @Test
    public void testUpdate() throws InterruptedException
    {
        ColumnFamilyStore cfs = mock(ColumnFamilyStore.class, Mockito.RETURNS_DEEP_STUBS);

        StorageAttachedIndex index1 = mock(StorageAttachedIndex.class);
        StorageAttachedIndex index2 = mock(StorageAttachedIndex.class);

        INotification notification1 = mock(INotification.class);
        INotification notification2 = mock(INotification.class);
        Object sender1 = new Object();
        Object sender2 = new Object();

        Collection<SSTableReader> sstables1 = Collections.emptyList();
        Collection<SSTableReader> sstables2 = Collections.singletonList(mock(SSTableReader.class));

        Collection<SSTableReader> removed1 = Collections.emptyList();
        Collection<SSTableReader> removed2 = Collections.singletonList(mock(SSTableReader.class));

        Iterable<SSTableReader> added1 = Collections.emptyList();
        Iterable<SSTableReader> added2 = Collections.singletonList(mock(SSTableReader.class));

        Set<StorageAttachedIndex> indexesSet1 = Collections.emptySet();
        Set<StorageAttachedIndex> indexesSet2 = Collections.singleton(index1);

        IndexValidation validation1 = IndexValidation.NONE;
        IndexValidation validation2 = IndexValidation.CHECKSUM;

        DecoratedKey key = mock(DecoratedKey.class);
        RegularAndStaticColumns columns = mock(RegularAndStaticColumns.class);
        WriteContext ctx = mock(WriteContext.class);
        Memtable memtable = mock(Memtable.class);
        RowFilter rowFilter = mock(RowFilter.class);
        Descriptor descriptor = mock(Descriptor.class);
        ILifecycleTransaction txn = mock(ILifecycleTransaction.class);
        TableMetadata metadata = mock(TableMetadata.class);

        new AtomicTestBuilder<>(() -> new StorageAttachedIndexGroup(cfs))
            // State-changing methods returning a value
            .addUpdate(group -> group.onSSTableChanged(removed1, added1, indexesSet1, validation1))
            .addUpdate(group -> group.onSSTableChanged(removed2, added2, indexesSet2, validation2))

            // State-changing methods with arguments and void return
            .addWrite(group -> group.addIndex(index1))
            .addWrite(group -> group.addIndex(index2))
            .addWrite(group -> group.removeIndex(index1))
            .addWrite(group -> group.removeIndex(index2))
            .addWrite(group -> group.handleNotification(notification1, sender1))
            .addWrite(group -> group.handleNotification(notification2, sender2))
            .addWrite(group -> group.deletePerSSTableFiles(sstables1))
            .addWrite(group -> group.deletePerSSTableFiles(sstables2))
            .addWrite(group -> group.dropIndexSSTables(sstables1, index1))
            .addWrite(group -> group.dropIndexSSTables(sstables2, index2))

            // State-changing methods without arguments and void return
            .addWrite(StorageAttachedIndexGroup::invalidate)
            .addWrite(StorageAttachedIndexGroup::unsafeReload)
            .addWrite(StorageAttachedIndexGroup::reset)

            // Read-only methods
            .addReadOnly(StorageAttachedIndexGroup::getIndexes)
            .addReadOnly(group -> group.containsIndex(index1))
            .addReadOnly(StorageAttachedIndexGroup::isSingleton)
            .addReadOnly(group -> group.indexerFor(i -> true, key, columns, 0L, ctx, IndexTransaction.Type.UPDATE, memtable))
            .addReadOnly(group -> group.queryPlanFor(rowFilter))
            .addReadOnly(group -> group.getFlushObserver(descriptor, txn, metadata))
            .addReadOnly(group -> group.handles(IndexTransaction.Type.UPDATE))
            .addReadOnly(StorageAttachedIndexGroup::getComponents)
            .addReadOnly(group -> group.validateSSTableAttachedIndexes(sstables1, false, false))
            .addReadOnly(StorageAttachedIndexGroup::supportsL0Shards)
            .addReadOnly(StorageAttachedIndexGroup::openIndexFiles)
            .addReadOnly(StorageAttachedIndexGroup::diskUsage)
            .addReadOnly(StorageAttachedIndexGroup::totalIndexBuildsInProgress)
            .addReadOnly(StorageAttachedIndexGroup::totalQueryableIndexCount)
            .addReadOnly(StorageAttachedIndexGroup::totalIndexCount)
            .addReadOnly(StorageAttachedIndexGroup::totalDiskUsage)
            .addReadOnly(StorageAttachedIndexGroup::metadata)
            .addReadOnly(StorageAttachedIndexGroup::table)
            .addReadOnly(StorageAttachedIndexGroup::sstableContextManager)
            .runTests();
    }
}