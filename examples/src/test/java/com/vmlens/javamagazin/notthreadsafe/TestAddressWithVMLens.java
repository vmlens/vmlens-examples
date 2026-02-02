package com.vmlens.javamagazin.notthreadsafe;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestAddressWithVMLens {

    @Test
    public void testUpdate() throws InterruptedException {
        try (AllInterleavings allInterleavings =
                     new AllInterleavings("javamagazin")) {
            while (allInterleavings.hasNext()) {
                Address address = new Address();
                Thread first = new Thread(() -> {
                    address.setCity("A");
                    address.setStreet("A");
                });
                first.start();
                address.setCity("B");
                address.setStreet("B");
                first.join();
                assertThat(address, either(is(new Address("A", "A")))
                        .or(is(new Address("B", "B"))));
            }
        }
    }
}
