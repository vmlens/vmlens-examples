package com.vmlens.examples.howto.address.volatilefield;

public class Address {

    private volatile String street;
    private volatile String city;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }
    public void update(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public String getStreetAndCity() {
        return  street + ", " + city;
    }
}
