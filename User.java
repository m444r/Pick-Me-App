package com.pickme.pickmeapp;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class User {
    private String name;
    private String email;
    private float rating;
    private boolean isVerified;
    private String profilePicture;
   // private List<Report> reports;
   // private List<Message> messages;
   // private List<Notification> notifications;
   // private List<Review> reviews;
    //private List<Payment> payments;
    private List<Login> logins;
   // private List<Authentication> authentications;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.logins = new ArrayList<>();
        //this.authentications = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

   /* public void register() throws RegistrationException {
        // Registration logic
    }*/

    public boolean login(String inputPassword) {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pickmeapp", "root", "password");
         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {

        stmt.setString(1, this.email);
        stmt.setString(2, inputPassword);

        ResultSet rs = stmt.executeQuery();
        return rs.next();

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    public void logout() {
    Session.userId = -1;
    Session.pickMode = null;
    new StartPage().setVisible(true);
    }


  /*  @Override
    public void rate(User user, float rating, String comment) throws RatingException {
        if (rating < 0 || rating > 5) {
            throw new RatingException("Rating must be between 0 and 5");
        }
        // Store rating logic
    }*/

    
}