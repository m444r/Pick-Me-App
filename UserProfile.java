package com.pickme.pickmeapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserProfile extends javax.swing.JFrame {

    private void loadProfil() {
    String sql = "SELECT name, email FROM users WHERE id = ?";
    String vehicleSql = "SELECT brand, model, color, license_plate, fuel_type, total_seats, ac, smoking_allowed FROM vehicle WHERE driver_id = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement(sql);
         PreparedStatement vstmt = conn.prepareStatement(vehicleSql)) {

        stmt.setInt(1, Session.userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            jTextField2.setText(rs.getString("name"));
            jTextField3.setText(rs.getString("email"));
        }

        vstmt.setInt(1, Session.userId);
        ResultSet vrs = vstmt.executeQuery();

        if (vrs.next()) {
            VehicleBrand.setText(vrs.getString("brand"));
            VehicleModel.setText(vrs.getString("model"));
            VehicleColor.setText(vrs.getString("color"));
            VehiclePlate.setText(vrs.getString("license_plate"));
            VehicleFuelType.setText(vrs.getString("fuel_type"));
            VehicleTotalSeats.setText(String.valueOf(vrs.getInt("total_seats")));
            ACCheckbox.setSelected(vrs.getBoolean("ac"));
            SmokingAllowedCheckbox.setSelected(vrs.getBoolean("smoking_allowed"));
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Σφάλμα κατά την ανάκτηση στοιχείων.");
    }
}


    private void updateProfile() {
    String newName = jTextField2.getText().trim();
    String newEmail = jTextField3.getText().trim();

    String brand = VehicleBrand.getText().trim();
    String model = VehicleModel.getText().trim();
    String color = VehicleColor.getText().trim();
    String licensePlate = VehiclePlate.getText().trim();
    String fuelType = VehicleFuelType.getText().trim();
    int totalSeats = Integer.parseInt(VehicleTotalSeats.getText().trim());
    boolean ac = ACCheckbox.isSelected();
    boolean smokingAllowed = SmokingAllowedCheckbox.isSelected();

    if (newName.isEmpty() || newEmail.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Το όνομα και το email δεν μπορούν να είναι κενά.");
        return;
    }

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password")) {

        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, newName);
        stmt.setString(2, newEmail);
        stmt.setInt(3, Session.userId);
        stmt.executeUpdate();

        String checkSql = "SELECT * FROM vehicle WHERE driver_id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setInt(1, Session.userId);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {

            String updateVehicle = "UPDATE vehicle SET brand = ?, model = ?, color = ?, license_plate = ?, fuel_type = ?, total_seats = ?, ac = ?, smoking_allowed = ? WHERE driver_id = ?";
            PreparedStatement vstmt = conn.prepareStatement(updateVehicle);
            vstmt.setString(1, brand);
            vstmt.setString(2, model);
            vstmt.setString(3, color);
            vstmt.setString(4, licensePlate);
            vstmt.setString(5, fuelType);
            vstmt.setInt(6, totalSeats);
            vstmt.setBoolean(7, ac);
            vstmt.setBoolean(8, smokingAllowed);
            vstmt.setInt(9, Session.userId);
            vstmt.executeUpdate();
        } else {

            String insertVehicle = "INSERT INTO vehicle (driver_id, brand, model, color, license_plate, fuel_type, total_seats, ac, smoking_allowed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement vstmt = conn.prepareStatement(insertVehicle);
            vstmt.setInt(1, Session.userId);
            vstmt.setString(2, brand);
            vstmt.setString(3, model);
            vstmt.setString(4, color);
            vstmt.setString(5, licensePlate);
            vstmt.setString(6, fuelType);
            vstmt.setInt(7, totalSeats);
            vstmt.setBoolean(8, ac);
            vstmt.setBoolean(9, smokingAllowed);
            vstmt.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Το προφίλ ενημερώθηκε με επιτυχία.");

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Σφάλμα κατά την ενημέρωση προφίλ.");
    }
}


    public UserProfile() {
        initComponents();
        loadProfil();
        
        if (!"DRIVER".equalsIgnoreCase(Session.pickMode)) {
        // ΟΧΙ οδηγός -> κρύψε όλα τα στοιχεία του οχήματος
        VehicleBrand.setVisible(false);
        VehicleModel.setVisible(false);
        VehicleColor.setVisible(false);
        VehiclePlate.setVisible(false);
        VehicleFuelType.setVisible(false);
        VehicleTotalSeats.setVisible(false);
        ACCheckbox.setVisible(false);
        SmokingAllowedCheckbox.setVisible(false);

        // Και τα labels τους, αν έχουν ονόματα
        labelBrand.setVisible(false);
        labelModel.setVisible(false);
        labelColor.setVisible(false);
        labelPlate.setVisible(false);
        labelFuelType.setVisible(false);
        labelSeats.setVisible(false);
        labelAC.setVisible(false);
        labelSmoking.setVisible(false);
    }

        
        setTitle("Driver Profile");
        setSize(370, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Σελίδα προφίλ οδηγού");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);

        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField4 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        Name = new javax.swing.JLabel();
        Email = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        labelBrand = new javax.swing.JLabel();
        labelModel = new javax.swing.JLabel();
        labelColor = new javax.swing.JLabel();
        labelPlate = new javax.swing.JLabel();
        VehicleBrand = new javax.swing.JTextField();
        VehicleModel = new javax.swing.JTextField();
        VehicleColor = new javax.swing.JTextField();
        VehiclePlate = new javax.swing.JTextField();
        labelAC = new javax.swing.JTextField();
        ACCheckbox = new javax.swing.JCheckBox();
        labelSmoking = new javax.swing.JTextField();
        SmokingAllowedCheckbox = new javax.swing.JCheckBox();
        VehicleFuelType = new javax.swing.JTextField();
        labelFuelType = new javax.swing.JLabel();
        labelSeats = new javax.swing.JLabel();
        VehicleTotalSeats = new javax.swing.JTextField();

        jTextField4.setText("jTextField4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pickmeapp/icons/Basic_Elements_(161).png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pickmeapp/icons/heart-938313_640.png"))); // NOI18N

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pickmeapp/icons/images.png"))); // NOI18N

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pickmeapp/icons/gray-user-profile-icon-png-fP8Q1P.png"))); // NOI18N

        Name.setText("Name :");

        Email.setText("Email :");

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 51, 51));
        jButton5.setText("Αποσυνδεση");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe Script", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ACCOUNT CENTER");

        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLabel2.setText("Vehicle Details");

        labelBrand.setText("Brand:");

        labelModel.setText("Model:");

        labelColor.setText("Color:");

        labelPlate.setText("Plate Number:");

        VehicleBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VehicleBrandActionPerformed(evt);
            }
        });

        VehicleModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VehicleModelActionPerformed(evt);
            }
        });

        VehicleColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VehicleColorActionPerformed(evt);
            }
        });

        VehiclePlate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VehiclePlateActionPerformed(evt);
            }
        });

        labelAC.setText("AC ");
        labelAC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelACActionPerformed(evt);
            }
        });

        ACCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ACCheckboxActionPerformed(evt);
            }
        });

        labelSmoking.setText("Smoking");
        labelSmoking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelSmokingActionPerformed(evt);
            }
        });

        labelFuelType.setText("Fuel Type:");

        labelSeats.setText("Total Seats:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, 0)
                        .addComponent(jButton2)
                        .addGap(0, 0, 0)
                        .addComponent(jButton3)
                        .addGap(0, 0, 0)
                        .addComponent(jButton4)))
                .addGap(0, 18, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(labelModel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(labelBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(labelColor, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(69, 69, 69)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(VehicleBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(VehicleColor, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(VehicleModel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addComponent(labelAC, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(ACCheckbox)
                                            .addGap(59, 59, 59)
                                            .addComponent(labelSmoking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                                            .addComponent(SmokingAllowedCheckbox))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(labelPlate, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(labelFuelType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGap(37, 37, 37)))
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(VehiclePlate)
                                                .addComponent(VehicleFuelType)
                                                .addComponent(VehicleTotalSeats, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Email, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Name))
                                        .addGap(57, 57, 57)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(74, 74, 74)
                                .addComponent(jLabel2))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelSeats, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Name)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Email)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(VehicleBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelBrand))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelModel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(VehicleModel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelColor)
                    .addComponent(VehicleColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPlate)
                    .addComponent(VehiclePlate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(VehicleFuelType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelFuelType))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSeats)
                    .addComponent(VehicleTotalSeats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ACCheckbox)
                    .addComponent(labelSmoking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SmokingAllowedCheckbox))
                .addGap(74, 74, 74)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton4)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new DriverHome(); // Ξανανοίγει την αρχική σελίδα
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
       
        
        updateProfile();
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        updateProfile();
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        logout();
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void VehicleBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VehicleBrandActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_VehicleBrandActionPerformed

    private void VehiclePlateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VehiclePlateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_VehiclePlateActionPerformed

    private void labelACActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelACActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_labelACActionPerformed

    private void VehicleColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VehicleColorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_VehicleColorActionPerformed

    private void VehicleModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VehicleModelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_VehicleModelActionPerformed

    private void labelSmokingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelSmokingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_labelSmokingActionPerformed

    private void ACCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ACCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ACCheckboxActionPerformed

    /**
     * @param args the command line arguments
     */
    

    
    public void logout() {
        new StartPage(); // Ξανανοίγει την αρχική σελίδα
        
    }



    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserProfile().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ACCheckbox;
    private javax.swing.JLabel Email;
    private javax.swing.JLabel Name;
    private javax.swing.JCheckBox SmokingAllowedCheckbox;
    private javax.swing.JTextField VehicleBrand;
    private javax.swing.JTextField VehicleColor;
    private javax.swing.JTextField VehicleFuelType;
    private javax.swing.JTextField VehicleModel;
    private javax.swing.JTextField VehiclePlate;
    private javax.swing.JTextField VehicleTotalSeats;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField labelAC;
    private javax.swing.JLabel labelBrand;
    private javax.swing.JLabel labelColor;
    private javax.swing.JLabel labelFuelType;
    private javax.swing.JLabel labelModel;
    private javax.swing.JLabel labelPlate;
    private javax.swing.JLabel labelSeats;
    private javax.swing.JTextField labelSmoking;
    // End of variables declaration//GEN-END:variables
}
