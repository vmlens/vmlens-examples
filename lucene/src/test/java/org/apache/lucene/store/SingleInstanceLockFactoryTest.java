
package org.apache.lucene.store;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class SingleInstanceLockFactoryTest {

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(SingleInstanceLockFactory::new)
        .addUpdate(
            factory -> {
              try {
                return factory.obtainLock(null, "lock1") != null;
              } catch (Exception e) {
                return false;
              }
            })
        .addUpdate(
            factory -> {
              try {
                return factory.obtainLock(null, "lock2") != null;
              } catch (Exception e) {
                return false;
              }
            })
        .runTests();
  }
}
