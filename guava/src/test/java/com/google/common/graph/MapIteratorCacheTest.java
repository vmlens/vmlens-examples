
package com.google.common.graph;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class MapIteratorCacheTest {

    //@Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> new MapIteratorCache<String, String>(new HashMap<>()))
                // put: changes state, returns value, has arguments -> add two addUpdate
                .addUpdate(cache -> cache.put("key1", "value1"))
                .addUpdate(cache -> cache.put("key2", "value2"))
                // remove: changes state, returns value, has arguments -> add two addUpdate
                .addUpdate(cache -> cache.remove("key1"))
                .addUpdate(cache -> cache.remove("key2"))
                // clear: changes state, returns void, no arguments -> add one addWrite
                .addWrite(cache -> cache.clear())
                // get: read only -> add addReadOnly
                .addReadOnly(cache -> cache.get("key1"))
                // getWithoutCaching: read only -> add addReadOnly
                .addReadOnly(cache -> cache.getWithoutCaching("key1"))
                // containsKey: read only -> add addReadOnly
                .addReadOnly(cache -> cache.containsKey("key1"))
                // unmodifiableKeySet: read only -> add addReadOnly
                .addReadOnly(cache -> cache.unmodifiableKeySet())
                // getIfCached: read only -> add addReadOnly
                .addReadOnly(cache -> cache.getIfCached("key1"))
                // clearCache: changes state, returns void, no arguments -> add one addWrite
                .addWrite(cache -> cache.clearCache())
                .runTests();
    }
}
