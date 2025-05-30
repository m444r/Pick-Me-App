
package pickmeapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Favorites {
    
    public static void addFavorite(int userId, String route) {
    String sql = "INSERT INTO favorites (user_id, route_text) VALUES (?, ?)";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        stmt.setString(2, route);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
    public static void removeFavorite(int userId, String route) {
    String sql = "DELETE FROM favorites WHERE user_id = ? AND route_text = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        stmt.setString(2, route);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
    public static boolean checkIfFavorite(int userId, String route) {
    String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND route_text = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        stmt.setString(2, route);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
    
     public static List<String> fetchFavoriteRoutesFromDB(int userId) {
    List<String> favorites = new ArrayList<>();

    String sql = "SELECT route_text FROM favorites WHERE user_id = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            favorites.add(rs.getString("route_text"));
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "❌ Σφάλμα κατά την ανάκτηση αγαπημένων: " + e.getMessage());
    }

    return favorites;
}
}
