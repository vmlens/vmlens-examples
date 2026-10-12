
package org.apache.cassandra.diag;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.vmlens.api.testbuilder.AtomicTestBuilder;

public class DiagnosticEventServiceTest
{
    private enum TestType
    {
        TYPE1, TYPE2
    }

    private static class TestEvent1 extends DiagnosticEvent
    {
        private final TestType type;

        TestEvent1(TestType type)
        {
            this.type = type;
        }

        public Enum<?> getType()
        {
            return type;
        }

        public Map<String, Serializable> toMap()
        {
            return Collections.emptyMap();
        }
    }

    private static class TestEvent2 extends DiagnosticEvent
    {
        private final TestType type;

        TestEvent2(TestType type)
        {
            this.type = type;
        }

        public Enum<?> getType()
        {
            return type;
        }

        public Map<String, Serializable> toMap()
        {
            return Collections.emptyMap();
        }
    }

    @Test
    public void testUpdate() throws InterruptedException
    {
        Consumer<TestEvent1> consumer1 = e -> {};
        Consumer<TestEvent1> consumer2 = e -> {};
        Consumer<DiagnosticEvent> allConsumer1 = e -> {};
        Consumer<DiagnosticEvent> allConsumer2 = e -> {};

        new AtomicTestBuilder<>(() -> {
            DiagnosticEventService service = DiagnosticEventService.instance();
            service.cleanup();
            return service;
        })
        .addWrite(s -> s.publish(new TestEvent1(TestType.TYPE1)))
        .addWrite(s -> s.publish(new TestEvent2(TestType.TYPE2)))
        .addWrite(s -> s.subscribe(TestEvent1.class, consumer1))
        .addWrite(s -> s.subscribe(TestEvent1.class, consumer2))
        .addWrite(s -> s.subscribe(TestEvent1.class, TestType.TYPE1, consumer1))
        .addWrite(s -> s.subscribe(TestEvent1.class, TestType.TYPE2, consumer2))
        .addWrite(s -> s.subscribeAll(allConsumer1))
        .addWrite(s -> s.subscribeAll(allConsumer2))
        .addWrite(s -> s.unsubscribe(consumer1))
        .addWrite(s -> s.unsubscribe(consumer2))
        .addWrite(s -> s.unsubscribe(TestEvent1.class, consumer1))
        .addWrite(DiagnosticEventService::cleanup)
        .addWrite(DiagnosticEventService::disableDiagnostics)
        .addWrite(s -> s.enableEventPersistence("EventClass1"))
        .addWrite(s -> s.enableEventPersistence("EventClass2"))
        .addWrite(s -> s.disableEventPersistence("EventClass1"))
        .addWrite(s -> s.disableEventPersistence("EventClass2"))
        .addReadOnly(s -> s.hasSubscribers(TestEvent1.class))
        .addReadOnly(s -> s.hasSubscribers(TestEvent1.class, TestType.TYPE1))
        .addReadOnly(s -> s.isEnabled(TestEvent1.class))
        .addReadOnly(s -> s.isEnabled(TestEvent1.class, TestType.TYPE1))
        .addReadOnly(DiagnosticEventService::isDiagnosticsEnabled)
        .addReadOnly(s -> s.readEvents("EventClass1", 0L, 10))
        .runTests();
    }
}
