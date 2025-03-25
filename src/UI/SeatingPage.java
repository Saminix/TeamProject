package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class SeatingPage extends JPanel {
    // Color definitions
    private static final Color RESERVED_COLOR = Color.RED;
    private static final Color AVAILABLE_COLOR = Color.GREEN;
    private static final Color RESTRICTED_VIEW_COLOR = Color.GRAY;
    private static final Color COMPANION_COLOR = Color.YELLOW;
    private static final Color ACCESSIBILITY_COLOR = Color.MAGENTA;
    private static final Color GROUP_BOOKING_COLOR = Color.BLUE;

    public SeatingPage() {
        setLayout(new BorderLayout());

        // Load the seating layout image
        URL imageURL = getClass().getResource("/Resources/MainHall.png");
        ImageIcon seatingIcon = (imageURL != null)
                ? new ImageIcon(imageURL)
                : new ImageIcon("MainHall.png");
        JLabel imageLabel = new JLabel(seatingIcon);

        // Create legend panel
        JPanel legendPanel = createLegendPanel();

        // Add instructions
        JTextArea instructionsArea = new JTextArea(
                "Seating: Main Hall"
        );
        instructionsArea.setEditable(false);

        // Combine components
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(legendPanel, BorderLayout.NORTH);
        controlPanel.add(new JScrollPane(instructionsArea), BorderLayout.CENTER);

        add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Add key listener for seat color changes
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (Character.toLowerCase(e.getKeyChar())) {
                    case 'r': imageLabel.setBackground(RESERVED_COLOR); break;
                    case 'g': imageLabel.setBackground(AVAILABLE_COLOR); break;
                    case 'b': imageLabel.setBackground(GROUP_BOOKING_COLOR); break;
                    case 'y': imageLabel.setBackground(COMPANION_COLOR); break;
                    case 'm': imageLabel.setBackground(ACCESSIBILITY_COLOR); break;
                    case 'x': imageLabel.setBackground(RESTRICTED_VIEW_COLOR); break;
                }
            }
        });
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create color legend
        Color[] colors = {
                RESERVED_COLOR, AVAILABLE_COLOR, GROUP_BOOKING_COLOR,
                COMPANION_COLOR, ACCESSIBILITY_COLOR, RESTRICTED_VIEW_COLOR
        };
        String[] labels = {
                "Reserved", "Available", "Group Booking",
                "Companion", "Accessibility", "Restricted View"
        };

        for (int i = 0; i < colors.length; i++) {
            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(20, 20));
            colorBox.setBackground(colors[i]);

            JLabel colorLabel = new JLabel(labels[i]);

            JPanel colorEntry = new JPanel(new FlowLayout(FlowLayout.LEFT));
            colorEntry.add(colorBox);
            colorEntry.add(colorLabel);

            legendPanel.add(colorEntry);
        }

        return legendPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Seating Layout");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new SeatingPage());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}