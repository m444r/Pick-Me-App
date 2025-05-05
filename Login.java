package pickmeapp;

import javax.swing.*;

public class Login {
    public static void main(String[] args) {
        JFrame frame = new JFrame("User Login");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        JLabel idLabel = new JLabel("Password:");
        idLabel.setBounds(10, 50, 80, 25);
        panel.add(idLabel);

        JTextField idText = new JTextField(20);
        idText.setBounds(100, 50, 165, 25);
        panel.add(idText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 90, 80, 25);
        panel.add(loginButton);
    }
}
