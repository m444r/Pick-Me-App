

package pickmeapp;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class RideRequest {
    private static final String GOOGLE_API_KEY = "ΤΟ_API_KEY_ΣΟΥ";

    public class LatLng {
    public double lat;
    public double lng;

    public LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}

    public static double distanceBetween(LatLng point1, LatLng point2) {
    final int R = 6371; // Ακτίνα Γης σε km

    double latDistance = Math.toRadians(point2.lat - point1.lat);
    double lonDistance = Math.toRadians(point2.lng - point1.lng);

    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
             + Math.cos(Math.toRadians(point1.lat)) * Math.cos(Math.toRadians(point2.lat))
             * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c; 

    return distance; 
}

  public static boolean isPassengerRouteInsideDriverRoute(List<LatLng> passengerRoute, List<LatLng> driverRoute) {
    for (LatLng passengerPoint : passengerRoute) {
        boolean closeToDriver = false;
        for (LatLng driverPoint : driverRoute) {
            if (distanceBetween(passengerPoint, driverPoint) < 0.3) { 
                closeToDriver = true;
                break;
            }
        }
        if (!closeToDriver) return false;
    }
    return true;
}

    public static List<LatLng> getRouteCoordinates(String origin, String destination, String apiKey) {
    List<LatLng> coordinates = new ArrayList<>();

    try {
        origin = URLEncoder.encode(origin, "UTF-8");
        destination = URLEncoder.encode(destination, "UTF-8");
        String urlString = String.format(
            "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s",
            origin, destination, apiKey
        );

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        JSONArray routes = json.getJSONArray("routes");
        if (routes.length() == 0) return coordinates;

        JSONArray steps = routes.getJSONObject(0)
                                .getJSONArray("legs")
                                .getJSONObject(0)
                                .getJSONArray("steps");

        for (int i = 0; i < steps.length(); i++) {
            JSONObject startLoc = steps.getJSONObject(i).getJSONObject("start_location");
            double lat = startLoc.getDouble("lat");
            double lng = startLoc.getDouble("lng");
            coordinates.add(new LatLng(lat, lng));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return coordinates;
}
public static void checkPassengerMatchesDriver() {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password")) {
       
        String driverSql = "SELECT id, pickup_address, address FROM ride_requests WHERE driver_id IS NOT NULL AND status = 'PENDING'";
        PreparedStatement driverStmt = conn.prepareStatement(driverSql);
        ResultSet driverRs = driverStmt.executeQuery();

        
        while (driverRs.next()) {
            int driverRequestId = driverRs.getInt("id");
            String driverStart = driverRs.getString("pickup_address");
            String driverEnd = driverRs.getString("address");

            
            String passengerSql = "SELECT id, pickup_address, address FROM ride_requests WHERE passenger_id IS NOT NULL AND status = 'PENDING'";
            PreparedStatement passengerStmt = conn.prepareStatement(passengerSql);
            ResultSet passengerRs = passengerStmt.executeQuery();

            while (passengerRs.next()) {
                int passengerRequestId = passengerRs.getInt("id");
                String passengerStart = passengerRs.getString("pickup_address");
                String passengerEnd = passengerRs.getString("address");

                
                boolean match = isPassengerRouteInsideDriverRoute(driverStart, driverEnd, passengerStart, passengerEnd);

                if (match) {
                    System.out.println("✔️ Βρέθηκε match: Driver #" + driverRequestId + " με Passenger #" + passengerRequestId);
                    DriverHome.loadRideRequests(); 
                }
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static void assignMatchingRequestsToDriver(int driverId, String driverStart, String driverEnd, String apiKey) {
    String sql = "SELECT id, pickup_address, address FROM ride_requests WHERE driver_id IS NULL AND status = 'PENDING'";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        List<LatLng> driverRoute = getRouteCoordinates(driverStart, driverEnd, apiKey);

        while (rs.next()) {
            int requestId = rs.getInt("id");
            String pickup = rs.getString("pickup_address");
            String dropoff = rs.getString("address");

            List<LatLng> passengerRoute = getRouteCoordinates(pickup, dropoff, apiKey);

            if (isPassengerRouteInsideDriverRoute(passengerRoute, driverRoute)) {
               
                assignRequestToDriver(requestId, driverId);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "❌ Σφάλμα κατά τον έλεγχο αιτημάτων: " + e.getMessage());
    }
}
public static void assignRequestToDriver(int rideRequestId, int driverId) {
    String sql = "UPDATE ride_requests SET driver_id = ? WHERE id = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, driverId);
        stmt.setInt(2, rideRequestId);

        int updated = stmt.executeUpdate();

        if (updated > 0) {
            System.out.println("✅ Η διαδρομή ανατέθηκε στον οδηγό.");
            DriverHome.loadRideRequests();
        } else {
            System.out.println("⚠️ Δεν βρέθηκε διαδρομή για ανάθεση.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "❌ Σφάλμα κατά την ανάθεση διαδρομής: " + e.getMessage());
    }
}

   

}
