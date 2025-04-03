package SeatingLayouts;

import javax.swing.*;
import java.awt.*;

public class SmallHall extends JFrame {

    public SmallHall() {
        // Set up the JFrame
        setTitle("SmallHall Seating Chart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE); // White background for the frame

        // Main panel with a modern look
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE); // White background
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(33, 33, 33), 2)); // Darker border

        // Sound Desk (top-right, modern dark gray box)
        JPanel soundDeskPanel = new JPanel(new BorderLayout());
        JLabel soundDeskLabel = new JLabel("SOUND DESK", SwingConstants.CENTER);
        soundDeskLabel.setFont(new Font("Arial", Font.BOLD, 14));
        soundDeskLabel.setForeground(Color.WHITE);
        soundDeskLabel.setBackground(new Color(50, 50, 50)); // Modern dark gray
        soundDeskLabel.setOpaque(true);
        soundDeskLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding

        // Align sound desk to the right
        JPanel soundDeskContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        soundDeskContainer.setBackground(Color.WHITE);
        soundDeskContainer.add(soundDeskLabel);
        soundDeskPanel.add(soundDeskContainer, BorderLayout.CENTER);
        soundDeskPanel.setBackground(Color.WHITE);
        mainPanel.add(soundDeskPanel, BorderLayout.NORTH);

        // Create a panel for both the aisle label and seating
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // Create the left panel for the aisle and entrance
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        leftPanel.setBackground(Color.WHITE);

        // Add entrance at the top
        JPanel entrancePanel = new JPanel();
        entrancePanel.setLayout(new BoxLayout(entrancePanel, BoxLayout.Y_AXIS));
        entrancePanel.setBackground(Color.WHITE);

        // Add arrow pointing up
        JLabel arrowLabel = new JLabel("↑");
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 18));
        arrowLabel.setForeground(new Color(33, 150, 243)); // Modern blue
        arrowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add "ENTRANCE" text
        JLabel entranceLabel = new JLabel("ENTRANCE");
        entranceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        entranceLabel.setForeground(new Color(33, 33, 33)); // Dark gray text
        entranceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        entrancePanel.add(arrowLabel);
        entrancePanel.add(entranceLabel);
        entrancePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(entrancePanel);

        // Add space between entrance and aisle text
        leftPanel.add(Box.createVerticalGlue());

        // Add "AISLE" text (rotated vertically)
        JPanel aisleTextPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(new Color(33, 150, 243)); // Modern blue
                g2d.rotate(Math.PI / 2, getWidth() / 2, getHeight() / 2);
                g2d.drawString("AISLE", getWidth() / 2 - 25, getHeight() / 2 + 5);
                g2d.dispose();
            }
        };
        aisleTextPanel.setPreferredSize(new Dimension(30, 150));
        aisleTextPanel.setBackground(Color.WHITE);
        aisleTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(aisleTextPanel);

        // Add more space at the bottom
        leftPanel.add(Box.createVerticalGlue());

        centerPanel.add(leftPanel, BorderLayout.WEST);

        // Stalls Section (with aisle on the left)
        JPanel stallsPanel = new JPanel();
        stallsPanel.setLayout(new BoxLayout(stallsPanel, BoxLayout.PAGE_AXIS));
        stallsPanel.setBackground(Color.WHITE);

        // Stalls rows (N to A, excluding I)
        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new GridLayout(13, 1, 2, 2)); // Slightly larger gaps
        rowsPanel.setBackground(Color.WHITE);

        // Row N (seats 1-4, shifted one space to the left)
        JPanel rowN = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        rowN.setBackground(Color.WHITE);
        rowN.add(Box.createHorizontalStrut(20)); // Reduced spacing to shift left
        JLabel rowNLabel = new JLabel("N");
        rowNLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rowNLabel.setForeground(new Color(33, 33, 33));
        rowN.add(rowNLabel);
        for (int i = 1; i <= 4; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201)); // Light green for seats
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rowN.add(seat);
        }
        rowsPanel.add(rowN);

        // Row M (seats 1-4)
        JPanel rowM = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        rowM.setBackground(Color.WHITE);
        rowM.add(Box.createHorizontalStrut(45)); // Normal spacing for aisle
        JLabel rowMLabel = new JLabel("M");
        rowMLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rowMLabel.setForeground(new Color(33, 33, 33));
        rowM.add(rowMLabel);
        for (int i = 1; i <= 4; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201)); // Light green for seats
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rowM.add(seat);
        }
        rowsPanel.add(rowM);

        // Rows L to A (seats 1-7, excluding I)
        char[] rowsToInclude = {'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        for (char rowLetter : rowsToInclude) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
            row.setBackground(Color.WHITE);
            row.add(Box.createHorizontalStrut(45)); // Normal spacing for aisle
            JLabel rowLabel = new JLabel("" + rowLetter);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            rowLabel.setForeground(new Color(33, 33, 33));
            row.add(rowLabel);
            for (int i = 1; i <= 7; i++) {
                JButton seat = new JButton(String.valueOf(i));
                seat.setPreferredSize(new Dimension(28, 22));
                seat.setMargin(new Insets(0, 0, 0, 0));
                seat.setFont(new Font("Arial", Font.PLAIN, 10));
                seat.setBackground(new Color(200, 230, 201)); // Light green for seats
                seat.setForeground(new Color(33, 33, 33));
                seat.setFocusPainted(false);
                seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
                row.add(seat);
            }
            rowsPanel.add(row);
        }

        stallsPanel.add(rowsPanel);
        centerPanel.add(stallsPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Use a JScrollPane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // No border for scroll pane

        // Add everything to the frame
        add(scrollPane, BorderLayout.CENTER);

        // Stage Label (at the bottom)
        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBackground(Color.WHITE);
        JLabel stageLabel = new JLabel("STAGE", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Arial", Font.BOLD, 22));
        stageLabel.setForeground(Color.WHITE);
        stageLabel.setBackground(new Color(62, 116, 70)); // Modern blue
        stageLabel.setOpaque(true);
        stageLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Padding

        // Adjust stage width to match the seating width
        stagePanel.add(stageLabel, BorderLayout.CENTER);
        mainPanel.add(stagePanel, BorderLayout.SOUTH);

        // Set frame size and visibility
        pack(); // Pack to fit contents
        setSize(400, 500); // Size for SmallHall
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmallHall());
    }
}