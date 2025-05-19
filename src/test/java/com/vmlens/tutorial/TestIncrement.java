package com.vmlens.tutorial;

import com.vmlens.api.AllInterleaving;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestIncrement {

    private volatile int j = 0;

    @Test
    public void testReadWrite() throws InterruptedException {
        try(AllInterleaving allInterleaving = new AllInterleaving("testIncrement")) {
            while (allInterleaving.hasNext()) {
                j = 0;
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        j++;
                    }
                };
                first.start();
                j++;
                first.join();
                assertThat(j,is(2));
            }
        }
    }

}
