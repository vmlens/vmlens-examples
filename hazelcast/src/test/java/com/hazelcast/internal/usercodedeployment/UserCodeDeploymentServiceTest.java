
package com.hazelcast.internal.usercodedeployment;

import com.hazelcast.config.Config;
import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.NodeEngine;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserCodeDeploymentServiceTest {

    @Test
    public void testUpdate() throws InterruptedException {
        NodeEngine nodeEngine1 = mock(NodeEngine.class);
        NodeEngine nodeEngine2 = mock(NodeEngine.class);
        Config config = mock(Config.class);
        UserCodeDeploymentConfig udcConfig = new UserCodeDeploymentConfig().setEnabled(true);
        ILogger logger = mock(ILogger.class);

        when(nodeEngine1.getConfig()).thenReturn(config);
        when(nodeEngine2.getConfig()).thenReturn(config);
        when(config.getUserCodeDeploymentConfig()).thenReturn(udcConfig);
        when(nodeEngine1.getConfigClassLoader()).thenReturn(UserCodeDeploymentServiceTest.class.getClassLoader());
        when(nodeEngine2.getConfigClassLoader()).thenReturn(UserCodeDeploymentServiceTest.class.getClassLoader());
        when(nodeEngine1.getLogger(any(Class.class))).thenReturn(logger);
        when(nodeEngine2.getLogger(any(Class.class))).thenReturn(logger);

        Properties props1 = new Properties();
        Properties props2 = new Properties();
        props2.setProperty("test.property", "true");

        List<Map.Entry<String, byte[]>> definitions1 = Collections.emptyList();
        List<Map.Entry<String, byte[]>> definitions2 = new ArrayList<>();
        definitions2.add(new AbstractMap.SimpleEntry<>("com.example.GeneratedClass", new byte[]{0}));

        new AtomicTestBuilder<>(() -> {
            UserCodeDeploymentService service = new UserCodeDeploymentService();
            service.init(nodeEngine1, new Properties());
            return service;
        })
        // init: changes state, has arguments
        .addWrite(s -> s.init(nodeEngine1, props1))
        .addWrite(s -> s.init(nodeEngine2, props2))
        // defineClasses: changes state, has arguments
        .addWrite(s -> s.defineClasses(definitions1))
        .addWrite(s -> s.defineClasses(definitions2))
        // handleClassNotFoundException: changes state (populates caches), returns value, has arguments
        .addUpdate(s -> {
            try {
                return s.handleClassNotFoundException("class.one.Name");
            } catch (ClassNotFoundException e) {
                return null;
            }
        })
        .addUpdate(s -> {
            try {
                return s.handleClassNotFoundException("class.two.Name");
            } catch (ClassNotFoundException e) {
                return null;
            }
        })
        // reset: changes state, no arguments
        .addWrite(s -> s.reset())
        // shutdown: changes state, has arguments
        .addWrite(s -> s.shutdown(true))
        .addWrite(s -> s.shutdown(false))
        // Read only methods
        .addReadOnly(s -> s.getClassDataOrNull("some.Class"))
        .addReadOnly(s -> s.findLoadedClass("some.Class"))
        .runTests();
    }
}
