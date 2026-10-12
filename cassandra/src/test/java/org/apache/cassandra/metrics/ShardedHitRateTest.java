
package org.apache.cassandra.metrics;

import java.util.concurrent.locks.ReentrantLock;
import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;

public class ShardedHitRateTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(ShardedHitRate::new)
                .addUpdate(s -> s.newShard(new ReentrantLock()))
                .addUpdate(s -> s.newShard(new ReentrantLock()))
                .addWrite(ShardedHitRate::refresh)
                .addReadOnly(ShardedHitRate::totalHits)
                .addReadOnly(ShardedHitRate::totalMisses)
                .addReadOnly(ShardedHitRate::totalRequests)
                .addReadOnly(ShardedHitRate::hitRateAllTime)
                .addReadOnly(s -> s.hitRate(1))
                .addReadOnly(s -> s.requestsPerSecond(1))
                .runTests();
    }
}
