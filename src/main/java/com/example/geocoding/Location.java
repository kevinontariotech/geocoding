package com.example.geocoding;

public class Location {

    int id;
    double longitude, latitude;
    String Address;

    public Location(){}
    public Location(int id, double latitude, double longitude, String address) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        Address = address;
    }

    public Location(double latitude, double longitude, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        Address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
