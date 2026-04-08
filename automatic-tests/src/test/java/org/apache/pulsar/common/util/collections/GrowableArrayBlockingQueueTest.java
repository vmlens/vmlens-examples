package org.apache.pulsar.common.util.collections;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class GrowableArrayBlockingQueueTest {

    @Test
    public void testAtomic() {
        new AtomicTestBuilder<>(
                 GrowableArrayBlockingQueue::new)
                .addReadOnly(GrowableArrayBlockingQueue::peek)
                .addWrite((queue) -> {  queue.put("test");  })
                .runTests();
    }

}
