package org.apache.lucene.search;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.search.LRUQueryCache;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import org.junit.jupiter.api.Test;

public class LRUQueryCacheTest {

  @Test
  public void testUpdate() throws InterruptedException {
    Query query1 = new MatchAllDocsQuery();
    Query query2 = new MatchNoDocsQuery();
    Object key1 = new Object();
    Object key2 = new Object();

    new AtomicTestBuilder<>(() -> new LRUQueryCache(10, 10000))
        .addWrite(cache -> cache.setSkipCacheFactor(1.5f))
        .addWrite(cache -> cache.setSkipCacheFactor(2.0f))
        .addWrite(cache -> cache.clearCoreCacheKey(key1))
        .addWrite(cache -> cache.clearCoreCacheKey(key2))
        .addWrite(cache -> cache.clearQuery(query1))
        .addWrite(cache -> cache.clearQuery(query2))
        .addWrite(LRUQueryCache::clear)
        .addReadOnly(LRUQueryCache::getSkipCacheFactor)
        .addReadOnly(LRUQueryCache::ramBytesUsed)
        .addReadOnly(LRUQueryCache::getChildResources)
        .addReadOnly(LRUQueryCache::getTotalCount)
        .addReadOnly(LRUQueryCache::getHitCount)
        .addReadOnly(LRUQueryCache::getMissCount)
        .addReadOnly(LRUQueryCache::getCacheSize)
        .addReadOnly(LRUQueryCache::getCacheCount)
        .addReadOnly(LRUQueryCache::getEvictionCount)
        .runTests();
  }
}
