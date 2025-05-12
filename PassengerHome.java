package pickmeapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;


public class PassengerHome extends HomePage {

    private JButton sendRequestButton;

    public PassengerHome() {
        super(); // Καλεί τον constructor της HomePage
        setTitle("Passenger Home");

        // Δημιουργία κουμπιού και προσθήκη στο υπάρχον searchPanel της HomePage
        sendRequestButton = new JButton("Στείλτε Αίτημα στον Οδηγό");
        searchButton.getParent().add(sendRequestButton); // Προσθέτει δίπλα στο κουμπί "Search"
        sendRequestButton.addActionListener(e -> sendRideRequest());
    }

    private void sendRideRequest() {
        String address = addressField.getText().trim();

        // ⚠️ Αντί για νέο πεδίο, βρίσκουμε το pickupField από το υπάρχον panel
        JPanel searchPanel = (JPanel) searchButton.getParent();
        JTextField pickupField = null;

        for (Component comp : searchPanel.getComponents()) {
            if (comp instanceof JTextField && comp != addressField) {
                pickupField = (JTextField) comp;
                break;
            }
        }

        if (pickupField == null) {
            JOptionPane.showMessageDialog(this, "Δεν βρέθηκε πεδίο επιβίβασης.");
            return;
        }

        String pickupAddress = pickupField.getText().trim();

        if (address.isEmpty() || pickupAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ εισάγετε διεύθυνση και διεύθυνση επιβίβασης.");
            return;
        }

        String driver_id = "2";
        sendRideRequestToDB(address, driver_id, pickupAddress);

        JOptionPane.showMessageDialog(this, "Αίτημα στάλθηκε στον οδηγό με ID " + driver_id +
                " για τη διεύθυνση: " + address + " και επιβίβαση από: " + pickupAddress);
    }

    private void sendRideRequestToDB(String address, String driver_id, String pickupAddress) {
        String sql = "INSERT INTO ride_requests (passenger_id, driver_id, address, pickup_address, status) VALUES (?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.userId);
            stmt.setInt(2, Integer.parseInt(driver_id));
            stmt.setString(3, address);
            stmt.setString(4, pickupAddress);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
