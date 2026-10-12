package org.apache.lucene.facet.taxonomy.writercache;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.facet.taxonomy.FacetLabel;
import org.apache.lucene.facet.taxonomy.writercache.LruTaxonomyWriterCache;
import org.junit.jupiter.api.Test;

public class LruTaxonomyWriterCacheTest {

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(() -> new LruTaxonomyWriterCache(100))
        .addWrite(LruTaxonomyWriterCache::clear)
        .addUpdate(cache -> cache.put(new FacetLabel("a"), 1))
        .addUpdate(cache -> cache.put(new FacetLabel("b"), 2))
        .addReadOnly(LruTaxonomyWriterCache::isFull)
        .addReadOnly(LruTaxonomyWriterCache::size)
        .addReadOnly(cache -> cache.get(new FacetLabel("a")))
        .runTests();
  }
}
