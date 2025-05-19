package pickmeapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DriverHome extends HomePage {

    private JPanel requestsPanel;

    public DriverHome() {
        super(); // κληρονομεί HomePage
        setTitle("Driver Home");

        requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(requestsPanel);
        add(scrollPane, BorderLayout.EAST);

        loadRideRequests();
    }

    private void loadRideRequests() {
        String sql = "SELECT rr.id, u.name AS passenger_name,"
                + " rr.address, rr.pickup_address, rr.status "
                + "FROM ride_requests rr JOIN users u ON rr.passenger_id = u.id "
                + "WHERE rr.driver_id = ? "
                + "AND rr.status = 'PENDING'";


        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int requestId = rs.getInt("id");
                String passengerName = rs.getString("passenger_name");
                String address = rs.getString("address");
                String pickup_address = rs.getString("pickup_address");

                JPanel requestCard = new JPanel();
                requestCard.setBorder(BorderFactory.createTitledBorder("Αίτημα"));
                requestCard.setLayout(new GridLayout(0, 1));
                requestCard.add(new JLabel("Επιβάτης: " + passengerName));
                requestCard.add(new JLabel("Διεύθυνση: " + address));
                requestCard.add(new JLabel("Διεύθυνση Επιβίβασης: " + pickup_address));
                

                JButton acceptBtn = new JButton("Αποδοχή");
                JButton rejectBtn = new JButton("Απόρριψη");

                acceptBtn.addActionListener(e -> updateRequestStatus(requestId, "ACCEPTED"));
                rejectBtn.addActionListener(e -> updateRequestStatus(requestId, "REJECTED"));

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(acceptBtn);
                buttonPanel.add(rejectBtn);
                requestCard.add(buttonPanel);

                requestsPanel.add(requestCard);
            }

            requestsPanel.revalidate();
            requestsPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Σφάλμα στη φόρτωση αιτημάτων: " + e.getMessage());
        }
    }

    private void updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE ride_requests SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Η κατάσταση ενημερώθηκε."  );
            requestsPanel.removeAll(); // καθάρισε και φόρτωσε ξανά
            loadRideRequests();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
