package org.apache.lucene.util;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.util.SameThreadExecutorService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class SameThreadExecutorServiceTest {

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(SameThreadExecutorService::new)
        .addWrite(SameThreadExecutorService::shutdown)
        .addWrite(executor -> {
          try {
            executor.execute(() -> {});
          } catch (Exception ignored) {
          }
        })
        .addWrite(executor -> {
          try {
            executor.execute(() -> {
              int v = 1;
            });
          } catch (Exception ignored) {
          }
        })
        .addUpdate(SameThreadExecutorService::shutdownNow)
        .addReadOnly(SameThreadExecutorService::isShutdown)
        .addReadOnly(SameThreadExecutorService::isTerminated)
        .addReadOnly(executor -> {
          try {
            return executor.awaitTermination(1, TimeUnit.MILLISECONDS);
          } catch (InterruptedException e) {
            return false;
          }
        })
        .runTests();
  }
}
