package pickmeapp;

import javax.swing.*;
import java.sql.*;

public class PickModeManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/pickmeapp";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";

    public static void updatePickMode(int userId, String pickMode) {
        String sql = "UPDATE users SET pickMode = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pickMode);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("PickMode updated to " + pickMode);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Σφάλμα στην ενημέρωση pickMode: " + e.getMessage());
        }
    }

    public static String fetchPickModeFromDB(int userId) {
        String sql = "SELECT pickMode FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("pickMode");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Σφάλμα κατά τη φόρτωση pickMode: " + e.getMessage());
        }

        return "PASSANGER"; // Προεπιλογή
    }
    
    public static void togglePickMode(JFrame currentFrame) {
        int userId = Session.userId;
        String currentMode = fetchPickModeFromDB(userId);
        String newMode = currentMode.equalsIgnoreCase("PASSENGER") ? "DRIVER" : "PASSENGER";
        updatePickMode(userId, newMode);

        currentFrame.dispose();

        if (newMode.equals("PASSENGER")) {
            new PassengerHome();
        } else {
            new DriverHome();
        }
    }
}
