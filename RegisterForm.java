package pickmeapp;

import javax.swing.*;
import java.sql.*;

public class RegisterForm {

    
    public static void main(String[] args) {
        
        showForm();
        
    }

    public static void showForm() {
        JFrame frame = new JFrame("Register");
        frame.setSize(350, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         frame.setLocationRelativeTo(null); // Κέντρο οθόνης
        frame.setVisible(true);
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(100, 20, 200, 25);
        panel.add(nameText);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 60, 80, 25);
        panel.add(emailLabel);

        JTextField emailText = new JTextField(20);
        emailText.setBounds(100, 60, 200, 25);
        panel.add(emailText);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(10, 100, 80, 25);
        panel.add(passLabel);

        JPasswordField passText = new JPasswordField(20);
        passText.setBounds(100, 100, 200, 25);
        panel.add(passText);

        JLabel modeLabel = new JLabel("Pick Mode:");
        modeLabel.setBounds(10, 140, 80, 25);
        panel.add(modeLabel);

        String[] modes = {"DRIVER", "PASSENGER"};
        JComboBox<String> modeBox = new JComboBox<>(modes);
        modeBox.setBounds(100, 140, 200, 25);
        panel.add(modeBox);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(100, 190, 120, 25);
        panel.add(registerButton);

        registerButton.addActionListener(e -> {
            String name = nameText.getText().trim();
            String email = emailText.getText().trim();
            String password = new String(passText.getPassword()).trim();
            String pickMode = (String) modeBox.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill all fields.");
                return;
            }

            boolean success = insertUser(name, email, password, pickMode);
            if (success) {
                JOptionPane.showMessageDialog(panel, "User registered!");
                nameText.setText("");
                emailText.setText("");
                passText.setText("");
                modeBox.setSelectedIndex(0);
            }
        });
    }

    private static boolean insertUser(String name, String email, String password, String pickMode) {
        String url = "jdbc:mysql://localhost:3306/pickmeapp?useSSL=false&serverTimezone=UTC";
        String user = "root"; // change if needed
        String pass = "password"; // change to your DB password

        String sql = "INSERT INTO users(name, email, password, pickMode) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure driver is loaded
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.setString(4, pickMode);

                pstmt.executeUpdate();
                return true;

            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
