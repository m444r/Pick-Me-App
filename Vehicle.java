package com.pickme.pickmeapp;

import java.sql.*;

public class Vehicle {
    private String vehicleId;
    private Driver owner;
    private String model;
    private String brand;
    private String color;
    private int manufactureYear;
    private String licensePlate;
    private String fuelType;
    private int totalSeats;
    private int availableSeats;
    private boolean insurance;
    private boolean AC;
    private boolean isSmokingAllowed;

    public Vehicle(String vehicleId, String model, String brand, String color,
                   int manufactureYear, String licensePlate, String fuelType,
                   int totalSeats, boolean insurance, boolean AC, boolean isSmokingAllowed) {
        this.vehicleId = vehicleId;
        this.model = model;
        this.brand = brand;
        this.color = color;
        this.manufactureYear = manufactureYear;
        this.licensePlate = licensePlate;
        this.fuelType = fuelType;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.insurance = insurance;
        this.AC = AC;
        this.isSmokingAllowed = isSmokingAllowed;
    }

    // Getters
    public String getVehicleId() { return vehicleId; }
    public String getModel() { return model; }
    public String getBrand() { return brand; }
    public String getColor() { return color; }
    public int getManufactureYear() { return manufactureYear; }
    public String getLicensePlate() { return licensePlate; }
    public String getFuelType() { return fuelType; }
    public int getTotalSeats() { return totalSeats; }
    public boolean hasInsurance() { return insurance; }
    public boolean hasAC() { return AC; }
    public boolean isSmokingAllowed() { return isSmokingAllowed; }

    // Φέρνει το όχημα του οδηγού
    public static Vehicle fetchForDriver(int driverId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM vehicle WHERE driver_id = ?")) {

            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Vehicle(
                    rs.getString("vehicle_id"),
                    rs.getString("model"),
                    rs.getString("brand"),
                    rs.getString("color"),
                    rs.getInt("manufacture_year"),
                    rs.getString("license_plate"),
                    rs.getString("fuel_type"),
                    rs.getInt("total_seats"),
                    rs.getBoolean("insurance"),
                    rs.getBoolean("ac"),
                    rs.getBoolean("smoking_allowed")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}


