
package pickmeapp;

import javax.swing.JOptionPane;
import java.sql.*; // αν κάνεις χρήση βάσης δεδομένων

public class Review {
    
   public static void saveRating(int currentUserId, int rideId, int rating) {
    String sql = "INSERT INTO ratings (ride_request_id, rater_id, rated_id, rating) VALUES (?, ?, ?, ?)";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password")) {

        // 🔁 Εντοπίζουμε ποιον θα βαθμολογήσει (αν ο τρέχων είναι οδηγός => βαθμολογεί επιβάτη, αλλιώς το αντίθετο)
        String query;
        if (Session.pickMode.equalsIgnoreCase("driver")) {
            query = "SELECT passenger_id FROM ride_requests WHERE id = ?";
        } else {
            query = "SELECT driver_id FROM ride_requests WHERE id = ?";
        }

        int ratedId = -1;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rideId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ratedId = rs.getInt(1);
            }
        }

        // Αποθήκευση αξιολόγησης
        if (ratedId != -1) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, rideId);
                stmt.setInt(2, currentUserId);
                stmt.setInt(3, ratedId);
                stmt.setInt(4, rating);
                stmt.executeUpdate();
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "❌ Σφάλμα κατά την αποθήκευση αξιολόγησης: " + e.getMessage());
    }
}

public static double getAverageRatingForUser(int userId) {
    String sql = "SELECT AVG(rating) AS avg_rating FROM ratings WHERE rated_id = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getDouble("avg_rating");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0.0; // Αν δεν υπάρχει βαθμολογία
}

    
}
