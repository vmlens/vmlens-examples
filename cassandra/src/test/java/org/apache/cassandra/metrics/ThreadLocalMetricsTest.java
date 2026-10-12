
package org.apache.cassandra.metrics;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class ThreadLocalMetricsTest {

    static class Counter {
        private final int metricId = ThreadLocalMetrics.allocateMetricId();

        public void add(long n) {
            ThreadLocalMetrics.add(metricId, n);
        }

        public void release() {
            ThreadLocalMetrics.get().release();
        }

        public long getCountAndReset() {
            return ThreadLocalMetrics.getCountAndReset(metricId);
        }

        public long getCountAndReset(int dummy) {
            return ThreadLocalMetrics.getCountAndReset(metricId);
        }

        public long getCount() {
            return ThreadLocalMetrics.getCount(metricId);
        }
    }

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(Counter::new)
                .addWrite(c -> c.add(1L))
                .addWrite(c -> c.add(2L))
                .addWrite(Counter::release)
                .addUpdate(Counter::getCountAndReset)
                .addUpdate(c -> c.getCountAndReset(1))
                .addUpdate(c -> c.getCountAndReset(2))
                .addReadOnly(Counter::getCount)
                .runTests();
    }
}
