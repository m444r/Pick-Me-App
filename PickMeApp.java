
package pickmeapp;

import java.util.List;

public class PickMeApp {
    public static void main(String[] args) {
         StartPage.main(args); 
    }
}

// ===================== USERS =====================
class Passenger {
    private String id;
    private String name;
    private String email;

    public void login(String email, String id) {}
    public void pickInterest(String interest) {}
    public void rating(Driver driver, int stars, String comment) {}
}

class Driver {
    private String id;
    private String name;
    private Vehicle vehicle;

    public void login(String email, String id) {}
    public void acceptRequest(Request request) {}
    public void rating(Driver driver, int stars, String comment) {}
}

// ===================== AUTHENTICATION =====================
//class Login {
  //  private String username;
    //private String password;

    //public boolean authenticate() { return false; }
//}

class Verification {
    private String method;

    public boolean verifyCode(String code) { return false; }
}

// ===================== INTERACTION =====================
class PickInterest {
    private String category;

    public void interest(String interest) {}
}

class PickMode {}

// ===================== REQUESTING A RIDE =====================
class Request {
    private String id;
    private Passenger passenger;
    private Driver driver;
    private Location location;
    private Destination destination;
    private String status;

    public void updateStatus(String newStatus) {}
}

class SearchDriver {
    private List<Driver> availableDrivers;

    public List<Driver> findDrivers(Location location) { return null; }
}

// ===================== LOCATION & ROUTE =====================
class Location {
    private double latitude;
    private double longitude;

    public String getAddress() { return ""; }
}

class Destination {
    private double latitude;
    private double longitude;

    public String getAddress() { return ""; }
}

class Route {
    private Location start;
    private Destination end;
    private double distance;
}

// ===================== VEHICLE SYSTEM =====================
class Vehicle {
    private String plate;
    private String model;
    private List<Seats> seats;

    public boolean isAvailable() { return false; }
}

class Seats {
    private int seatNumber;
    private boolean isOccupied;

    public void occupy() {}
    public void release() {}
}

// ===================== FEEDBACK =====================
class Rating {
    private Passenger passenger;
    private Driver driver;
    private int stars;
    private String comment;

    public void leaveRating() {}
}
