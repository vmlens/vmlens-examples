package com.vmlens.examples.howto.entity.atomic;

public class Address {

    private String street;
    private String city;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public synchronized void update(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public synchronized String getStreetAndCity() {
        return  street + ", " + city;
    }

}
