package pickmeapp;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame {
    private JMapViewer map;
    private boolean iconToggled = false;
    private JButton iconButton;
    private ImageIcon carIcon;
    private ImageIcon maleIcon;

    public HomePage() {
        setTitle("PickMeApp Home Page");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Φορτώνει εικόνες από resources/icons
        carIcon = new ImageIcon(getClass().getResource("icons/car.png"));
        maleIcon = new ImageIcon(getClass().getResource("icons/male.png"));
        
        Image scaledCar = carIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        Image scaledMale = maleIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);

        // Νέες εικόνες με το μειωμένο μέγεθος
        carIcon = new ImageIcon(scaledCar);
        maleIcon = new ImageIcon(scaledMale);


        map = new JMapViewer();
        add(map, BorderLayout.CENTER);

        // Search Panel (bottom)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField addressField = new JTextField(30);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Διεύθυνση: "));
        searchPanel.add(addressField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.SOUTH);

        // Icon Button (top right) με εικόνες
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        iconButton = new JButton(carIcon);
        topRightPanel.add(iconButton);
        add(topRightPanel, BorderLayout.NORTH);

        // Button action to toggle icon image
        iconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iconToggled = !iconToggled;
                iconButton.setIcon(iconToggled ? maleIcon : carIcon);
            }
        });

        // Search action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address = addressField.getText();
                if (!address.isEmpty()) {
                    // Dummy location (e.g., Athens center) - replace with actual geocoding
                    Coordinate coord = new Coordinate(37.9838, 23.7275);
                    map.setDisplayPosition(coord, 12);
                    map.addMapMarker(new MapMarkerDot("Αναζήτηση", coord));
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePage());
    }
}
