
package org.apache.cassandra.gms;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.cassandra.locator.InetAddressAndPort;

public class NewGossiperTest {

    @Test
    public void testUpdate() throws InterruptedException {
        Map<InetAddressAndPort, EndpointState> map1 = Collections.emptyMap();
        Map<InetAddressAndPort, EndpointState> map2 = new HashMap<>();

        new AtomicTestBuilder<>(NewGossiper::new)
                .addUpdate(gossiper -> {
                    try {
                        return gossiper.doShadowRound();
                    } catch (Throwable t) {
                        return null;
                    }
                })
                .addWrite(gossiper -> gossiper.onAck(map1))
                .addWrite(gossiper -> gossiper.onAck(map2))
                .addReadOnly(NewGossiper::isInShadowRound)
                .runTests();
    }
}
