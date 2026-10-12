
package org.apache.lucene.search.suggest.tst;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class TSTLookupTest {

  //@Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(TSTLookup::new)
        .addUpdate(lookup -> lookup.add("key1", 1L))
        .addUpdate(lookup -> lookup.add("key2", 2L))
        .addReadOnly(lookup -> lookup.get("key1"))
        .addReadOnly(lookup -> lookup.lookup("key1", null, false, 5))
        .addReadOnly(TSTLookup::getCount)
        .addReadOnly(TSTLookup::ramBytesUsed)
        .runTests();
  }
}
