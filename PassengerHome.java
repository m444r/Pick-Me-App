package com.pickme.pickmeapp;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerHome extends javax.swing.JFrame {

    private Passenger passenger;
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
            Location startLoc = new Location(0, 0, start, "", "");
            Location endLoc = new Location(0, 0, end, "", "");
            List<Route> routes = passenger.searchRoutes(startLoc, endLoc);
            showRoutes(routes);
        }
    }
});

    }

private void showRoutes(List<Route> routes) {
    matchingDriversPanel.removeAll();

    if (routes.isEmpty()) {
        matchingDriversPanel.add(new JLabel("Δεν βρέθηκαν διαθέσιμες διαδρομές."));
    } else {
        for (Route route : routes) {
            String from = route.getStartLocation().formatAddress();
            String to = route.getEndLocation().formatAddress();
            String driverName = route.getDriver().getName(); 

            JPanel card = new JPanel(new GridLayout(0, 1));
            card.setBorder(BorderFactory.createTitledBorder("Οδηγός"));
            card.add(new JLabel("Ονοματεπώνυμο: " + driverName));
            card.add(new JLabel("Διαδρομή: " + from + " → " + to));

            JButton chooseBtn = new JButton("Επιλογή");
            chooseBtn.addActionListener(e -> {
                RideRequest request = passenger.sendRideRequest(route);
                JOptionPane.showMessageDialog(this, "Το αίτημα στάλθηκε στον οδηγό " + driverName);
            });

            card.add(chooseBtn);
            matchingDriversPanel.add(card);
        }
    }

    matchingDriversPanel.revalidate();
    matchingDriversPanel.repaint();
}


    private void showUserRequests() {
        // Υποθετική μέθοδος για εμφάνιση αιτημάτων - placeholder για επέκταση
    }
}
