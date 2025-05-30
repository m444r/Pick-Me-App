package pickmeapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login {
    public static void main(String[] args) {
        JFrame frame = new JFrame("User Login");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame);

        frame.setLocationRelativeTo(null); // Κέντρο οθόνης
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Email:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(10, 50, 80, 25);
        panel.add(passLabel);

        JPasswordField passText = new JPasswordField(20);
        passText.setBounds(100, 50, 165, 25);
        panel.add(passText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 90, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            String email = userText.getText();
            String password = new String(passText.getPassword());

            if (authenticateUser(email, password)) {
                frame.dispose(); // Κλείνει το login παράθυρο

                // Διάβασε το pickMode από το Session και άνοιξε το αντίστοιχο Home
                if (Session.pickMode.equalsIgnoreCase("DRIVER")) {
                    new DriverHome().setVisible(true);
                } else {
                   new PassengerHome().setVisible(true);
                }
            } 
            else {
                JOptionPane.showMessageDialog(panel, "Λανθασμένα στοιχεία.");
            }
        });

    }

    private static boolean authenticateUser(String email, String password) {
        String url = "jdbc:mysql://localhost:3306/pickmeapp";
        String dbUser = "root";
        String dbPassword = "password";

        String sql = "SELECT id, pickMode FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Βρέθηκε ο χρήστης
                int userId = rs.getInt("id");
                String pickMode = rs.getString("pickMode");

                Session.userId = userId;
                Session.username = email;
                Session.pickMode = pickMode;

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Σφάλμα βάσης: " + e.getMessage());
        }

        return false;
    }
}