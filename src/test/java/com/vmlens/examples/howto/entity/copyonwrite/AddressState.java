package com.vmlens.examples.howto.entity.copyonwrite;

public class AddressState {

    private final String street;
    private final String city;

    public AddressState(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }
}
