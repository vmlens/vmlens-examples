package com.vmlens.javamagazin.notthreadsafe;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestAddress {

    @Disabled
    @RepeatedTest(100000)
    public void testIncrement() throws InterruptedException {
        Address address = new Address();
        Thread first = new Thread(() -> { address.setCity("A"); address.setStreet("A"); });
        first.start();
        address.setCity("B"); address.setStreet("B");
        first.join();
        assertThat(address,either( is(new Address("A" ,"A")))
                .or( is(new Address("B" ,"B"))));
    }

}
