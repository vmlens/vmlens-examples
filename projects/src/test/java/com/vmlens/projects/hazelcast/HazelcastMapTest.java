package com.vmlens.projects.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.vmlens.api.Runner.runParallel;

class HazelcastMapTest {

    private Config config;


    @BeforeEach
    void setUp() {
        config = new Config();
        config.setClusterName("test-cluster");

        // Disable networking to keep it fully local
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);


    }

    @Test
    void mapPut() {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(config);
        IMap<Integer, String> map = hazelcast.getMap("test-map");
        int key = 0;
        try(AllInterleavings allInterleavings =
                    new AllInterleavingsBuilder()
                            .build("hazelcast,put")) {
            while(allInterleavings.hasNext()) {
            runParallel(() -> {
                        map.put(key,"");
            }, () -> {
                map.put(key,"");
                    }
            );
            }
        }
        Hazelcast.shutdownAll();
    }

    @Test
    void mapPutGet() {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(config);
        IMap<Integer, String> map = hazelcast.getMap("test-map");
        int key = 0;
        try(AllInterleavings allInterleavings =
                    new AllInterleavingsBuilder()
                            .build("hazelcast.putGet")) {
        while(allInterleavings.hasNext()) {
            runParallel(() -> {
                        map.put(key,"");
                    }, () -> {
                        map.get(key);
                    }
            );
        }
        Hazelcast.shutdownAll();
    }
    }

}
