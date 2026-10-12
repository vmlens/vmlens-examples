
package org.apache.cassandra.streaming.async;

import java.io.IOException;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import org.apache.cassandra.streaming.StreamingChannel;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

public class NettyStreamingChannelTest {

    @Test
    public void testUpdate() throws InterruptedException {
        ChannelHandlerContext ctx1 = mock(ChannelHandlerContext.class);
        ChannelHandlerContext ctx2 = mock(ChannelHandlerContext.class);

        new AtomicTestBuilder<>(() -> new NettyStreamingChannel(new EmbeddedChannel(), StreamingChannel.Kind.CONTROL))
                .addReadOnly(NettyStreamingChannel::id)
                .addReadOnly(NettyStreamingChannel::description)
                .addReadOnly(NettyStreamingChannel::peer)
                .addReadOnly(NettyStreamingChannel::connectedTo)
                .addReadOnly(NettyStreamingChannel::connected)
                .addReadOnly(NettyStreamingChannel::in)
                .addUpdate(NettyStreamingChannel::acquireOut)
                .addUpdate(ch -> ch.send(factory -> factory.apply(10)))
                .addUpdate(ch -> ch.send(factory -> factory.apply(20)))
                .addUpdate(NettyStreamingChannel::close)
                .addWrite(ch -> ch.onClose(() -> {}))
                .addWrite(ch -> ch.onClose(() -> System.out.println("closed")))
                .addWrite(ch -> ch.channelRead(ctx1, Unpooled.buffer(10)))
                .addWrite(ch -> ch.channelRead(ctx2, Unpooled.buffer(20)))
                .addWrite(ch -> ch.channelInactive(ctx1))
                .addWrite(ch -> ch.channelInactive(ctx2))
                .addWrite(ch -> ch.exceptionCaught(ctx1, new IOException("error1")))
                .addWrite(ch -> ch.exceptionCaught(ctx2, new RuntimeException("error2")))
                .runTests();
    }
}
