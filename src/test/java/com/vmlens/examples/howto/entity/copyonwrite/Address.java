package com.vmlens.examples.howto.entity.copyonwrite;

public class Address {

    private volatile AddressState addressState;

    public Address(String street, String city) {
        this.addressState = new AddressState(street,city);

    }

    public void update(String street, String city) {
        this.addressState = new AddressState(street,city);
    }

    public String getStreetAndCity() {
        AddressState currentState = addressState;
        return  currentState.getStreet() + ", " + currentState.getCity();
    }

}
