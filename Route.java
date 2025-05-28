package com.pickme.pickmeapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Route {
    private String id;
    private Location startLocation;
    private Location endLocation;
    private LocalDateTime departureTime;
    private LocalDateTime estimatedArrival;
    private Driver driver;
    private float costPerPassenger;
    private boolean active;
    private List<Passenger> passengers;

    // Constructor
    public Route(Location startLocation, Location endLocation, LocalDateTime departureTime, float costPerPassenger, Driver driver) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.departureTime = departureTime;
        this.costPerPassenger = costPerPassenger;
        this.driver = driver;
        this.estimatedArrival = calculateETA();
        this.passengers = new ArrayList<>();
        this.active = true;
        this.id = generateId();
    }

    private String generateId() {
        return "ROUTE-" + System.currentTimeMillis();
    }

    private LocalDateTime calculateETA() {
        // Απλά +1 ώρα για παράδειγμα (μπορούμε να βάλουμε απόσταση)
        return departureTime.plusHours(1);
    }

    public boolean isActive() {
        return active;
    }

    public boolean includes(Location loc) {
        // Αν ταιριάζει περίπου με start ή end
        return startLocation.isCloseTo(loc) || endLocation.isCloseTo(loc);
    }

    public boolean isMatchingRoute(Location start, Location end) {
        return includes(start) && includes(end);
    }

    public void addPassenger(Passenger p) {
        passengers.add(p);
    }

    public int getTotalPassengers() {
        return passengers.size();
    }

    // Getters
    public Location getStartLocation() { return startLocation; }
    public Location getEndLocation() { return endLocation; }
    public Driver getDriver() { return driver; }
    public String getId() { return id; }
}
