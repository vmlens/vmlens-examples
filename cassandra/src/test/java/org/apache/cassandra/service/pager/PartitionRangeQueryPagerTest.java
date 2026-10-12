
package org.apache.cassandra.service.pager;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.db.ReadExecutionController;
import org.apache.cassandra.db.rows.Row;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PartitionRangeQueryPagerTest {

    @Test
    public void testUpdate() throws InterruptedException {
        DecoratedKey key1 = Mockito.mock(DecoratedKey.class);
        DecoratedKey key2 = Mockito.mock(DecoratedKey.class);
        Row row1 = Mockito.mock(Row.class);
        Row row2 = Mockito.mock(Row.class);
        ReadExecutionController controller1 = Mockito.mock(ReadExecutionController.class);
        ReadExecutionController controller2 = Mockito.mock(ReadExecutionController.class);

        new AtomicTestBuilder<PartitionRangeQueryPager>(() -> Mockito.mock(PartitionRangeQueryPager.class, Mockito.CALLS_REAL_METHODS))
                .addWrite(pager -> pager.recordLast(key1, row1))
                .addWrite(pager -> pager.recordLast(key2, row2))
                .addUpdate(pager -> pager.fetchPageInternal(10, controller1))
                .addUpdate(pager -> pager.fetchPageInternal(20, controller2))
                .addReadOnly(PartitionRangeQueryPager::state)
                .addReadOnly(PartitionRangeQueryPager::isTopK)
                .addReadOnly(PartitionRangeQueryPager::isExhausted)
                .addReadOnly(PartitionRangeQueryPager::maxRemaining)
                .addReadOnly(PartitionRangeQueryPager::remainingInPartition)
                .addReadOnly(pager -> pager.isPreviouslyReturnedPartition(key1))
                .runTests();
    }
}
