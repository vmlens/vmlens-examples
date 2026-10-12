
package com.hazelcast.internal.util.concurrent;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.hazelcast.internal.util.concurrent.ConcurrentConveyor.concurrentConveyor;

public class ConcurrentConveyorTest {

    //@Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> {
            QueuedPipe<String> q0 = new OneToOneConcurrentArrayQueue<>(128);
            QueuedPipe<String> q1 = new OneToOneConcurrentArrayQueue<>(128);
            return concurrentConveyor("GONE", q0, q1);
        })
        // Methods that change state and do not return a value
        .addWrite(ConcurrentConveyor::drainerArrived)
        .addWrite(ConcurrentConveyor::drainerDone)
        .addWrite(ConcurrentConveyor::backpressureOn)
        .addWrite(ConcurrentConveyor::backpressureOff)
        // Method with argument: drainerFailed
        .addWrite(c -> c.drainerFailed(new RuntimeException("Failure 1")))
        .addWrite(c -> c.drainerFailed(new RuntimeException("Failure 2")))
        // Method with arguments: submit (throws checked exception)
        .addWrite(c -> {
            try {
                c.submit(c.queue(0), "itemA");
            } catch (Exception ignored) {}
        })
        .addWrite(c -> {
            try {
                c.submit(c.queue(1), "itemB");
            } catch (Exception ignored) {}
        })
        // Methods that change state and return a value
        // Method with argument: removeQueue
        .addUpdate(c -> c.removeQueue(0))
        .addUpdate(c -> c.removeQueue(1))
        // Method with arguments: offer(int, E)
        .addUpdate(c -> c.offer(0, "item1"))
        .addUpdate(c -> c.offer(1, "item2"))
        // Method with arguments: offer(Queue, E)
        .addUpdate(c -> {
            try {
                return c.offer(c.queue(0), "item1");
            } catch (Exception e) {
                return false;
            }
        })
        .addUpdate(c -> {
            try {
                return c.offer(c.queue(1), "item2");
            } catch (Exception e) {
                return false;
            }
        })
        // Method with argument: drainTo(Collection)
        .addUpdate(c -> c.drainTo(new ArrayList<>()))
        .addUpdate(c -> c.drainTo(new LinkedList<>()))
        // Method with arguments: drainTo(int, Collection)
        .addUpdate(c -> c.drainTo(0, new ArrayList<>()))
        .addUpdate(c -> c.drainTo(1, new ArrayList<>()))
        // Method with arguments: drain(int, Predicate)
        .addUpdate(c -> c.drain(0, item -> true))
        .addUpdate(c -> c.drain(1, item -> false))
        // Method with arguments: drainTo(Collection, int)
        .addUpdate(c -> c.drainTo(new ArrayList<>(), 1))
        .addUpdate(c -> c.drainTo(new ArrayList<>(), 2))
        // Method with arguments: drainTo(int, Collection, int)
        .addUpdate(c -> c.drainTo(0, new ArrayList<>(), 1))
        .addUpdate(c -> c.drainTo(1, new ArrayList<>(), 2))
        // Read-only methods
        .addReadOnly(ConcurrentConveyor::submitterGoneItem)
        .addReadOnly(ConcurrentConveyor::queueCount)
        .addReadOnly(ConcurrentConveyor::liveQueueCount)
        .addReadOnly(c -> c.queue(0))
        .addReadOnly(ConcurrentConveyor::isDrainerGone)
        .runTests();
    }
}
