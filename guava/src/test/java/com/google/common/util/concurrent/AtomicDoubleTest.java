package com.google.common.util.concurrent;

import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;

public class AtomicDoubleTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(AtomicDouble::new)
            .addReadOnly(AtomicDouble::get)
            .addReadOnly(AtomicDouble::toString)
            .addReadOnly(AtomicDouble::intValue)
            .addReadOnly(AtomicDouble::longValue)
            .addReadOnly(AtomicDouble::floatValue)
            .addReadOnly(AtomicDouble::doubleValue)
            .addWrite(atomicDouble -> atomicDouble.set(1.0))
            .addWrite(atomicDouble -> atomicDouble.set(2.0))
            .addWrite(atomicDouble -> atomicDouble.lazySet(3.0))
            .addWrite(atomicDouble -> atomicDouble.lazySet(4.0))
            .addUpdate(atomicDouble -> atomicDouble.getAndSet(5.0))
            .addUpdate(atomicDouble -> atomicDouble.getAndSet(6.0))
            .addUpdate(atomicDouble -> atomicDouble.compareAndSet(0.0, 7.0))
            .addUpdate(atomicDouble -> atomicDouble.compareAndSet(5.0, 8.0))
            .addUpdate(atomicDouble -> atomicDouble.weakCompareAndSet(0.0, 9.0))
            .addUpdate(atomicDouble -> atomicDouble.weakCompareAndSet(7.0, 10.0))
            .addUpdate(atomicDouble -> atomicDouble.getAndAdd(1.0))
            .addUpdate(atomicDouble -> atomicDouble.getAndAdd(2.0))
            .addUpdate(atomicDouble -> atomicDouble.addAndGet(3.0))
            .addUpdate(atomicDouble -> atomicDouble.addAndGet(4.0))
            .addUpdate(atomicDouble -> atomicDouble.getAndAccumulate(1.0, Double::sum))
            .addUpdate(atomicDouble -> atomicDouble.getAndAccumulate(2.0, Double::sum))
            .addUpdate(atomicDouble -> atomicDouble.accumulateAndGet(3.0, Double::sum))
            .addUpdate(atomicDouble -> atomicDouble.accumulateAndGet(4.0, Double::sum))
            .addUpdate(atomicDouble -> atomicDouble.getAndUpdate(x -> x + 1.0))
            .addUpdate(atomicDouble -> atomicDouble.getAndUpdate(x -> x + 2.0))
            .addUpdate(atomicDouble -> atomicDouble.updateAndGet(x -> x + 3.0))
            .addUpdate(atomicDouble -> atomicDouble.updateAndGet(x -> x + 4.0))
            .runTests();
    }
}