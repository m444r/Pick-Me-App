package com.pickme.pickmeapp;

public class Location {
    private String name;
    private float latitude;
    private float longitude;

    public Location(String name, float lat, float lon) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
    }

    public boolean isCloseTo(Location other) {
        float dist = Math.abs(this.latitude - other.latitude) + Math.abs(this.longitude - other.longitude);
        return dist < 0.05f; // π.χ. κάτω από 50 μέτρα
    }

    public String getName() {
        return name;
    }
}
