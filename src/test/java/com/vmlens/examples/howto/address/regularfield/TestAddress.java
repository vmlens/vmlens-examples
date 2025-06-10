package com.vmlens.examples.howto.address.regularfield;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestAddress {

    @Test
    public void readWrite() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("howto.address.regularFieldReadWrite")) {
            while (allInterleavings.hasNext()) {
                // Given
                Address address = new Address("First Street", "First City");

                // When
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        address.update("Second Street","Second City");
                    }
                };
                first.start();
                String streetAndCity = address.getStreetAndCity();;
                first.join();

                // Then
                assertThat(streetAndCity,anyOf(is("First Street, First City"),
                        is("Second Street, Second City")));
            }
        }
    }

}
