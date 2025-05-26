import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SearchRoutesFrame {
    private JFrame frame;
    private JPanel bottomPanel;
    private JTextField destinationField;
    private JButton searchButton;

    public SearchRoutesFrame() {
        frame = new JFrame("Search Routes");
        frame.setSize(400, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Main content panel (κεντρικό άδειο μέρος)
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(240, 240, 240));
        frame.add(contentPanel, BorderLayout.CENTER);
        
        //bottom panel lilac
        bottomPanel = new RoundedPanel(30);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(new Color(200, 162, 200));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //search bar
        destinationField = new RoundedTextField("Enter Destination", 20);
        destinationField.setPreferredSize(new Dimension(260, 50));
        destinationField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        destinationField.setMargin(new Insets(10, 10, 10, 10));
        destinationField.setBackground(Color.WHITE);


        searchButton = new JButton("Search Route");
        searchButton.setPreferredSize(new Dimension(140, 50));
        searchButton.setFocusPainted(false);
        searchButton.setBackground(new Color(66, 133, 244));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchButton.setBorder(new RoundedBorder(30));

        // Action button click
        searchButton.addActionListener(e -> {
            String destination = destinationField.getText();
            if (!destination.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Best route to: " + destination);
            }
        });

        // Συνδυασμός πεδίου + κουμπιού
        JPanel inputRow = new JPanel(new BorderLayout());
        inputRow.setBackground(bottomPanel.getBackground());
        inputRow.add(destinationField, BorderLayout.CENTER);
        inputRow.add(searchButton, BorderLayout.EAST);

        bottomPanel.add(inputRow, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Εσωτερική κλάση για στρογγυλό περίγραμμα
    private static class RoundedBorder implements javax.swing.border.Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SearchRoutesFrame::new);
    }
}
