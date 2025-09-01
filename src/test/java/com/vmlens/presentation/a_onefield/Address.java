package com.vmlens.presentation.a_onefield;

public class Address {

    private  String street;

    public Address(String street) {
        this.street = street;
    }

    public void update(String street) {
        this.street = street;
    }

    public String getStreetAndCity() {
        return  street;
    }
}
