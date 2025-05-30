package com.pickme.pickmeapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Driver extends User {
    private String licenseNumber;
    private Date licenseExpireDate;
    private List<Vehicle> vehicles;
    private List<Route> routes;

    public class Driver extends User {
    private String licenseNumber;
    private Date licenseExpireDate;
    private List<Vehicle> vehicles;
    private List<Route> routes;
    
    public Driver(String name, String email, String licenseNumber) {
        super(name, email);
        this.licenseNumber = licenseNumber;
        this.vehicles = new ArrayList<>();
        this.routes = new ArrayList<>();
    }

    public Route createRoute(Location start, Location end, LocalDateTime departureTime) throws RouteCreationException {
        

        Route route = new Route.RouteBuilder()
                .setStart(start)
                .setEnd(end)
                .setDepartureTime(departureTime)
                .setDriver(this)
                .build();

        routes.add(route);
        return route;
    }
        
        
        public void acceptRequest(RideRequest request) throws RequestProcessingException {
        request.accept();
    }

    public void rejectRequest(RideRequest request) throws RequestProcessingException {
        request.reject();
    }

    public void ratePassenger(Passenger passenger, float rating, String comment) throws RatingException {
        super.rate(passenger, rating, comment);
    }
}
