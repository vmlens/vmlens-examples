package com.vmlens.presentation.c_copyonwrite;

public class Address {

    private volatile AddressState state;

    public Address(String street, String city) {
        this.state = new AddressState(0,street,city);
    }
    public synchronized void update(String street, String city) {
        this.state = new AddressState(state.getUpdateCount() + 1,street,city);
    }

    public String getStreetAndCity() {
        AddressState current = state;
        return  current.getStreet() + ", " + current.getCity();
    }

    public int getUpdateCount() {
        return state.getUpdateCount();
    }
}
