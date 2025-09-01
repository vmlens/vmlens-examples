package com.vmlens.presentation.a_onefield;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * readWrite
 *      Read and write ist non-commutative
 *      FirstThread write MainThread read != MainThread read FirstThread write
 *      offset + 2 Testläufe
 *
 * readRead
 *      Read and read ist commutative
 *      FirstThread read MainThread read == MainThread read FirstThread read
 *      offset + 0 Testläufe
 *
 */
public class TestAddress {


    @Test
    public void readWrite() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("presentation.onefield.readwrite")) {
            while (allInterleavings.hasNext()) {
                // Given
                Address address = new Address("First Street");

                // When
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        address.update("Second Street");
                    }
                };
                first.start();
                String streetAndCity = address.getStreetAndCity();;
                first.join();

                // Then
                assertThat(streetAndCity,anyOf(is("First Street"),
                        is("Second Street")));
            }
        }
    }


    @Test
    public void readRead() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("presentation.onefield.readread")) {
            while (allInterleavings.hasNext()) {
                // Given
                Address address = new Address("First Street");

                // When
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        String streetAndCity = address.getStreetAndCity();
                        assertThat(streetAndCity,is("First Street"));
                    }
                };
                first.start();
                String streetAndCity = address.getStreetAndCity();;
                assertThat(streetAndCity,is("First Street"));
                first.join();
            }

        }
    }

}
