package com.vmlens.presentation.c_copyonwrite;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * Über alle Thread Interleavings iterieren, jeweils zwei Methoden parallel ausführen
 *
 * Jeweils für jede Kombination aus non commutative atomic actions ein Test:
 *
 *      Thread 1 read,  Thread 2 write !=  Thread 2 write,  Thread 1 read  =>  test readWrite
 *      Thread 1 write, Thread 2 write !=  Thread 2 write,  Thread 1 write =>  test writeWrite
 *
 * Optional, um ganz sicher zu gehen:
 *      Thread 1 read, Thread 2 read ==  Thread 2 read,  Thread 1 read
 *
 */


public class TestAddress {

    @Test
    public void readWrite() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("presentation.copyonwrite.readWrite")) {
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

    @Test
    public void writeWrite() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("presentation.copyonwrite.writeWrite")) {
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
                address.update("Third Street","Third City");
                first.join();

                // Then
                String streetAndCity = address.getStreetAndCity();;
                assertThat(streetAndCity,anyOf(is("Second Street, Second City"),
                        is("Third Street, Third City")));
                assertThat(address.getUpdateCount(),is(2));
            }
        }
    }

}
