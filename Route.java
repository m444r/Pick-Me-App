
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
    
    public static void EndofRoute(int rideRequestId) {
    String updateSql;
    if (Session.pickMode.equalsIgnoreCase("driver")) {
        updateSql = "UPDATE ride_requests SET driver_completed = TRUE WHERE id = ?";
    } else {
        updateSql = "UPDATE ride_requests SET passenger_completed = TRUE WHERE id = ?";
    }

    String checkSql = "SELECT driver_completed, passenger_completed FROM ride_requests WHERE id = ?";
    String completeSql = "UPDATE ride_requests SET status = 'COMPLETED' WHERE id = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password")) {

        // Πρώτο update: μαρκάρει ποιος ολοκλήρωσε
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setInt(1, rideRequestId);
            stmt.executeUpdate();
        }

        // Έλεγχος αν και οι δύο έχουν ολοκληρώσει
        boolean driverDone = false;
        boolean passengerDone = false;

        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setInt(1, rideRequestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                driverDone = rs.getBoolean("driver_completed");
                passengerDone = rs.getBoolean("passenger_completed");
            }
        }

        // Αν και οι δύο έχουν τελειώσει, κάνουμε πλήρη ολοκλήρωση
        if (driverDone && passengerDone) {
            try (PreparedStatement stmt = conn.prepareStatement(completeSql)) {
                stmt.setInt(1, rideRequestId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "✅ Η διαδρομή ολοκληρώθηκε από όλους!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Η διαδρομή σημειώθηκε ως ολοκληρωμένη από εσάς.\nΑναμένεται και ο άλλος χρήστης.");
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "❌ Σφάλμα κατά την ενημέρωση της διαδρομής:\n" + ex.getMessage());
    }
}


    
   public static void createNewRoute() {
    JCheckBox useCurrentLocationCheckbox = new JCheckBox("Χρήση τρέχουσας τοποθεσίας");
    JTextField txtStart = new JTextField();
    JTextField txtEnd = new JTextField();
    JButton btnShowHistory = new JButton("Ιστορικό Διαδρομών");
    btnShowHistory.addActionListener(e -> showRouteHistory());

    // Νέο κουμπί "Προσθήκη στα Αγαπημένα"
    JButton btnAddFavorite = new JButton("Προσθήκη στα Αγαπημένα");

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Αφετηρία (ή επιλέξτε τρέχουσα):"));
    panel.add(txtStart);
    panel.add(useCurrentLocationCheckbox);
    panel.add(new JLabel("Προορισμός:"));
    panel.add(txtEnd);
    panel.add(btnShowHistory);
    panel.add(btnAddFavorite);  // Προσθέτουμε το κουμπί στο panel

    // Αρχικά το αποτέλεσμα null, θα το ορίσουμε χειροκίνητα παρακάτω
    final int[] dialogResult = {JOptionPane.CANCEL_OPTION};

    // Δημιουργούμε το JOptionPane για να έχουμε πρόσβαση στο αποτέλεσμα
    JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

    JDialog dialog = optionPane.createDialog("Δημιουργία Διαδρομής");

    // Listener για το κουμπί "Προσθήκη στα Αγαπημένα"
    btnAddFavorite.addActionListener(e -> {
        String startStr = useCurrentLocationCheckbox.isSelected() ? getApproximateLocationFromIP() : txtStart.getText().trim();
        String endStr = txtEnd.getText().trim();

        if (startStr.isEmpty() || endStr.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Παρακαλώ συμπληρώστε αφετηρία και προορισμό για να προσθέσετε στα αγαπημένα.");
            return;
        }

        String routeText = "Από: " + startStr + " → Προς: " + endStr;
        Favorites.addFavorite(Session.userId, routeText);

        JOptionPane.showMessageDialog(dialog, "Η διαδρομή προστέθηκε στα αγαπημένα!");
    });

    dialog.setVisible(true);

    // Παίρνουμε το αποτέλεσμα επιλογής (OK ή Cancel)
    Object value = optionPane.getValue();
    dialogResult[0] = (value instanceof Integer) ? (Integer) value : JOptionPane.CLOSED_OPTION;

    if (dialogResult[0] == JOptionPane.OK_OPTION) {
        String startStr = useCurrentLocationCheckbox.isSelected() ? getApproximateLocationFromIP() : txtStart.getText().trim();
        String endStr = txtEnd.getText().trim();

        if (startStr.isEmpty() || endStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Παρακαλώ συμπληρώστε αφετηρία και προορισμό.");
            return;
        }

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
        
        //JToggleButton favToggle = new JToggleButton(" ♡");
        //favToggle.setToolTipText("Προσθήκη στα αγαπημένα");

        // Προαιρετικά: έλεγχος αν είναι ήδη στα αγαπημένα
       /*boolean isFavorite = Favorites.checkIfFavorite(Session.userId, route);
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
        });*/

        rowPanel.add(routeLabel, BorderLayout.CENTER);
        //rowPanel.add(favToggle, BorderLayout.EAST);
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

   

public static void weFoundDriver(int passengerId) {
    String sql = "SELECT u.id as driver_id, u.name, u.email, v.brand, v.model, v.color, v.license_plate, " +
                 "v.fuel_type, v.total_seats, v.ac, v.smoking_allowed, " +
                 "r.pickup_address, r.address " +
                 "FROM ride_requests r " +
                 "JOIN users u ON r.driver_id = u.id " +
                 "JOIN vehicle v ON v.driver_id = u.id " +
                 "WHERE r.passenger_id = ? AND r.status = 'ACCEPTED' " +
                 "ORDER BY r.timestamp DESC LIMIT 1";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, passengerId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int driverId = rs.getInt("driver_id");
            String driverName = rs.getString("name");
            String email = rs.getString("email");
            String brand = rs.getString("brand");
            String model = rs.getString("model");
            String color = rs.getString("color");
            String licensePlate = rs.getString("license_plate");
            String fuel = rs.getString("fuel_type");
            int seats = rs.getInt("total_seats");
            boolean ac = rs.getBoolean("ac");
            boolean smoking = rs.getBoolean("smoking_allowed");
            String pickup = rs.getString("pickup_address");
            String destination = rs.getString("address");

            // Χρήση της getAverageRatingForUser
            double avgRating = Review.getAverageRatingForUser(driverId);

            String message = "🚗 Ο οδηγός σας είναι έτοιμος!\n\n" +
                             "👤 Όνομα: " + driverName + "\n" +
                             "📧 Email: " + email + "\n\n" +
                             "🛻 Όχημα: " + brand + " " + model + " (" + color + ")\n" +
                             "🔢 Πινακίδα: " + licensePlate + "\n" +
                             "⛽ Καύσιμο: " + fuel + "\n" +
                             "💺 Θέσεις: " + seats + "\n" +
                             "❄️ AC: " + (ac ? "Ναι" : "Όχι") + "\n" +
                             "🚬 Κάπνισμα: " + (smoking ? "Επιτρέπεται" : "Όχι") + "\n" +
                             "🗺️ Διαδρομή: " + pickup + " → " + destination + "\n\n" +
                             "⭐ Μέσος όρος αξιολόγησης οδηγού: " + (avgRating > 0 ? String.format("%.2f", avgRating) : "Δεν υπάρχουν αξιολογήσεις ακόμα");

            JOptionPane.showMessageDialog(null, message, "Βρέθηκε Οδηγός!", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(null, "❌ Δεν βρέθηκε επιβεβαιωμένος οδηγός ακόμα.");
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Σφάλμα κατά την αναζήτηση οδηγού:\n" + ex.getMessage());
    }
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
  public static int getAcceptedRideRequestId(int userId) {
    String role = Session.pickMode;
    String sql;

    if ("driver".equalsIgnoreCase(role)) {
        sql = "SELECT id FROM ride_requests WHERE driver_id = ? AND status = 'ACCEPTED' AND driver_completed = FALSE ORDER BY timestamp DESC LIMIT 1";
    } else {
        sql = "SELECT id FROM ride_requests WHERE passenger_id = ? AND status = 'ACCEPTED' AND passenger_completed = FALSE ORDER BY timestamp DESC LIMIT 1";
    }

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Σφάλμα κατά την αναζήτηση ενεργής διαδρομής.");
    }

    return -1; // Δεν βρέθηκε διαδρομή
}

   



    // Getters
    public Location getStartLocation() { return startLocation; }
    public Location getEndLocation() { return endLocation; }
    public DriverHome getDriver() { return driver; }
    public String getId() { return id; }
}
