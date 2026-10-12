
import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.cassandra.db.ColumnFamilyStore;
import static org.mockito.Mockito.mock;

public class ColumnFamilyStoreTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<ColumnFamilyStore>(() -> mock(ColumnFamilyStore.class))
                .addWrite(cfs -> cfs.setNeverPurgeTombstones(true))
                .addWrite(cfs -> cfs.setNeverPurgeTombstones(false))
                .addWrite(cfs -> cfs.compactionDiskSpaceCheck(true))
                .addWrite(cfs -> cfs.compactionDiskSpaceCheck(false))
                .addWrite(ColumnFamilyStore::disableAutoCompaction)
                .addWrite(ColumnFamilyStore::enableAutoCompaction)
                .addReadOnly(ColumnFamilyStore::getNeverPurgeTombstones)
                .addReadOnly(ColumnFamilyStore::isCompactionDiskSpaceCheckEnabled)
                .addReadOnly(ColumnFamilyStore::isAutoCompactionDisabled)
                .addReadOnly(ColumnFamilyStore::getTableName)
                .addReadOnly(ColumnFamilyStore::getKeyspaceName)
                .addReadOnly(ColumnFamilyStore::getKeyspaceTableName)
                .addReadOnly(ColumnFamilyStore::getCrcCheckChance)
                .addReadOnly(ColumnFamilyStore::getMinimumCompactionThreshold)
                .addReadOnly(ColumnFamilyStore::getMaximumCompactionThreshold)
                .runTests();
    }
}
