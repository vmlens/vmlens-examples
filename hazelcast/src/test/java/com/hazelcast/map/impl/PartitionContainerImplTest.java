
package com.hazelcast.map.impl;

import com.hazelcast.config.MapConfig;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.spi.impl.NodeEngine;
import com.hazelcast.config.Config;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PartitionContainerImplTest {

    //@Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> {
            MapService mapService = mock(MapService.class);
            MapServiceContext mapServiceContext = mock(MapServiceContext.class);
            NodeEngine nodeEngine = mock(NodeEngine.class);
            Config config = mock(Config.class);

            when(mapService.getMapServiceContext()).thenReturn(mapServiceContext);
            mapService.mapServiceContext = mapServiceContext;
            when(mapServiceContext.getNodeEngine()).thenReturn(nodeEngine);
            when(nodeEngine.getConfig()).thenReturn(config);
            when(config.getMapConfigs()).thenReturn(new HashMap<>());

            RecordStore recordStore = mock(RecordStore.class);
            MapContainer mapContainer = mock(MapContainer.class);
            when(mapContainer.getMapConfig()).thenReturn(new MapConfig());
            when(mapServiceContext.getMapContainer(anyString())).thenReturn(mapContainer);
            when(mapServiceContext.createRecordStore(any(), anyInt(), any())).thenReturn(recordStore);
            when(mapServiceContext.getMapContainers()).thenReturn(Collections.emptyMap());

            return new PartitionContainerImpl(mapService, 1);
        })
                .addReadOnly(PartitionContainerImpl::getMaps)
                .addReadOnly(PartitionContainerImpl::getAllRecordStores)
                .addReadOnly(p -> p.getAllNamespaces(1))
                .addReadOnly(p -> p.getNamespaces(cfg -> true, 1))
                .addReadOnly(PartitionContainerImpl::getPartitionId)
                .addReadOnly(PartitionContainerImpl::getMapService)
                .addUpdate(p -> p.getRecordStore("map1"))
                .addUpdate(p -> p.getRecordStore("map2"))
                .addUpdate(p -> p.getRecordStore("map1", true))
                .addUpdate(p -> p.getRecordStore("map2", false))
                .addUpdate(p -> p.getRecordStoreForHotRestart("map1"))
                .addUpdate(p -> p.getRecordStoreForHotRestart("map2"))
                .addReadOnly(p -> p.getExistingRecordStore("map1"))
                .addWrite(p -> {
                    MapContainer mc1 = mock(MapContainer.class);
                    when(mc1.getName()).thenReturn("map1");
                    p.destroyMap(mc1);
                })
                .addWrite(p -> {
                    MapContainer mc2 = mock(MapContainer.class);
                    when(mc2.getName()).thenReturn("map2");
                    p.destroyMap(mc2);
                })
                .addReadOnly(PartitionContainerImpl::hasRunningCleanup)
                .addWrite(p -> p.setHasRunningCleanup(true))
                .addWrite(p -> p.setHasRunningCleanup(false))
                .addReadOnly(PartitionContainerImpl::getLastCleanupTime)
                .addWrite(p -> p.setLastCleanupTime(100L))
                .addWrite(p -> p.setLastCleanupTime(200L))
                .addReadOnly(PartitionContainerImpl::getLastCleanupTimeCopy)
                .addWrite(p -> p.setLastCleanupTimeCopy(100L))
                .addWrite(p -> p.setLastCleanupTimeCopy(200L))
                .addWrite(p -> p.cleanUpOnMigration(1))
                .addWrite(p -> p.cleanUpOnMigration(-1))
                .runTests();
    }
}
