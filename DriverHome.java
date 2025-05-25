package pickmeapp;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DriverHome extends JFrame {

    private JPanel requestsPanel;
    private JMapViewer map;
    private JButton iconButton;
    private ImageIcon carIcon;
    private ImageIcon maleIcon;
    private boolean iconToggled;
    private JTextField addressField;

    public DriverHome() {
        setTitle("Driver Home");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Φόρτωση εικόνων
        carIcon = new ImageIcon(getClass().getResource("icons/car.png"));
        maleIcon = new ImageIcon(getClass().getResource("icons/male.png"));
        Image scaledCar = carIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        Image scaledMale = maleIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        carIcon = new ImageIcon(scaledCar);
        maleIcon = new ImageIcon(scaledMale);

        iconToggled = PickModeManager.fetchPickModeFromDB(Session.userId).equalsIgnoreCase("PASSENGER");
        iconButton = new JButton(iconToggled ? maleIcon : carIcon);

        // Χάρτης
        map = new JMapViewer();
        add(map, BorderLayout.CENTER);

        // Πάνω δεξιά - κουμπί εικονιδίου
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRightPanel.add(iconButton);
        add(topRightPanel, BorderLayout.NORTH);

        iconButton.addActionListener(e -> {
            PickModeManager.togglePickMode(this);
        });

        // Κάτω μέρος - Περιοχή αναζήτησης
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel pickupLabel = new JLabel("Διεύθυνση Επιβίβασης: ");
        JTextField pickupField = new JTextField(20);
        JLabel addressLabel = new JLabel("Προορισμός ");
        addressField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String address = addressField.getText();
            if (!address.isEmpty()) {
                Coordinate coord = new Coordinate(37.9838, 23.7275); // Placeholder
                map.setDisplayPosition(coord, 12);
                map.addMapMarker(new MapMarkerDot("Αναζήτηση", coord));
            }
        });

        searchPanel.add(pickupLabel);
        searchPanel.add(pickupField);
        searchPanel.add(addressLabel);
        searchPanel.add(addressField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.SOUTH);

        // Panel αιτημάτων
        requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(requestsPanel);
        scrollPane.setPreferredSize(new Dimension(350, getHeight()));
        add(scrollPane, BorderLayout.EAST);

        loadRideRequests();
        setVisible(true);
    }

    private void loadRideRequests() {
        String sql = "SELECT rr.id, u.name AS passenger_name, rr.address, rr.pickup_address " +
                     "FROM ride_requests rr " +
                     "JOIN users u ON rr.passenger_id = u.id " +
                     "WHERE rr.driver_id = ? AND rr.status = 'PENDING'";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.userId);
            ResultSet rs = stmt.executeQuery();

            requestsPanel.removeAll();

            while (rs.next()) {
                int requestId = rs.getInt("id");
                String passengerName = rs.getString("passenger_name");
                String address = rs.getString("address");
                String pickupAddress = rs.getString("pickup_address");

                JPanel requestCard = new JPanel();
                requestCard.setBorder(BorderFactory.createTitledBorder("Αίτημα"));
                requestCard.setLayout(new GridLayout(0, 1));
                requestCard.add(new JLabel("Επιβάτης: " + passengerName));
                requestCard.add(new JLabel("Διεύθυνση: " + address));
                requestCard.add(new JLabel("Επιβίβαση από: " + pickupAddress));

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

            JOptionPane.showMessageDialog(this, "Η κατάσταση ενημερώθηκε.");
            loadRideRequests();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
