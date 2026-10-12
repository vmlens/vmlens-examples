
package org.apache.cassandra.net;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.vmlens.api.testbuilder.AtomicTestBuilder;

import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.embedded.EmbeddedChannel;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AsyncMessageOutputPlusTest {

    @Test
    public void testAsyncMessageOutputPlus() throws InterruptedException {
        Supplier<AsyncMessageOutputPlus> supplier = () -> {
            EmbeddedChannel channel = new EmbeddedChannel();
            channel.config().setWriteBufferWaterMark(new WriteBufferWaterMark(32, 64));
            FrameEncoder.PayloadAllocator payloadAllocator = mock(FrameEncoder.PayloadAllocator.class);
            when(payloadAllocator.allocate(anyBoolean(), anyInt())).thenAnswer(invocation -> {
                int size = invocation.getArgument(1);
                FrameEncoder.Payload payload = mock(FrameEncoder.Payload.class);
                try {
                    Field bufferField = FrameEncoder.Payload.class.getDeclaredField("buffer");
                    bufferField.setAccessible(true);
                    bufferField.set(payload, ByteBuffer.allocate(size));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return payload;
            });
            return new AsyncMessageOutputPlus(channel, 1024, 2048, payloadAllocator);
        };

        new AtomicTestBuilder<>(supplier)
                .addWrite(output -> {
                    try {
                        output.write(1);
                    } catch (Throwable ignored) {
                    }
                })
                .addWrite(output -> {
                    try {
                        output.write(2);
                    } catch (Throwable ignored) {
                    }
                })
                .addWrite(output -> {
                    try {
                        output.close();
                    } catch (Throwable ignored) {
                    }
                })
                .addWrite(output -> {
                    try {
                        output.discard();
                    } catch (Throwable ignored) {
                    }
                })
                .addReadOnly(AsyncMessageOutputPlus::position)
                .runTests();
    }
}
