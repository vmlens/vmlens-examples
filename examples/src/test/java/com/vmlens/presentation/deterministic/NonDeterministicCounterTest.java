package com.vmlens.presentation.deterministic;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NonDeterministicCounterTest {

    private volatile int j = 0;

    @Disabled
    @RepeatedTest(100000)
    public void testIncrement() throws InterruptedException {
        Thread first = new Thread(() -> j++);
        first.start();
        j++;
        first.join();
        assertThat(j, is(2));
    }
}
