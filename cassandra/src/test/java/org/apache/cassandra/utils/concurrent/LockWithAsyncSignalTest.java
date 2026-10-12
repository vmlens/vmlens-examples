
package org.apache.cassandra.utils.concurrent;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class LockWithAsyncSignalTest {

    //@Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(LockWithAsyncSignal::new)
                .addWrite(LockWithAsyncSignal::lock)
                .addWrite(lock -> {
                    try {
                        lock.lockInterruptibly();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
                .addUpdate(LockWithAsyncSignal::tryLock)
                .addWrite(lock -> {
                    try {
                        lock.await();
                    } catch (Exception e) {
                        // ignore exception if lock is not held
                    }
                })
                .addWrite(lock -> {
                    try {
                        lock.awaitUninterruptibly();
                    } catch (Exception e) {
                        // ignore exception if lock is not held
                    }
                })
                .addWrite(lock -> {
                    try {
                        lock.unlock();
                    } catch (Exception e) {
                        // ignore exception if lock is not held
                    }
                })
                .addWrite(LockWithAsyncSignal::signal)
                .addWrite(LockWithAsyncSignal::clearSignal)
                .addReadOnly(lock -> lock.isOwner(Thread.currentThread()))
                .runTests();
    }
}
