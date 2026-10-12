
package org.apache.lucene.index;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.util.BytesRef;
import org.junit.jupiter.api.Test;

public class BinaryDocValuesFieldUpdatesTest {

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(() -> new BinaryDocValuesFieldUpdates(0L, "field", 10))
        .addWrite(updates -> updates.add(0, new BytesRef("val1")))
        .addWrite(updates -> updates.add(1, new BytesRef("val2")))
        .addReadOnly(BinaryDocValuesFieldUpdates::ramBytesUsed)
        .runTests();
  }
}
