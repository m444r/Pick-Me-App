package pickmeapp;

import javax.swing.*;

public class RegisterForm {
    public static void showForm() {
        JFrame frame = new JFrame("Register");
        frame.setSize(350, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        JLabel idLabel = new JLabel("Password:");
        idLabel.setBounds(10, 100, 80, 25);
        panel.add(idLabel);

        JTextField idText = new JTextField(20);
        idText.setBounds(100, 100, 200, 25);
        panel.add(idText);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(100, 150, 120, 25);
        panel.add(registerButton);
    }
}
