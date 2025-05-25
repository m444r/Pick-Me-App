package pickmeapp;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class PassengerHome extends JFrame {

    private JMapViewer map;
    private boolean iconToggled = true; // Passenger mode default
    private JButton iconButton;
    private ImageIcon carIcon;
    private ImageIcon maleIcon;
    private String pickMode = "PASSENGER";
    private JPanel requestListPanel; // Εδώ θα εμφανίζονται τα αιτήματα


    private JTextField addressField;
    private JButton searchButton;
    private JButton sendRequestButton;

    public PassengerHome() {
        setTitle("Passenger Home");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        requestListPanel = new JPanel();
        requestListPanel.setLayout(new BoxLayout(requestListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(requestListPanel);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        add(scrollPane, BorderLayout.EAST);

        carIcon = new ImageIcon(getClass().getResource("icons/car.png"));
        maleIcon = new ImageIcon(getClass().getResource("icons/male.png"));

        Image scaledCar = carIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        Image scaledMale = maleIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);

        carIcon = new ImageIcon(scaledCar);
        maleIcon = new ImageIcon(scaledMale);
        iconButton = new JButton(maleIcon); // Default is PASSENGER

        map = new JMapViewer();
        add(map, BorderLayout.CENTER);

        // Bottom Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel pickupLabel = new JLabel("Διεύθυνση Επιβίβασης: ");
        JTextField pickupField = new JTextField(20);

        JLabel addressLabel = new JLabel("Προορισμός");
        addressField = new JTextField(20);

        searchButton = new JButton("Search");
        sendRequestButton = new JButton("Στείλτε Αίτημα στον Οδηγό");

        searchPanel.add(pickupLabel);
        searchPanel.add(pickupField);
        searchPanel.add(addressLabel);
        searchPanel.add(addressField);
        searchPanel.add(searchButton);
        searchPanel.add(sendRequestButton);

        add(searchPanel, BorderLayout.SOUTH);

        // Icon button panel
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRightPanel.add(iconButton);
        add(topRightPanel, BorderLayout.NORTH);

        // Icon toggle (still present if needed)
                // Μέσα στον iconButton ActionListener
        iconButton.addActionListener(e -> {
        PickModeManager.togglePickMode(this);
        });



        // Search button
        searchButton.addActionListener(e -> {
            String address = addressField.getText();
            if (!address.isEmpty()) {
                Coordinate coord = new Coordinate(37.9838, 23.7275);
                map.setDisplayPosition(coord, 12);
                map.addMapMarker(new MapMarkerDot("Αναζήτηση", coord));
            }
        });

        // Send ride request
        sendRequestButton.addActionListener(e -> {
            String address = addressField.getText().trim();
            String pickupAddress = pickupField.getText().trim();

            if (address.isEmpty() || pickupAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Παρακαλώ εισάγετε διεύθυνση και διεύθυνση επιβίβασης.");
                return;
            }

            String driver_id = "2"; // Temporary
            sendRideRequest(address, driver_id, pickupAddress);

            JOptionPane.showMessageDialog(this, "Αίτημα στάλθηκε στον οδηγό με ID " + driver_id +
                    " για τη διεύθυνση: " + address + " και επιβίβαση από: " + pickupAddress);
        });

        setVisible(true);
        showUserRequests();
    }

    private void sendRideRequest(String address, String driver_id, String pickupAddress) {
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
    private void showUserRequests() {
    requestListPanel.removeAll();

    String sql = "SELECT id, address, pickup_address, status, timestamp FROM ride_requests WHERE passenger_id = ? ORDER BY timestamp DESC";

    String url = "jdbc:mysql://localhost:3306/pickmeapp";
    String dbUser = "root";
    String dbPassword = "password";
        
    try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, Session.userId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int requestId = rs.getInt("id");
            String pickup = rs.getString("pickup_address");
            String destination = rs.getString("address");
            String status = rs.getString("status");
            String time = rs.getString("timestamp");

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            itemPanel.setMaximumSize(new Dimension(280, 80));

            JLabel infoLabel = new JLabel("<html><b>ID:</b> " + requestId +
                    "<br><b>Από:</b> " + pickup +
                    "<br><b>Προς:</b> " + destination +
                    "<br><b>Κατάσταση:</b> " + status + "</html>");
            itemPanel.add(infoLabel, BorderLayout.CENTER);

            if (status.equalsIgnoreCase("PENDING")) {
                JButton cancelBtn = new JButton("Ακύρωση");
                cancelBtn.addActionListener(e -> {
                    cancelRequest(requestId);
                    showUserRequests(); // Refresh
                });
                itemPanel.add(cancelBtn, BorderLayout.EAST);
            }

            requestListPanel.add(itemPanel);
        }

        requestListPanel.revalidate();
        requestListPanel.repaint();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void cancelRequest(int requestId) {
    String sql = "UPDATE ride_requests SET status='CANCELLED' WHERE id = ? AND status = 'PENDING'";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, requestId);
        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


   

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PassengerHome());
    }
}
