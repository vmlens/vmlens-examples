
package com.hazelcast.spi.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DelegatingCompletableFutureTest {

    @Test
    public void testUpdate() throws InterruptedException {
        InternalSerializationService serializationService = (InternalSerializationService) new DefaultSerializationServiceBuilder().build();

        new AtomicTestBuilder<DelegatingCompletableFuture<Object>>(() ->
                new DelegatingCompletableFuture<>(serializationService, new CompletableFuture<>()))
                // Methods that change state and return a value (addUpdate)
                // boolean complete(V value)
                .addUpdate(f -> f.complete("ValueA"))
                .addUpdate(f -> f.complete("ValueB"))
                // boolean completeExceptionally(Throwable ex)
                .addUpdate(f -> f.completeExceptionally(new RuntimeException("Error1")))
                .addUpdate(f -> f.completeExceptionally(new RuntimeException("Error2")))
                // boolean cancel(boolean mayInterruptIfRunning)
                .addUpdate(f -> f.cancel(true))
                .addUpdate(f -> f.cancel(false))

                // Methods that change state and do not return a value (addWrite)
                // void obtrudeValue(V value)
                .addWrite(f -> f.obtrudeValue("ValueC"))
                .addWrite(f -> f.obtrudeValue("ValueD"))
                // void obtrudeException(Throwable ex)
                .addWrite(f -> f.obtrudeException(new RuntimeException("Error3")))
                .addWrite(f -> f.obtrudeException(new RuntimeException("Error4")))

                // Read-only methods (addReadOnly)
                .addReadOnly(f -> f.isDone())
                .addReadOnly(f -> f.isCancelled())
                .addReadOnly(f -> f.isCompletedExceptionally())
                .addReadOnly(f -> f.getNumberOfDependents())
                .addReadOnly(f -> f.getNow("ValueIfAbsent"))
                .addReadOnly(f -> f.getDeserializedValue())
                .addReadOnly(f -> f.getDelegate())
                .addReadOnly(f -> f.toCompletableFuture())
                .addReadOnly(f -> f.toString())
                .addReadOnly(f -> f.joinInternal())
                .addReadOnly(f -> {
                    try {
                        return f.isDone() ? f.get() : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .addReadOnly(f -> {
                    try {
                        return f.isDone() ? f.get(1, TimeUnit.SECONDS) : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .addReadOnly(f -> {
                    try {
                        return f.isDone() ? f.join() : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .runTests();
    }
}
