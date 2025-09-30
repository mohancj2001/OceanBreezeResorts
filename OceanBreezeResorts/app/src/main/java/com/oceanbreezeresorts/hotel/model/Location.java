package com.oceanbreezeresorts.hotel.model;

public class Location {
    private String id;
    private String city;
    private String address_line;

    public Location(String id, String city, String address_line) {
        this.id = id;
        this.city = city;
        this.address_line = address_line;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress_line() {
        return address_line;
    }

    public void setAddress_line(String address_line) {
        this.address_line = address_line;
    }
}
