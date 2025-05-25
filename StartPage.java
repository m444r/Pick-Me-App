package pickmeapp;

import javax.swing.*;
import java.awt.event.*;

public class StartPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("PickMeApp - Welcome");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(50, 20, 180, 30);
        panel.add(registerButton);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(50, 60, 180, 30);
        panel.add(loginButton);

        // Άνοιγμα Register Form
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RegisterForm.showForm();
            }
        });

        // Άνοιγμα Login Form
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Login.main(null);
            }
        });
    }
}
