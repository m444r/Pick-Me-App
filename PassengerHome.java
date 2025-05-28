package com.pickme.pickmeapp;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerHome extends javax.swing.JFrame {

    private JPanel requestListPanel;
    private JPanel matchingDriversPanel;
    private ImageIcon carIcon;
    private ImageIcon maleIcon;
    private javax.swing.JButton jButton1;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JToggleButton jToggleButton2;

    public PassengerHome() {
        initComponents();
        requestListPanel = new JPanel();
        requestListPanel.setLayout(new BoxLayout(requestListPanel, BoxLayout.Y_AXIS));

        matchingDriversPanel = new JPanel();
        matchingDriversPanel.setLayout(new BoxLayout(matchingDriversPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(matchingDriversPanel);
        scrollPane.setPreferredSize(new Dimension(300, 0));

        Container oldContent = getContentPane();
        Component[] oldComponents = oldContent.getComponents();

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GroupLayout(centerPanel));
        for (Component comp : oldComponents) {
            centerPanel.add(comp);
        }

        oldContent.removeAll();
        wrapperPanel.add(centerPanel, BorderLayout.CENTER);
        wrapperPanel.add(scrollPane, BorderLayout.EAST);
        setContentPane(wrapperPanel);

        setTitle("Passenger Home");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        carIcon = new ImageIcon(getClass().getResource("/pickmeapp/icons/car.png"));
        maleIcon = new ImageIcon(getClass().getResource("/pickmeapp/icons/male.png"));

        Image scaledCar = carIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image scaledMale = maleIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);

        carIcon = new ImageIcon(scaledCar);
        maleIcon = new ImageIcon(scaledMale);

        jToggleButton2.setIcon(maleIcon);

        setVisible(true);

        showUserRequests();
    }

    private void initComponents() {
        jButton1 = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();

        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String start = jTextField5.getText();
                String end = jTextField6.getText();

                if (start.isEmpty() || end.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Συμπλήρωσε και τα δύο πεδία.");
                } else {
                    searchForDrivers(start, end);
                }
            }
        });
    }

    private void searchForDrivers(String start, String end) {
        String sql = "SELECT u.id, u.name, r.start_location, r.end_location " +
                     "FROM routes r " +
                     "JOIN users u ON r.driver_id = u.id " +
                     "WHERE r.start_location LIKE ? AND r.end_location LIKE ?";

        matchingDriversPanel.removeAll();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + start + "%");
            stmt.setString(2, "%" + end + "%");

            ResultSet rs = stmt.executeQuery();

            boolean found = false;

            while (rs.next()) {
                int driverId = rs.getInt("id");
                String name = rs.getString("name");
                String from = rs.getString("start_location");
                String to = rs.getString("end_location");

                JPanel card = new JPanel(new GridLayout(0, 1));
                card.setBorder(BorderFactory.createTitledBorder("Οδηγός"));
                card.add(new JLabel("Ονοματεπώνυμο: " + name));
                card.add(new JLabel("Διαδρομή: " + from + " → " + to));

                JButton chooseBtn = new JButton("Επιλογή");
                chooseBtn.addActionListener(e -> {
                    sendRideRequest(driverId, start, end);
                    JOptionPane.showMessageDialog(this, "✅ Το αίτημα στάλθηκε στον οδηγό " + name);
                });
                card.add(chooseBtn);

                matchingDriversPanel.add(card);
                found = true;
            }

            if (!found) {
                matchingDriversPanel.add(new JLabel("❌ Δεν βρέθηκαν διαθέσιμοι οδηγοί."));
            }

            matchingDriversPanel.revalidate();
            matchingDriversPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Σφάλμα κατά την αναζήτηση οδηγών: " + e.getMessage());
        }
    }

    private void sendRideRequest(int driverId, String pickup, String dest) {
        String sql = "INSERT INTO ride_requests (passenger_id, driver_id, pickup_address, address, status) VALUES (?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.userId);
            stmt.setInt(2, driverId);
            stmt.setString(3, pickup);
            stmt.setString(4, dest);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showUserRequests() {
        // Υποθετική μέθοδος για εμφάνιση αιτημάτων - placeholder για επέκταση
    }
} 
