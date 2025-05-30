
package pickmeapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

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
    private DriverHome driver;
    private float costPerPassenger;
    private boolean active;
    private static List<PassengerHome> passengers;
    private static ArrayList<String> routeHistory;
    // Constructor
    public Route(Location startLocation, Location endLocation, LocalDateTime departureTime, float costPerPassenger, DriverHome driver) {
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

    public void addPassenger(PassengerHome p) {
        passengers.add(p);
    }

    public int getTotalPassengers() {
        return passengers.size();
    }
    
    public static void createNewRoute() {
    JCheckBox useCurrentLocationCheckbox = new JCheckBox("Χρήση τρέχουσας τοποθεσίας");
    JTextField txtStart = new JTextField();
    JTextField txtEnd = new JTextField();
    JButton btnShowHistory = new JButton("Ιστορικό Διαδρομών");
    btnShowHistory.addActionListener(e -> showRouteHistory());

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Αφετηρία (ή επιλέξτε τρέχουσα):"));
    panel.add(txtStart);
    panel.add(useCurrentLocationCheckbox);
    panel.add(new JLabel("Προορισμός:"));
    panel.add(txtEnd);
    panel.add(btnShowHistory);

    int result = JOptionPane.showConfirmDialog(null, panel, "Δημιουργία Διαδρομής", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        String startStr = useCurrentLocationCheckbox.isSelected() ? getApproximateLocationFromIP() : txtStart.getText();
        String endStr = txtEnd.getText();

        String sql;
        if ("DRIVER".equalsIgnoreCase(Session.pickMode)) {
            sql = "INSERT INTO ride_requests (driver_id, pickup_address , address, timestamp) VALUES (?, ?, ?, NOW())";
        } else {
            sql = "INSERT INTO ride_requests (passenger_id, pickup_address , address, timestamp) VALUES (?, ?, ?, NOW())";
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.userId);
            stmt.setString(2, startStr);
            stmt.setString(3, endStr);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null,
                "✅ Διαδρομή αποθηκεύτηκε:\nΑπό: " + startStr + "\nΠρος: " + endStr);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ Σφάλμα αποθήκευσης διαδρομής: " + e.getMessage());
        }
    }
}

    
   private static void showRouteHistory() {
    List<String> routeHistory = fetchRouteHistoryFromDB(); // Από DB

    if (routeHistory.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Δεν υπάρχουν προηγούμενες διαδρομές.");
        return;
    }

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    for (String route : routeHistory) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        JLabel routeLabel = new JLabel(route);
        
        JToggleButton favToggle = new JToggleButton(" ♡");
        favToggle.setToolTipText("Προσθήκη στα αγαπημένα");

        // Προαιρετικά: έλεγχος αν είναι ήδη στα αγαπημένα
        boolean isFavorite = Favorites.checkIfFavorite(Session.userId, route);
        favToggle.setSelected(isFavorite);
        favToggle.setText(isFavorite ? "♥" : " ♡");

        favToggle.addActionListener(e -> {
            boolean selected = favToggle.isSelected();
            favToggle.setText(selected ? "♥" : " ♡");
            if (selected) {
                Favorites.addFavorite(Session.userId, route);
            } else {
                Favorites.removeFavorite(Session.userId, route);
            }
        });

        rowPanel.add(routeLabel, BorderLayout.CENTER);
        rowPanel.add(favToggle, BorderLayout.EAST);
        panel.add(rowPanel);
    }

    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    JOptionPane.showMessageDialog(null, scrollPane, "Ιστορικό Διαδρομών", JOptionPane.INFORMATION_MESSAGE);
}

   private static List<String> fetchRouteHistoryFromDB() {
    List<String> history = new ArrayList<>();

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement("SELECT pickup_address, address FROM ride_requests WHERE driver_id = ? OR passenger_id = ? ORDER BY timestamp DESC")) {

        stmt.setInt(1, Session.userId);
        stmt.setInt(2, Session.userId);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String from = rs.getString("pickup_address");
                String to = rs.getString("address");
                history.add("Από: " + from + " → Προς: " + to);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return history;
}

   









    public static String getApproximateLocationFromIP() {
    try {
        URL url = new URL("https://ipapi.co/json/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0"); // Χρήσιμο για αποδοχή από το API

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();

        String json = content.toString();
        System.out.println("🔎 JSON Απόκριση: " + json); // Εκτύπωσε το για έλεγχο

        // Εξαγωγή πεδίων με ασφαλή τρόπο
        if (json.contains("\"city\":\"") && json.contains("\"country_name\":\"")) {
            String city = json.split("\"city\":\"")[1].split("\"")[0];
            String country = json.split("\"country_name\":\"")[1].split("\"")[0];
            return city + ", " + country;
        } else {
            System.out.println("⚠️ Δεν βρέθηκαν τα πεδία city ή country_name.");
            return "Unknown";
        }

    } catch (Exception e) {
        System.out.println("❌ Σφάλμα κατά το αίτημα στο IP API.");
        e.printStackTrace();
        return "Unknown";
    }
}




    // Getters
    public Location getStartLocation() { return startLocation; }
    public Location getEndLocation() { return endLocation; }
    public DriverHome getDriver() { return driver; }
    public String getId() { return id; }
}
