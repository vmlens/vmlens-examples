package com.vmlens.presentation.b_twofields;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

public class TestAddress {

    @Test
    public void readWrite() throws InterruptedException {
        int i = 0;

        try(AllInterleavings allInterleavings = new AllInterleavings("presentation.twofields")) {
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

                i++;

                // Then
                /*
                 * Test ob update atomar ist:
                 * Erwartes Ergebnis entweder vor oder nach dem update
                 */
                //assertThat(streetAndCity,anyOf(
                //        is("First Street, First City"),
                //        is("Second Street, Second City")));
            }

            System.out.println(i);
        }
    }

}
