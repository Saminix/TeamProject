package UI;

import db.dbConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SeatingPage extends JPanel {
    private JTabbedPane tabbedPane;
    private JPanel mainHallPanel;
    private JPanel smallHallPanel;
    private JComboBox<String> showComboBox;
    private JButton saveButton;
    private Connection connection;
    private Map<String, JLabel> seatButtons;
    private Map<String, String> seatStatuses;
    private int selectedShowId;
    private int selectedVenueId;

    private static final Color AVAILABLE_COLOR = new Color(200, 230, 201); // Light green
    private static final Color OCCUPIED_COLOR = new Color(239, 83, 80);    // Modern red
    private static final Color RESTRICTED_COLOR = new Color(66, 66, 66);  // Dark gray
    private static final Color COMPANION_COLOR = new Color(171, 71, 188); // Modern purple
    private static final Color DISABILITY_COLOR = new Color(255, 202, 40); // Bright yellow
    private static final Color DISCOUNTED_COLOR = new Color(255, 165, 0);  // Orange
    private static final Color DARK_GREEN = new Color(0, 100, 0);         // Dark green for button

    public SeatingPage() throws SQLException {
        connection = dbConnection.getConnection();
        seatButtons = new HashMap<>();
        seatStatuses = new HashMap<>();

        setupUI();
        loadShows();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        showComboBox = new JComboBox<>();
        showComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        showComboBox.addActionListener(e -> loadSeatingForShow());
        leftPanel.add(new JLabel("Select Show: ") {{
            setFont(new Font("Arial", Font.BOLD, 14));
            setForeground(new Color(33, 33, 33));
        }});
        leftPanel.add(showComboBox);

        saveButton = new JButton("Save Configuration");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(DARK_GREEN);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        saveButton.setPreferredSize(new Dimension(200, 35));
        saveButton.setOpaque(true);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> saveSeatingConfiguration());
        leftPanel.add(saveButton);

        topPanel.add(leftPanel, BorderLayout.WEST);

        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tabPanel.setBackground(Color.WHITE);
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        mainHallPanel = createMainHallPanel();
        smallHallPanel = createSmallHallPanel();
        tabbedPane.addTab("Main Hall", new JScrollPane(mainHallPanel) {{
            setBorder(BorderFactory.createEmptyBorder());
        }});
        tabbedPane.addTab("Small Hall", new JScrollPane(smallHallPanel) {{
            setBorder(BorderFactory.createEmptyBorder());
        }});
        tabPanel.add(tabbedPane);
        topPanel.add(tabPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        JPanel legendPanel = createLegendPanel();
        legendPanel.setPreferredSize(new Dimension(0, 50));
        add(legendPanel, BorderLayout.SOUTH);
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        addLegendItem(legendPanel, "Available", Color.GREEN);
        addLegendItem(legendPanel, "Reserved", Color.RED);
        addLegendItem(legendPanel, "Restricted", Color.GRAY);
        addLegendItem(legendPanel, "Companion", Color.BLUE);
        addLegendItem(legendPanel, "Disability", Color.YELLOW);
        addLegendItem(legendPanel, "Discounted", Color.ORANGE);

        return legendPanel;
    }

    private void addLegendItem(JPanel panel, String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(Color.WHITE);
        JLabel colorBox = new JLabel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(new Color(33, 33, 33)));

        item.add(colorBox);
        item.add(new JLabel(text) {{
            setFont(new Font("Arial", Font.PLAIN, 12));
            setForeground(new Color(33, 33, 33));
        }});
        panel.add(item);
    }

    private JPanel createMainHallPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        JLabel balconyLabel = new JLabel("BALCONY", SwingConstants.CENTER);
        balconyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balconyLabel.setForeground(new Color(33, 33, 33));
        balconyLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        contentPanel.add(balconyLabel);

        JPanel balconyPanel = new JPanel(new GridLayout(3, 1, 1, 1));
        balconyPanel.setBackground(Color.WHITE);

        balconyPanel.add(createRow("CC", 1, 8));
        balconyPanel.add(createRow("BB", 6, 23));
        balconyPanel.add(createRow("AA", 21, 33));

        contentPanel.add(balconyPanel);
        contentPanel.add(Box.createVerticalStrut(5));

        JLabel stallsLabel = new JLabel("STALLS", SwingConstants.CENTER);
        stallsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        stallsLabel.setForeground(new Color(33, 33, 33));
        stallsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        contentPanel.add(stallsLabel);

        JPanel stallsWithBalconies = new JPanel(new BorderLayout());
        stallsWithBalconies.setBackground(Color.WHITE);

        JPanel leftBalcony = createSideBalcony("BB", 1, 5, "AA", 1, 20);
        leftBalcony.setPreferredSize(new Dimension(80, 0));
        stallsWithBalconies.add(leftBalcony, BorderLayout.WEST);

        JPanel rightBalcony = createSideBalcony("AA", 34, 53, "BB", 24, 28);
        rightBalcony.setPreferredSize(new Dimension(80, 0));
        stallsWithBalconies.add(rightBalcony, BorderLayout.EAST);

        JPanel stallsPanel = new JPanel(new GridLayout(16, 1, 1, 1));
        stallsPanel.setBackground(Color.WHITE);

        stallsPanel.add(createRow("Q", 1, 10));
        stallsPanel.add(createRow("P", 1, 11));
        stallsPanel.add(createSplitRow("O", 1, 16, 17, 20, 0, 0));
        stallsPanel.add(createSplitRow("N", 4, 14, 1, 3, 17, 19));
        stallsPanel.add(createSplitRow("M", 3, 12, 1, 2, 15, 16));

        char[] rows = {'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        for (char row : rows) {
            int endSeat = row == 'A' ? 19 : 19;
            stallsPanel.add(createSplitRow(row + "", 4, 16, 1, 3, 17, endSeat));
        }

        stallsWithBalconies.add(stallsPanel, BorderLayout.CENTER);
        contentPanel.add(stallsWithBalconies);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBackground(Color.WHITE);

        JPanel stageContainer = new JPanel(new BorderLayout());
        stageContainer.setBackground(Color.WHITE);
        stageContainer.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));

        JLabel stageLabel = new JLabel("STAGE", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        stageLabel.setForeground(Color.BLACK);
        stageLabel.setBackground(Color.LIGHT_GRAY);
        stageLabel.setOpaque(true);
        stageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        stageContainer.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
        stageContainer.add(stageLabel, BorderLayout.CENTER);
        stageContainer.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);

        stagePanel.add(stageContainer, BorderLayout.CENTER);
        mainPanel.add(stagePanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createRow(String row, int start, int end) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        rowPanel.setBackground(Color.WHITE);
        JLabel label = new JLabel(row);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(33, 33, 33));
        rowPanel.add(label);
        for (int i = start; i <= end; i++) {
            String seatCode = row + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(25, 18));
            seat.setFont(new Font("Arial", Font.BOLD, 9));
            seat.setOpaque(true);
            seat.setBackground(AVAILABLE_COLOR);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            rowPanel.add(seat);
        }
        return rowPanel;
    }

    private JPanel createSplitRow(String row, int upperStart, int upperEnd, int lowerStart1, int lowerEnd1, int lowerStart2, int lowerEnd2) {
        JPanel rowPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        rowPanel.setBackground(Color.WHITE);

        JPanel upperRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        upperRow.setBackground(Color.WHITE);
        JLabel label = new JLabel(row);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(33, 33, 33));
        upperRow.add(label);
        for (int i = upperStart; i <= upperEnd; i++) {
            String seatCode = row + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(25, 18));
            seat.setFont(new Font("Arial", Font.BOLD, 9));
            seat.setOpaque(true);
            seat.setBackground(AVAILABLE_COLOR);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            upperRow.add(seat);
        }
        rowPanel.add(upperRow);

        JPanel lowerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        lowerRow.setBackground(Color.WHITE);
        lowerRow.add(Box.createHorizontalStrut(row.equals("O") ? 465 : row.equals("N") ? 20 : row.equals("M") ? 50 : 20));
        for (int i = lowerStart1; i <= lowerEnd1; i++) {
            String seatCode = row + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(25, 18));
            seat.setFont(new Font("Arial", Font.BOLD, 9));
            seat.setOpaque(true);
            seat.setBackground(AVAILABLE_COLOR);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            lowerRow.add(seat);
        }
        lowerRow.add(Box.createHorizontalStrut(row.equals("O") ? 25 : row.equals("N") ? 300 : row.equals("M") ? 268 : 355));
        for (int i = lowerStart2; i <= lowerEnd2; i++) {
            String seatCode = row + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(25, 18));
            seat.setFont(new Font("Arial", Font.BOLD, 9));
            seat.setOpaque(true);
            seat.setBackground(AVAILABLE_COLOR);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            lowerRow.add(seat);
        }
        lowerRow.add(Box.createHorizontalStrut(row.equals("M") ? 66 : 25));
        rowPanel.add(lowerRow);

        return rowPanel;
    }

    private JPanel createSideBalcony(String row1, int start1, int end1, String row2, int start2, int end2) {
        JPanel balcony = new JPanel(new GridLayout(1, 2, 5, 5));
        balcony.setBackground(Color.WHITE);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.setBackground(Color.WHITE);
        JLabel label1 = new JLabel(row1, SwingConstants.CENTER);
        label1.setFont(new Font("Arial", Font.BOLD, 12));
        label1.setForeground(new Color(33, 33, 33));
        panel1.add(label1);
        for (int i = end1; i >= start1; i--) {
            String seatCode = row1 + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(40, 30));
            seat.setFont(new Font("Arial", Font.BOLD, 12));
            seat.setOpaque(true);
            seat.setBackground(AVAILABLE_COLOR);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            panel1.add(seat);
            panel1.add(Box.createVerticalStrut(2));
        }
        balcony.add(panel1);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel2.setBackground(Color.WHITE);
        JLabel label2 = new JLabel(row2, SwingConstants.CENTER);
        label2.setFont(new Font("Arial", Font.BOLD, 12));
        label2.setForeground(new Color(33, 33, 33));
        panel2.add(label2);
        for (int i = end2; i >= start2; i--) {
            String seatCode = row2 + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(40, 30));
            seat.setFont(new Font("Arial", Font.BOLD, 12));
            seat.setOpaque(true);
            seat.setBackground(AVAILABLE_COLOR);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            panel2.add(seat);
            panel2.add(Box.createVerticalStrut(2));
        }
        balcony.add(panel2);

        return balcony;
    }

    private JPanel createSmallHallPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JPanel soundDeskPanel = new JPanel(new BorderLayout());
        JLabel soundDeskLabel = new JLabel("SOUND DESK", SwingConstants.CENTER);
        soundDeskLabel.setFont(new Font("Arial", Font.BOLD, 14));
        soundDeskLabel.setForeground(Color.WHITE);
        soundDeskLabel.setBackground(new Color(50, 50, 50));
        soundDeskLabel.setOpaque(true);
        soundDeskLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel soundDeskContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        soundDeskContainer.setBackground(Color.WHITE);
        soundDeskContainer.add(soundDeskLabel);
        soundDeskPanel.add(soundDeskContainer, BorderLayout.CENTER);
        soundDeskPanel.setBackground(Color.WHITE);
        mainPanel.add(soundDeskPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        leftPanel.setBackground(Color.WHITE);

        JPanel entrancePanel = new JPanel();
        entrancePanel.setLayout(new BoxLayout(entrancePanel, BoxLayout.Y_AXIS));
        entrancePanel.setBackground(Color.WHITE);
        JLabel arrowLabel = new JLabel("↑");
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 18));
        arrowLabel.setForeground(new Color(33, 150, 243));
        arrowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel entranceLabel = new JLabel("ENTRANCE");
        entranceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        entranceLabel.setForeground(new Color(33, 33, 33));
        entranceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        entrancePanel.add(arrowLabel);
        entrancePanel.add(entranceLabel);
        leftPanel.add(entrancePanel);
        leftPanel.add(Box.createVerticalGlue());

        JPanel aisleTextPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(new Color(33, 150, 243));
                g2d.rotate(Math.PI / 2, getWidth() / 2, getHeight() / 2);
                g2d.drawString("AISLE", getWidth() / 2 - 25, getHeight() / 2 + 5);
                g2d.dispose();
            }
        };
        aisleTextPanel.setPreferredSize(new Dimension(30, 150));
        aisleTextPanel.setBackground(Color.WHITE);
        leftPanel.add(aisleTextPanel);
        leftPanel.add(Box.createVerticalGlue());

        centerPanel.add(leftPanel, BorderLayout.WEST);

        JPanel stallsPanel = new JPanel(new GridLayout(13, 1, 5, 5));
        stallsPanel.setBackground(Color.WHITE);
        stallsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel rowN = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        rowN.setBackground(Color.WHITE);
        rowN.add(Box.createHorizontalStrut(20));
        JLabel rowNLabel = new JLabel("N");
        rowNLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rowNLabel.setForeground(new Color(33, 33, 33));
        rowN.add(rowNLabel);
        for (int i = 1; i <= 4; i++) {
            String seatCode = "N" + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setFont(new Font("Arial", Font.BOLD, 10));
            seat.setOpaque(true);
            seat.setBackground(AVAILABLE_COLOR);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            rowN.add(seat);
        }
        stallsPanel.add(rowN);

        JPanel rowM = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        rowM.setBackground(Color.WHITE);
        rowM.add(Box.createHorizontalStrut(45));
        JLabel rowMLabel = new JLabel("M");
        rowMLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rowMLabel.setForeground(new Color(33, 33, 33));
        rowM.add(rowMLabel);
        for (int i = 1; i <= 4; i++) {
            String seatCode = "M" + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setFont(new Font("Arial", Font.BOLD, 10));
            seat.setOpaque(true);
            seat.setBackground(AVAILABLE_COLOR);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            rowM.add(seat);
        }
        stallsPanel.add(rowM);

        char[] rowsToInclude = {'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        for (char rowLetter : rowsToInclude) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            row.setBackground(Color.WHITE);
            row.add(Box.createHorizontalStrut(45));
            JLabel rowLabel = new JLabel("" + rowLetter);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            rowLabel.setForeground(new Color(33, 33, 33));
            row.add(rowLabel);
            for (int i = 1; i <= 7; i++) {
                String seatCode = rowLetter + String.valueOf(i);
                JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                seat.setPreferredSize(new Dimension(28, 22));
                seat.setFont(new Font("Arial", Font.BOLD, 10));
                seat.setOpaque(true);
                seat.setBackground(AVAILABLE_COLOR);
                seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
                seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
                seat.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showSeatOptions(seatCode, seat);
                    }
                });
                seatButtons.put(seatCode, seat);
                row.add(seat);
            }
            stallsPanel.add(row);
        }

        centerPanel.add(stallsPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBackground(Color.WHITE);

        JPanel stageContainer = new JPanel(new BorderLayout());
        stageContainer.setBackground(Color.WHITE);
        stageContainer.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));

        JLabel stageLabel = new JLabel("STAGE", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        stageLabel.setForeground(Color.BLACK);
        stageLabel.setBackground(Color.LIGHT_GRAY);
        stageLabel.setOpaque(true);
        stageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        stageContainer.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
        stageContainer.add(stageLabel, BorderLayout.CENTER);
        stageContainer.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);

        stagePanel.add(stageContainer, BorderLayout.CENTER);
        mainPanel.add(stagePanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void showSeatOptions(String seatCode, JLabel seatLabel) {
        JPopupMenu popup = new JPopupMenu();
        String[] options = {"Available", "Occupied", "Restricted", "Companion", "Disability", "Discounted"};
        for (String option : options) {
            JMenuItem item = new JMenuItem(option);
            switch(option.toLowerCase()) {
                case "available":
                    item.setBackground(AVAILABLE_COLOR);
                    break;
                case "occupied":
                    item.setBackground(OCCUPIED_COLOR);
                    item.setForeground(Color.WHITE);
                    break;
                case "restricted":
                    item.setBackground(RESTRICTED_COLOR);
                    item.setForeground(Color.WHITE);
                    break;
                case "companion":
                    item.setBackground(COMPANION_COLOR);
                    item.setForeground(Color.WHITE);
                    break;
                case "disability":
                    item.setBackground(DISABILITY_COLOR);
                    break;
                case "discounted":
                    item.setBackground(DISCOUNTED_COLOR);
                    break;
            }
            item.setOpaque(true);
            item.addActionListener(e -> {
                updateSeatStatus(seatCode, seatLabel, option);
                seatLabel.repaint();
            });
            popup.add(item);
        }
        popup.show(seatLabel, seatLabel.getWidth() / 2, seatLabel.getHeight() / 2);
    }

    private void updateSeatStatus(String seatCode, JLabel seatLabel, String status) {
        Color backgroundColor;
        Color textColor = Color.BLACK;

        switch (status.toLowerCase()) {
            case "available":
                backgroundColor = AVAILABLE_COLOR;
                break;
            case "occupied":
            case "reserved":
            case "sold":
                backgroundColor = OCCUPIED_COLOR;
                textColor = Color.WHITE;
                status = "Occupied";
                break;
            case "restricted":
                backgroundColor = RESTRICTED_COLOR;
                textColor = Color.WHITE;
                break;
            case "companion":
                backgroundColor = COMPANION_COLOR;
                textColor = Color.WHITE;
                break;
            case "disability":
                backgroundColor = DISABILITY_COLOR;
                String[] parts = seatCode.split("(?<=\\D)(?=\\d)");
                if (parts.length == 2) {
                    String row = parts[0];
                    try {
                        int seatNum = Integer.parseInt(parts[1]);
                        String adjacentSeatCode = row + (seatNum + 1);
                        JLabel adjacentLabel = seatButtons.get(adjacentSeatCode);
                        if (adjacentLabel != null) {
                            adjacentLabel.setBackground(COMPANION_COLOR);
                            adjacentLabel.setForeground(Color.WHITE);
                            seatStatuses.put(adjacentSeatCode, "Companion");
                            adjacentLabel.repaint();
                        }
                    } catch (NumberFormatException e) {
                        // Handle invalid seat number
                    }
                }
                break;
            case "discounted":
                backgroundColor = DISCOUNTED_COLOR;
                break;
            default:
                backgroundColor = Color.LIGHT_GRAY;
        }

        seatLabel.setBackground(backgroundColor);
        seatLabel.setForeground(textColor);
        seatLabel.setOpaque(true);
        seatStatuses.put(seatCode, status);
    }

    private void loadShows() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Show_ID, Show_Title, Show_Date FROM Shows");

            while (rs.next()) {
                String showInfo = rs.getInt("Show_ID") + " - " + rs.getString("Show_Title") + " (" + rs.getDate("Show_Date") + ")";
                showComboBox.addItem(showInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading shows: " + e.getMessage());
        }
    }

    private void loadSeatingForShow() {
        String selectedShow = (String) showComboBox.getSelectedItem();
        if (selectedShow == null) return;

        try {
            for (JLabel label : seatButtons.values()) {
                label.setBackground(AVAILABLE_COLOR);
                label.setForeground(Color.BLACK);
                label.setOpaque(true);
            }
            seatStatuses.clear();

            selectedShowId = Integer.parseInt(selectedShow.split(" - ")[0]);

            PreparedStatement psVenue = connection.prepareStatement(
                    "SELECT Venue_ID, Hall_Type FROM Shows WHERE Show_ID = ?"
            );
            psVenue.setInt(1, selectedShowId);
            ResultSet rsVenue = psVenue.executeQuery();

            if (rsVenue.next()) {
                selectedVenueId = rsVenue.getInt("Venue_ID");
                String hallType = rsVenue.getString("Hall_Type");
                tabbedPane.setSelectedIndex("Main Hall".equals(hallType) ? 0 : 1);
            }

            PreparedStatement psTypes = connection.prepareStatement(
                    "SELECT Seat_Code, Is_Restricted_View, Is_Wheelchair_Accessible, Is_Companion " +
                            "FROM Seat WHERE Venue_ID = ?"
            );
            psTypes.setInt(1, selectedVenueId);
            ResultSet rsTypes = psTypes.executeQuery();

            while (rsTypes.next()) {
                String seatCode = rsTypes.getString("Seat_Code");
                boolean isRestricted = rsTypes.getBoolean("Is_Restricted_View");
                boolean isWheelchair = rsTypes.getBoolean("Is_Wheelchair_Accessible");
                boolean isCompanion = rsTypes.getBoolean("Is_Companion");

                JLabel seatLabel = seatButtons.get(seatCode);
                if (seatLabel != null) {
                    String status = isWheelchair ? "Disability" : isCompanion ? "Companion" : isRestricted ? "Restricted" : "Available";
                    updateSeatStatus(seatCode, seatLabel, status);
                }
            }

            PreparedStatement ps = connection.prepareStatement(
                    "SELECT s.Seat_Code, sa.Status " +
                            "FROM Seat_Availability sa " +
                            "JOIN Seat s ON sa.Seat_ID = s.Seat_ID " +
                            "WHERE sa.Show_ID = ? AND s.Venue_ID = ?"
            );
            ps.setInt(1, selectedShowId);
            ps.setInt(2, selectedVenueId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String seatCode = rs.getString("Seat_Code");
                String status = rs.getString("Status");
                JLabel seatLabel = seatButtons.get(seatCode);
                if (seatLabel != null) {
                    if (status.equals("Sold") || status.equals("Reserved")) {
                        updateSeatStatus(seatCode, seatLabel, "Occupied");
                    } else if (status.equals("Discounted")) {
                        updateSeatStatus(seatCode, seatLabel, "Discounted");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading seating: " + e.getMessage());
        }
    }

    private void saveSeatingConfiguration() {
        if (selectedShowId <= 0 || selectedVenueId <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a show first.");
            return;
        }

        try {
            connection.setAutoCommit(false);

            Map<String, Integer> seatCodeToIdMap = new HashMap<>();
            PreparedStatement psSeatIds = connection.prepareStatement(
                    "SELECT Seat_ID, Seat_Code FROM Seat WHERE Venue_ID = ?"
            );
            psSeatIds.setInt(1, selectedVenueId);
            ResultSet rsSeatIds = psSeatIds.executeQuery();

            while (rsSeatIds.next()) {
                seatCodeToIdMap.put(rsSeatIds.getString("Seat_Code"), rsSeatIds.getInt("Seat_ID"));
            }

            PreparedStatement psDelete = connection.prepareStatement(
                    "DELETE FROM Seat_Availability WHERE Show_ID = ?"
            );
            psDelete.setInt(1, selectedShowId);
            psDelete.executeUpdate();

            PreparedStatement psInsert = connection.prepareStatement(
                    "INSERT INTO Seat_Availability (Seat_ID, Show_ID, Status) VALUES (?, ?, ?)"
            );

            for (Map.Entry<String, String> entry : seatStatuses.entrySet()) {
                String seatCode = entry.getKey();
                String status = entry.getValue();

                String dbStatus;
                switch (status.toLowerCase()) {
                    case "occupied":
                        dbStatus = "Sold";
                        break;
                    case "discounted":
                        dbStatus = "Discounted";
                        break;
                    case "available":
                    case "restricted":
                    case "companion":
                    case "disability":
                        dbStatus = "Available";
                        break;
                    default:
                        dbStatus = "Available";
                }

                Integer seatId = seatCodeToIdMap.get(seatCode);
                if (seatId != null && !dbStatus.equals("Available")) {
                    psInsert.setInt(1, seatId);
                    psInsert.setInt(2, selectedShowId);
                    psInsert.setString(3, dbStatus);
                    psInsert.addBatch();
                }
            }

            psInsert.executeBatch();
            connection.commit();
            JOptionPane.showMessageDialog(this, "Seating configuration saved successfully!");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving configuration: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}