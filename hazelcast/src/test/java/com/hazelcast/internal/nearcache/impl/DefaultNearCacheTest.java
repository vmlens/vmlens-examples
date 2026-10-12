
package com.hazelcast.internal.nearcache.impl;

import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.serialization.Data;
import com.hazelcast.internal.serialization.SerializationService;
import com.hazelcast.spi.impl.executionservice.TaskScheduler;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultNearCacheTest {

    //@Test
    public void testUpdate() throws InterruptedException {
        NearCacheConfig nearCacheConfig = new NearCacheConfig();
        nearCacheConfig.setSerializeKeys(false);
        SerializationService serializationService = mock(SerializationService.class);
        TaskScheduler scheduler = mock(TaskScheduler.class);
        HazelcastProperties properties = mock(HazelcastProperties.class);

        when(properties.getInteger(Mockito.any())).thenReturn(1);

        Data keyData1 = mock(Data.class);
        Data keyData2 = mock(Data.class);
        Data valueData1 = mock(Data.class);
        Data valueData2 = mock(Data.class);
        DataStructureAdapter adapter1 = mock(DataStructureAdapter.class);
        DataStructureAdapter adapter2 = mock(DataStructureAdapter.class);

        new AtomicTestBuilder<>(() -> {
            DefaultNearCache<String, String> nearCache = new DefaultNearCache<>(
                    "testCache",
                    nearCacheConfig,
                    serializationService,
                    scheduler,
                    DefaultNearCacheTest.class.getClassLoader(),
                    properties);
            nearCache.initialize();
            return nearCache;
        })
                .addWrite(c -> c.initialize())
                .addWrite(c -> c.put("key1", keyData1, "value1", valueData1))
                .addWrite(c -> c.put("key2", keyData2, "value2", valueData2))
                .addWrite(c -> c.invalidate("key1"))
                .addWrite(c -> c.invalidate("key2"))
                .addWrite(c -> c.clear())
                .addWrite(c -> c.destroy())
                .addWrite(c -> c.preload(adapter1))
                .addWrite(c -> c.preload(adapter2))
                .addWrite(c -> c.storeKeys())
                .addUpdate(c -> c.tryReserveForUpdate("key1", keyData1, NearCache.UpdateSemantic.READ_UPDATE))
                .addUpdate(c -> c.tryReserveForUpdate("key2", keyData2, NearCache.UpdateSemantic.WRITE_UPDATE))
                .addUpdate(c -> c.tryPublishReserved("key1", "value1", 1L, true))
                .addUpdate(c -> c.tryPublishReserved("key2", "value2", 2L, false))
                .addReadOnly(c -> c.getName())
                .addReadOnly(c -> c.getNearCacheConfig())
                .addReadOnly(c -> c.get("key1"))
                .addReadOnly(c -> c.getNearCacheStats())
                .addReadOnly(c -> c.isSerializeKeys())
                .addReadOnly(c -> c.size())
                .addReadOnly(c -> c.isPreloadDone())
                .addReadOnly(c -> c.unwrap(DefaultNearCache.class))
                .runTests();
    }
}
