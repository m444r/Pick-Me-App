package com.pickme.pickmeapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;

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
    
    private void createNewRoute() {
    JCheckBox useCurrentLocationCheckbox = new JCheckBox("Χρήση τρέχουσας τοποθεσίας");
    JTextField txtStart = new JTextField();
    JTextField txtEnd = new JTextField();

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Αφετηρία (αν δεν επιλεγεί χρήση τοποθεσίας):"));
    panel.add(txtStart);
    panel.add(useCurrentLocationCheckbox);
    panel.add(new JLabel("Προορισμός:"));
    panel.add(txtEnd);

    int result = JOptionPane.showConfirmDialog(this, panel, "Δημιουργία Διαδρομής", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        String startStr = useCurrentLocationCheckbox.isSelected()
            ? getApproximateLocationFromIP()
            : txtStart.getText();

        String endStr = txtEnd.getText();

        JOptionPane.showMessageDialog(this, "✅ Αφετηρία: " + startStr + "\n📍 Προορισμός: " + endStr);
    }
}
    
    private String getApproximateLocationFromIP() {
    try {
        URL url = new URL("https://ipapi.co/json/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();

        String json = content.toString();
        String city = json.split("\"city\":\"")[1].split("\"")[0];
        String country = json.split("\"country_name\":\"")[1].split("\"")[0];

        return city + ", " + country;

    } catch (Exception e) {
        System.out.println("❌ IP API failed.");
        return "Unknown";
    }
}



    // Getters
    public Location getStartLocation() { return startLocation; }
    public Location getEndLocation() { return endLocation; }
    public Driver getDriver() { return driver; }
    public String getId() { return id; }
}
