package org.apache.pulsar.common.util.collections;

import com.vmlens.api.atomic.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class GrowableArrayBlockingQueueTest {

    @Test
    public void testAtomic() {
        new AtomicTestBuilder<>(
                () ->
                {
                    GrowableArrayBlockingQueue queue =   new GrowableArrayBlockingQueue();
                    return queue;
                })
                .addReadOnly(GrowableArrayBlockingQueue::peek)
                .addWrite((queue) -> {  queue.put("test");  })
                .runTests();
    }

}
