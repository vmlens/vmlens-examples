
package org.apache.lucene.index;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class ConcurrentMergeSchedulerTest {

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(ConcurrentMergeScheduler::new)
        .addWrite(cms -> cms.setMaxMergesAndThreads(2, 2))
        .addWrite(cms -> cms.setMaxMergesAndThreads(4, 3))
        .addWrite(cms -> cms.setDefaultMaxMergesAndThreads(true))
        .addWrite(cms -> cms.setDefaultMaxMergesAndThreads(false))
        .addWrite(cms -> cms.setForceMergeMBPerSec(10.0))
        .addWrite(cms -> cms.setForceMergeMBPerSec(20.0))
        .addWrite(ConcurrentMergeScheduler::enableAutoIOThrottle)
        .addWrite(ConcurrentMergeScheduler::disableAutoIOThrottle)
        .addReadOnly(ConcurrentMergeScheduler::getForceMergeMBPerSec)
        .addReadOnly(ConcurrentMergeScheduler::getAutoIOThrottle)
        .addReadOnly(ConcurrentMergeScheduler::getIORateLimitMBPerSec)
        .addReadOnly(ConcurrentMergeScheduler::getMaxThreadCount)
        .addReadOnly(ConcurrentMergeScheduler::getMaxMergeCount)
        .addReadOnly(ConcurrentMergeScheduler::mergeThreadCount)
        .addReadOnly(ConcurrentMergeScheduler::toString)
        .runTests();
  }
}
