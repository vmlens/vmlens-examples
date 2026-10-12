
package org.apache.lucene.index;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class NumericDocValuesFieldUpdatesTest {

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(() -> new NumericDocValuesFieldUpdates(0L, "field", 1024))
        .addWrite(updates -> updates.add(0, 10L))
        .addWrite(updates -> updates.add(1, 20L))
        .addReadOnly(NumericDocValuesFieldUpdates::any)
        .addReadOnly(NumericDocValuesFieldUpdates::ramBytesUsed)
        .runTests();
  }
}
