
package org.apache.cassandra.service;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class NativeTransportServiceTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(NativeTransportService::new)
            .addWrite(NativeTransportService::initialize)
            .addWrite(NativeTransportService::start)
            .addWrite(NativeTransportService::stop)
            .addWrite(service -> service.stop(true))
            .addWrite(service -> service.stop(false))
            .addWrite(NativeTransportService::destroy)
            .addWrite(NativeTransportService::clearConnectionHistory)
            .addWrite(service -> service.disconnect(user -> true))
            .addWrite(service -> service.disconnect(user -> false))
            .addReadOnly(NativeTransportService::isRunning)
            .addReadOnly(NativeTransportService::getWorkerGroup)
            .addReadOnly(NativeTransportService::getServer)
            .runTests();
    }
}
