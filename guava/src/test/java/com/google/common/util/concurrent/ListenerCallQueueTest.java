package com.google.common.util.concurrent;

import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;

public class ListenerCallQueueTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<ListenerCallQueue<Object>>(() -> new ListenerCallQueue<Object>())
            // addListener(L listener, Executor executor) - changes state, returns void, has arguments
            .addWrite(q -> q.addListener(new Object(), command -> command.run()))
            .addWrite(q -> q.addListener(new Object(), command -> { /* distinct executor */ }))
            
            // enqueue(Event<L> event) - changes state, returns void, has arguments
            .addWrite(q -> q.enqueue(l -> {}))
            .addWrite(q -> q.enqueue(l -> { int i = 1; }))
            
            // enqueue(Event<L> event, String label) - changes state, returns void, has arguments
            .addWrite(q -> q.enqueue(l -> {}, "label1"))
            .addWrite(q -> q.enqueue(l -> {}, "label2"))
            
            // dispatch() - changes state (internal queue processing), returns void, no arguments
            .addWrite(q -> q.dispatch())
            
            .runTests();
    }
}