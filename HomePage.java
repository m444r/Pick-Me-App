package pickmeapp;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HomePage extends JFrame {
    private JMapViewer map;
    private boolean iconToggled = false;
    private JButton iconButton;
    private ImageIcon carIcon;
    private ImageIcon maleIcon;
    private String pickMode = "DRIVER"; // Αρχική τιμή για το PickMode

    public HomePage() {
        setTitle("PickMeApp Home Page");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        // Φόρτωσε το pickMode από τη βάση
        pickMode = fetchPickModeFromDB();
        iconToggled = pickMode.equalsIgnoreCase("PASSENGER");


        // Φορτώνει εικόνες από resources/icons
        carIcon = new ImageIcon(getClass().getResource("icons/car.png"));
        maleIcon = new ImageIcon(getClass().getResource("icons/male.png"));
        
        
        Image scaledCar = carIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        Image scaledMale = maleIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        
        carIcon = new ImageIcon(scaledCar);
        maleIcon = new ImageIcon(scaledMale);
        iconButton = new JButton(iconToggled ? maleIcon : carIcon);

        map = new JMapViewer();
        add(map, BorderLayout.CENTER);

        // Search Panel (bottom)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField addressField = new JTextField(30);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Διεύθυνση: "));
        searchPanel.add(addressField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.SOUTH);

        // Icon Button (top right) με εικόνες
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRightPanel.add(iconButton);
        add(topRightPanel, BorderLayout.NORTH);

        // Button action to toggle icon image and change PickMode
        iconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iconToggled = !iconToggled;
                iconButton.setIcon(iconToggled ? maleIcon : carIcon);
                
                // Ενημέρωση PickMode όταν αλλάζει το εικονίδιο
                pickMode = iconToggled ? "PASSENGER" : "DRIVER";
                
                // Ενημέρωση της βάσης ή της εφαρμογής με το νέο PickMode
                updatePickMode(pickMode);
            }
        });

        // Search action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address = addressField.getText();
                if (!address.isEmpty()) {
                    // Dummy location (e.g., Athens center) - replace with actual geocoding
                    Coordinate coord = new Coordinate(37.9838, 23.7275);
                    map.setDisplayPosition(coord, 12);
                    map.addMapMarker(new MapMarkerDot("Αναζήτηση", coord));
                }
            }
        });

        setVisible(true);
    }

    // Μέθοδος για να ενημερώσουμε το PickMode του χρήστη στη βάση
    private void updatePickMode(String pickMode) {
        String url = "jdbc:mysql://localhost:3306/pickmeapp";
        String dbUsername = "root";
        String dbPassword = "password"; // Βάλε το σωστό password για τη βάση σου
        String id = "2"; // Το ID του χρήστη, αυτό μπορεί να προέλθει από το login ή άλλες πηγές

        String sql = "UPDATE users SET pickMode = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pickMode);
            pstmt.setInt(2, Session.userId); // Παίρνεις το id από το Session

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("PickMode updated to " + pickMode);
            } else {
                System.out.println("User not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }


      
    }
    private String fetchPickModeFromDB() {
    String url = "jdbc:mysql://localhost:3306/pickmeapp";
    String dbUsername = "root";
    String dbPassword = "password";

    String sql = "SELECT pickMode FROM users WHERE id = ?";

    try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, Session.userId);

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getString("pickMode");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Σφάλμα κατά τη φόρτωση pickMode: " + e.getMessage());
    }

    return "DRIVER"; // Προεπιλογή σε περίπτωση λάθους
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePage());
    }
}
