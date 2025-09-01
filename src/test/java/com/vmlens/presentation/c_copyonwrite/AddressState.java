package com.vmlens.presentation.c_copyonwrite;

public class AddressState {

    private final int updateCount;
    private final String street;
    private final String city;

    public AddressState(int updateCount,
                        String street,
                        String city) {
        this.updateCount = updateCount;
        this.street = street;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public int getUpdateCount() {
        return updateCount;
    }
}
