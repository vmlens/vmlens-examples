package projects.cassandra;

import com.datastax.oss.driver.api.core.config.DriverConfig;
import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.internal.core.channel.DefaultWriteCoalescer;
import com.vmlens.api.atomic.AtomicTestBuilder;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultWriteCoalescerTest {

    @Test
    public void testAtomic() {
        new AtomicTestBuilder<>(DefaultWriteCoalescerTest::create)
                .addWrite(DefaultWriteCoalescerTest::writeAndFlush)
                .runTests();

    }

    public static DefaultWriteCoalescer create() {
        DriverContext context = mock(DriverContext.class);
        DriverConfig driverConfig = mock(DriverConfig.class);
        DriverExecutionProfile config = mock(DriverExecutionProfile.class);

        when(context.getConfig()).thenReturn(driverConfig);
        when(driverConfig.getDefaultProfile()).thenReturn(config);
        when(config.getDuration(any())).thenReturn(Duration.ofMillis(100));
        return new DefaultWriteCoalescer(context);
    }

    public static void writeAndFlush(DefaultWriteCoalescer defaultWriteCoalescer) {
        Channel channel = mock(Channel.class);
        EventLoop eventLoop = mock(EventLoop.class);

        when(channel.eventLoop()).thenReturn(eventLoop);


        defaultWriteCoalescer.writeAndFlush(channel, "test");
    }


}
