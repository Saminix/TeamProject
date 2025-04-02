package SeatingLayouts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainHall extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color SEAT_COLOR = new Color(200, 220, 240);
    private static final Color SELECTED_COLOR = new Color(255, 200, 200);
    private static final Color STAGE_COLOR = new Color(180, 180, 180);
    private static final Color TEXT_COLOR = Color.BLACK;

    private JPanel mainPanel;
    private JButton[][] seats;
    private boolean[][] seatStatus;

    public MainHall() {
        setTitle("Main Hall Seating Plan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSeatingPlan(g);
            }
        };

        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(null); // Using absolute positioning for the complex layout

        // Initialize the seat arrays (these will be used in a more interactive version)
        seats = new JButton[30][60]; // Approximate size to cover all areas
        seatStatus = new boolean[30][60];

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);

        // Add some control buttons at the bottom for a real application
        JPanel controlPanel = new JPanel();
        JButton resetButton = new JButton("Reset Selection");
        JButton bookButton = new JButton("Book Selected Seats");

        controlPanel.add(resetButton);
        controlPanel.add(bookButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void drawSeatingPlan(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(TEXT_COLOR);

        int width = mainPanel.getWidth();
        int height = mainPanel.getHeight();

        // Set the dimensions for the seating area
        int centerX = width / 2;
        int marginTop = 50;
        int seatSize = 15;
        int seatGap = 5;

        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("MAIN HALL SEATING PLAN", centerX - 100, 30);

        // Draw the frame for the main seating area
        g2d.drawRect(180, 158, width - 310, height - 250);

        // -------- Top Balcony Section (Outside the box) --------
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("BALCONY", centerX - 30, marginTop);
        g2d.drawString("CC 1 2 3 4 5 6 7 8 CC", centerX - 25, marginTop + 20);

        // Draw row labels only
        g2d.drawString("BB", centerX - 190, marginTop + 40);
        g2d.drawString("BB", centerX + 190, marginTop + 40);

        // Draw top balcony seats
        int topRowY = marginTop + 50;

        // First row of seats (6-23)
        for (int i = 6; i <= 23; i++) {
            int xPos = centerX - 150 + (i - 6) * 15;
            g2d.setColor(SEAT_COLOR);
            g2d.fillRect(xPos, topRowY, 15, 15);
            g2d.setColor(TEXT_COLOR);
            g2d.drawRect(xPos, topRowY, 15, 15);
            g2d.drawString(String.valueOf(i), xPos + 2, topRowY + 12);
        }

        // Second row of balcony seats (directly below first row)
        g2d.drawString("BB", centerX - 150, topRowY + 30);
        for (int i = 6; i <= 23; i++) {
            int xPos = centerX - 150 + (i - 6) * 15;
            g2d.setColor(SEAT_COLOR);
            g2d.fillRect(xPos, topRowY + 30, 15, 15);
            g2d.setColor(TEXT_COLOR);
            g2d.drawRect(xPos, topRowY + 30, 15, 15);
            g2d.drawString(String.valueOf(i), xPos + 2, topRowY + 30 + 12);
        }
        g2d.drawString("BB", centerX + 120, topRowY + 30);

        // -------- Left Balcony Section (Outside the box) --------
        int leftX = 30; // Moved further left (outside the box)
        int leftY = 180;

        g2d.drawString("BALCONY", leftX - 10, leftY - 20);

        // Left balcony - Draw BB and AA sections
        // BB section seats - numbers 5 to 1
        g2d.drawString("BB", leftX - 10, leftY);
        for (int i = 0; i < 5; i++) {
            int seatNum = 5 - i;
            int yPos = leftY + 20 + i*20;

            g2d.setColor(SEAT_COLOR);
            g2d.fillRect(leftX, yPos, 15, 15);
            g2d.setColor(TEXT_COLOR);
            g2d.drawRect(leftX, yPos, 15, 15);
            g2d.drawString(String.valueOf(seatNum), leftX + 3, yPos + 12);
        }
        g2d.drawString("BB", leftX - 10, leftY + 120);

        // AA section seats - numbers 20 to 1
        g2d.drawString("AA", leftX + 20, leftY);
        for (int i = 0; i < 20; i++) {
            int seatNum = 20 - i;
            int yPos = leftY + 20 + i*20;

            g2d.setColor(SEAT_COLOR);
            g2d.fillRect(leftX + 30, yPos, 15, 15);
            g2d.setColor(TEXT_COLOR);
            g2d.drawRect(leftX + 30, yPos, 15, 15);
            g2d.drawString(String.valueOf(seatNum), leftX + 33, yPos + 12);
        }
        g2d.drawString("AA", leftX + 20, leftY + 420);

        // -------- Right Balcony Section (Outside the box) --------
        int rightX = width - 18; // Moved further right (outside the box)
        int rightY = 180;

        g2d.drawString("BALCONY", rightX - 30, rightY - 20);

        // Right balcony
        // AA section seats - numbers 34 to 53
        g2d.drawString("AA", rightX - 50, rightY);
        for (int i = 0; i < 20; i++) {
            int seatNum = 34 + i;
            int yPos = rightY + 20 + i*20;

            g2d.setColor(SEAT_COLOR);
            g2d.fillRect(rightX - 45, yPos, 15, 15);
            g2d.setColor(TEXT_COLOR);
            g2d.drawRect(rightX - 45, yPos, 15, 15);
            g2d.drawString(String.valueOf(seatNum), rightX - 42, yPos + 12);
        }
        g2d.drawString("AA", rightX - 50, rightY + 420);

        // BB section seats - numbers 24 to 29
        g2d.drawString("BB", rightX - 20, rightY);
        for (int i = 0; i < 6; i++) {
            int seatNum = 24 + i;
            int yPos = rightY + 20 + i*20;

            g2d.setColor(SEAT_COLOR);
            g2d.fillRect(rightX - 15, yPos, 15, 15);
            g2d.setColor(TEXT_COLOR);
            g2d.drawRect(rightX - 15, yPos, 15, 15);
            g2d.drawString(String.valueOf(seatNum), rightX - 12, yPos + 12);
        }
        g2d.drawString("BB", rightX - 20, rightY + 140);

        // -------- Center Stalls Section --------
        g2d.drawString("STALLS", centerX - 100, 180);
        g2d.drawString("STALLS", centerX + 70, 180);

        // Draw central rows
        int startY = 200;
        int rowHeight = 20;

        // Define the rows with their labels and seat ranges
        drawMainRow(g2d, "Q", "1 2 3 4 5 6 7 8 9 10 Q", startY + rowHeight * 0, centerX);
        drawMainRow(g2d, "P", "1 2 3 4 5 6 7 8 9 10 11 P", startY + rowHeight * 1, centerX);
        drawMainRow(g2d, "O", "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 | 17 18 19 20 O", startY + rowHeight * 2, centerX);

        drawMainRow(g2d, "N", "1 2 3", startY + rowHeight * 3, centerX - 80);
        drawMainRow(g2d, "", "4 5 6 7 8 9 10 11 12 13 14 15 16", startY + rowHeight * 3 + 5, centerX);

        // For rows N through A, align seat numbers 1-2 and 17-18 ahead of row labels

        // N row
        drawMainRow(g2d, "", "1 2 3", startY + rowHeight * 3, centerX - 80);
        drawMainRow(g2d, "", "4 5 6 7 8 9 10 11 12 13 14 15 16", startY + rowHeight * 3 + 5, centerX);
        drawMainRow(g2d, "N", "17 18 19", startY + rowHeight * 3, width - 200);

        // M row
        drawMainRow(g2d, "", "1 2", startY + rowHeight * 4, centerX - 100);
        drawMainRow(g2d, "", "4 5 6 7 8 9 10 11 12 13 14 15 16", startY + rowHeight * 4 + 5, centerX);
        drawMainRow(g2d, "M", "17 18", startY + rowHeight * 4, width - 200);

        // Draw rows L through A with their respective seat numbers
        String[] rowLabels = {"L", "K", "J", "H", "G", "F", "E", "D", "C", "B", "A"};
        for (int i = 0; i < rowLabels.length; i++) {
            int rowY = startY + rowHeight * (5 + i);

            // Left section with seats 1-2
            drawMainRow(g2d, "", "1 2", rowY, centerX - 100);

            // Middle section with seats 4-16
            drawMainRow(g2d, "", "4 5 6 7 8 9 10 11 12 13 14 15 16", rowY + 5, centerX);

            // Right section with seats 17-18 and row label
            drawMainRow(g2d, rowLabels[i], "17 18", rowY, width - 200);
        }

        // Draw the stage area
        g2d.setColor(STAGE_COLOR);
        g2d.fillRect(centerX - 150, height - 150, 200, 50);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("STAGE", centerX - 25, height - 120);
    }

    private void drawMainRow(Graphics2D g2d, String rowLabel, String seatsText, int y, int centerX) {
        if (!rowLabel.isEmpty()) {
            g2d.drawString(rowLabel, centerX - 120, y + 12);
        }

        if (!seatsText.isEmpty()) {
            String[] seats = seatsText.split(" ");
            int startX = centerX - 80;

            for (String seat : seats) {
                if (seat.equals("|")) {
                    // This is just a divider, move on
                    startX += 10;
                    continue;
                }

                if (isNumeric(seat)) {
                    // Draw this as a seat
                    g2d.setColor(SEAT_COLOR);
                    g2d.fillRect(startX, y, 15, 15);
                    g2d.setColor(TEXT_COLOR);
                    g2d.drawRect(startX, y, 15, 15);
                    g2d.drawString(seat, startX + (seat.length() == 1 ? 5 : 2), y + 12);
                } else if (!seat.equals(rowLabel)) {
                    // Just draw the text if it's not the row label again
                    g2d.drawString(seat, startX, y + 12);
                }
                startX += 18;
            }
        }
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainHall hall = new MainHall();
            hall.setVisible(true);
        });
    }
}