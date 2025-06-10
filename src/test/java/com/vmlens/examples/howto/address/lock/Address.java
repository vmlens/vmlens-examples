package com.vmlens.examples.howto.address.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Address {

    private final Lock lock = new ReentrantLock();
    private String street;
    private String city;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public void update(String street, String city) {
        lock.lock();
        try{
            this.street = street;
            this.city = city;
        } finally {
            lock.unlock();
        }

    }

    public synchronized String getStreetAndCity() {
        lock.lock();
        try{
            return  street + ", " + city;
        } finally {
            lock.unlock();
        }
    }

}
