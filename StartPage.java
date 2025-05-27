package pickmeapp;

import javax.swing.*;
import java.awt.event.*;

public class StartPage extends JFrame {

    public StartPage() {
        super("PickMeApp - Welcome");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        placeComponents(panel);

        setLocationRelativeTo(null); // για να βγει στο κέντρο
        setVisible(true);
    }

    private void placeComponents(JPanel panel) {

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
                dispose();  // κλείνουμε αυτό το JFrame (το StartPage)
            }
        });

        // Άνοιγμα Login Form
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Login.main(null);
                dispose();  // κλείνουμε αυτό το JFrame (το StartPage)
            }
        });
    }

    public static void main(String[] args) {
        new StartPage();  // Δημιουργούμε και εμφανίζουμε το παράθυρο
    }
}
