
package pickmeapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.sql.*;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

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
    public static void showFavorites(int userId) {
    List<String> favoriteRoutes = Favorites.fetchFavoriteRoutesFromDB(userId);

    if (favoriteRoutes.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Δεν υπάρχουν αγαπημένες διαδρομές.");
        return;
    }

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    for (String route : favoriteRoutes) {
        JPanel routePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel routeLabel = new JLabel(route);
        JToggleButton favToggle = new JToggleButton(" ♡");
        favToggle.setToolTipText("Αφαίρεση από τα αγαπημένα");

        boolean isFavorite = Favorites.checkIfFavorite(userId, route);
        favToggle.setSelected(isFavorite);
        favToggle.setText(isFavorite ? "♥" : " ♡");

        favToggle.addActionListener(e -> {
            boolean selected = favToggle.isSelected();
            favToggle.setText(selected ? "♥" : " ♡");
            if (selected) {
                Favorites.addFavorite(userId, route);
            } else {
                Favorites.removeFavorite(userId, route);
            }
        });

        JButton selectButton = new JButton("Επιλογή Διαδρομής");
selectButton.addActionListener(e -> {
  

    String sql;
    if ("DRIVER".equalsIgnoreCase(Session.pickMode)) {
        sql = "INSERT INTO ride_requests (driver_id, pickup_address, address, timestamp) VALUES (?, ?, ?, NOW())";
    } else {
        sql = "INSERT INTO ride_requests (passenger_id, pickup_address, address, timestamp) VALUES (?, ?, ?, NOW())";
    }

   
    String[] addresses = route.split("->");
    
if (!route.contains("→")) {
    JOptionPane.showMessageDialog(null, "Η διαδρομή δεν περιέχει έγκυρες διευθύνσεις.");
    return;
}

String[] parts = route.split("→");
if (parts.length < 2) {
    JOptionPane.showMessageDialog(null, "Η διαδρομή δεν περιέχει έγκυρες διευθύνσεις.");
    return;
}

String pickupAddress = parts[0].replace("Από:", "").trim();
String destinationAddress = parts[1].replace("Προς:", "").trim();


   // String pickupAddress = addresses[0].trim();
    //String destinationAddress = addresses[1].trim();

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, Session.userId);
        stmt.setString(2, pickupAddress);
        stmt.setString(3, destinationAddress);

        int rows = stmt.executeUpdate();
        if (rows > 0) {
            JOptionPane.showMessageDialog(null, "✅ Το αίτημα διαδρομής καταχωρήθηκε με επιτυχία!");
        } else {
            JOptionPane.showMessageDialog(null, "⚠️ Αποτυχία καταχώρησης.");
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "❌ Σφάλμα κατά την αποθήκευση: " + ex.getMessage());
    }
});

        routePanel.add(routeLabel);
        routePanel.add(favToggle);
        routePanel.add(selectButton);
        panel.add(routePanel);
    }

    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setPreferredSize(new Dimension(450, 300));

    JOptionPane.showMessageDialog(null, scrollPane, "📌 Αγαπημένες Διαδρομές", JOptionPane.PLAIN_MESSAGE);
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
