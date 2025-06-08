package com.vmlens.examples.howto.entity.regularfield;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestAddress {

    @Test
    public void readWrite() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("howto.entity.regularFieldReadWrite")) {
            while (allInterleavings.hasNext()) {
                // Given
                Address address = new Address("First Street", "First City");

                // When
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        address.setStreet("Second Street");
                        address.setCity("Second City");
                    }
                };
                first.start();
                String streetAndCity = address.getStreet() + ", " + address.getCity();
                first.join();

                // Then
                assertThat(streetAndCity,anyOf(is("First Street, First City"),
                        is("Second Street, Second City")));
            }
        }
    }

    @Test
    public void writeWrite() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("howto.entity.regularFieldWriteWrite")) {
            while (allInterleavings.hasNext()) {
                // Given
                Address address = new Address("First Street", "First City");

                // When
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        address.setStreet("Second Street");
                        address.setCity("Second City");
                    }
                };
                first.start();
                address.setStreet("Third Street");
                address.setCity("Third City");
                first.join();

                // Then
                String streetAndCity = address.getStreet() + ", " + address.getCity();
                assertThat(streetAndCity,anyOf(is("Second Street, Second City"),
                        is("Third Street, Third City")));
            }
        }
    }

}
