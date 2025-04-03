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

    private static final Color AVAILABLE_COLOR = Color.GREEN;
    private static final Color OCCUPIED_COLOR = Color.RED;
    private static final Color RESTRICTED_COLOR = Color.DARK_GRAY;
    private static final Color COMPANION_COLOR = new Color(128, 0, 128); // Purple
    private static final Color DISABILITY_COLOR = Color.YELLOW;

    public SeatingPage() throws SQLException {
        connection = dbConnection.getConnection();
        seatButtons = new HashMap<>();
        seatStatuses = new HashMap<>();

        setupUI();
        loadShows();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        showComboBox = new JComboBox<>();
        showComboBox.addActionListener(e -> loadSeatingForShow());
        topPanel.add(new JLabel("Select Show: "));
        topPanel.add(showComboBox);

        saveButton = new JButton("Save Configuration");
        saveButton.addActionListener(e -> saveSeatingConfiguration());
        topPanel.add(saveButton);

        // Add a legend panel to explain colors
        JPanel legendPanel = createLegendPanel();
        topPanel.add(legendPanel);

        add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        mainHallPanel = createMainHallPanel();
        smallHallPanel = createSmallHallPanel();
        tabbedPane.addTab("Main Hall", new JScrollPane(mainHallPanel));
        tabbedPane.addTab("Small Hall", new JScrollPane(smallHallPanel));
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legend"));

        addLegendItem(legendPanel, "Available", AVAILABLE_COLOR);
        addLegendItem(legendPanel, "Reserved", OCCUPIED_COLOR);
        addLegendItem(legendPanel, "Restricted", RESTRICTED_COLOR);
        addLegendItem(legendPanel, "Companion", COMPANION_COLOR);
        addLegendItem(legendPanel, "Disability", DISABILITY_COLOR);

        return legendPanel;
    }

    private void addLegendItem(JPanel panel, String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        JLabel colorBox = new JLabel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        item.add(colorBox);
        item.add(new JLabel(text));
        panel.add(item);
    }

    private JPanel createMainHallPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel balconyLabel = new JLabel("BALCONY", SwingConstants.CENTER);
        balconyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(balconyLabel);

        JPanel balconyPanel = new JPanel();
        balconyPanel.setLayout(new GridLayout(3, 1, 1, 1));

        balconyPanel.add(createRow("CC", 1, 8));
        balconyPanel.add(createRow("BB", 6, 23));
        balconyPanel.add(createRow("AA", 21, 33));

        mainPanel.add(balconyPanel);
        mainPanel.add(Box.createVerticalStrut(5));

        JLabel stallsLabel = new JLabel("STALLS", SwingConstants.CENTER);
        stallsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(stallsLabel);

        JPanel stallsPanel = new JPanel();
        stallsPanel.setLayout(new GridLayout(16, 1, 1, 1));

        stallsPanel.add(createRow("Q", 1, 10));
        stallsPanel.add(createRow("P", 1, 11));
        stallsPanel.add(createSplitRow("O", 1, 16, 17, 20));
        stallsPanel.add(createSplitRow("N", 1, 14, 17, 19));
        stallsPanel.add(createSplitRow("M", 1, 12, 15, 16));

        char[] rows = {'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        for (char row : rows) {
            stallsPanel.add(createSplitRow(row + "", 1, 16, 17, 19));
        }

        mainPanel.add(stallsPanel);

        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.add(mainPanel, BorderLayout.CENTER);

        JPanel leftBalcony = createSideBalcony("BB", 1, 5, "AA", 1, 20);
        sidePanel.add(leftBalcony, BorderLayout.WEST);

        JPanel rightBalcony = createSideBalcony("AA", 34, 53, "BB", 24, 28);
        sidePanel.add(rightBalcony, BorderLayout.EAST);

        JPanel stagePanel = new JPanel(new BorderLayout());
        JLabel stageLabel = new JLabel("STAGE", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        stageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        stagePanel.add(new JPanel() {{ setPreferredSize(new Dimension(175, 30)); }}, BorderLayout.WEST);
        stagePanel.add(stageLabel, BorderLayout.CENTER);
        stagePanel.add(new JPanel() {{ setPreferredSize(new Dimension(193, 30)); }}, BorderLayout.EAST);

        JPanel container = new JPanel(new BorderLayout());
        container.add(sidePanel, BorderLayout.CENTER);
        container.add(stagePanel, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createRow(String row, int start, int end) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        rowPanel.add(new JLabel(row));
        for (int i = start; i <= end; i++) {
            String seatCode = row + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(25, 18));
            seat.setFont(new Font("Arial", Font.BOLD, 9));
            seat.setOpaque(true);
            seat.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
        rowPanel.add(new JLabel(row));
        return rowPanel;
    }

    private JPanel createSplitRow(String row, int upperStart, int upperEnd, int lowerStart, int lowerEnd) {
        JPanel rowPanel = new JPanel(new GridLayout(2, 1, 0, 0));

        JPanel upperRow = createRow(row, upperStart, upperEnd);
        rowPanel.add(upperRow);

        JPanel lowerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        lowerRow.add(Box.createHorizontalStrut(20));
        for (int i = 1; i <= 3; i++) {
            String seatCode = row + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(25, 18));
            seat.setFont(new Font("Arial", Font.BOLD, 9));
            seat.setOpaque(true);
            seat.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
        lowerRow.add(Box.createHorizontalStrut(355));
        for (int i = lowerStart; i <= lowerEnd; i++) {
            String seatCode = row + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(25, 18));
            seat.setFont(new Font("Arial", Font.BOLD, 9));
            seat.setOpaque(true);
            seat.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
        rowPanel.add(lowerRow);
        return rowPanel;
    }

    private JPanel createSideBalcony(String row1, int start1, int end1, String row2, int start2, int end2) {
        JPanel balcony = new JPanel(new GridLayout(1, 2, 1, 1));
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.add(new JLabel(row1, SwingConstants.CENTER));
        for (int i = end1; i >= start1; i--) {
            String seatCode = row1 + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(25, 18));
            seat.setFont(new Font("Arial", Font.BOLD, 9));
            seat.setOpaque(true);
            seat.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));

            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            panel1.add(seat);
        }
        balcony.add(panel1);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel2.add(new JLabel(row2, SwingConstants.CENTER));
        for (int i = end2; i >= start2; i--) {
            String seatCode = row2 + i;
            JLabel seat = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            seat.setPreferredSize(new Dimension(25, 18));
            seat.setFont(new Font("Arial", Font.BOLD, 9));
            seat.setOpaque(true);
            seat.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));

            seat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSeatOptions(seatCode, seat);
                }
            });
            seatButtons.put(seatCode, seat);
            panel2.add(seat);
        }
        balcony.add(panel2);
        return balcony;
    }

    private JPanel createSmallHallPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Main panel with modern look
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(33, 33, 33), 2));

        // Sound Desk (top-right, modern dark gray box)
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

        // Center panel for aisle and seating
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // Left panel for aisle and entrance
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
        entrancePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        aisleTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(aisleTextPanel);

        leftPanel.add(Box.createVerticalGlue());

        centerPanel.add(leftPanel, BorderLayout.WEST);

        // Stalls Section
        JPanel stallsPanel = new JPanel();
        stallsPanel.setLayout(new BoxLayout(stallsPanel, BoxLayout.PAGE_AXIS));
        stallsPanel.setBackground(Color.WHITE);

        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new GridLayout(13, 1, 2, 2));
        rowsPanel.setBackground(Color.WHITE);

        // Row N (seats 1-4, shifted left)
        JPanel rowN = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
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
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setOpaque(true);
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
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
        rowsPanel.add(rowN);

        // Row M (seats 1-4)
        JPanel rowM = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
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
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setOpaque(true);
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
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
        rowsPanel.add(rowM);

        // Rows L to A (seats 1-7, excluding I)
        char[] rowsToInclude = {'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        for (char rowLetter : rowsToInclude) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
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
                seat.setFont(new Font("Arial", Font.PLAIN, 10));
                seat.setOpaque(true);
                seat.setBackground(new Color(200, 230, 201));
                seat.setForeground(new Color(33, 33, 33));
                seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
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
            rowsPanel.add(row);
        }

        stallsPanel.add(rowsPanel);
        centerPanel.add(stallsPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Stage
        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBackground(Color.WHITE);
        JLabel stageLabel = new JLabel("STAGE", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Arial", Font.BOLD, 22));
        stageLabel.setForeground(Color.WHITE);
        stageLabel.setBackground(new Color(62, 116, 70));
        stageLabel.setOpaque(true);
        stageLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        stagePanel.add(stageLabel, BorderLayout.CENTER);
        mainPanel.add(stagePanel, BorderLayout.SOUTH);

        panel.add(mainPanel, BorderLayout.CENTER);
        return panel;
    }

    private void showSeatOptions(String seatCode, JLabel seatLabel) {
        JPopupMenu popup = new JPopupMenu();
        String[] options = {"Available", "Occupied", "Restricted", "Companion", "Disability"};
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
                label.setBackground(UIManager.getColor("Label.background"));
                label.setForeground(UIManager.getColor("Label.foreground"));
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
                    seatStatuses.put(seatCode, status);
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
                if (status.equals("Reserved") || status.equals("Sold")) {
                    seatStatuses.put(seatCode, "Occupied");
                }
            }

            for (Map.Entry<String, String> entry : seatStatuses.entrySet()) {
                String seatCode = entry.getKey();
                String status = entry.getValue();
                JLabel seatLabel = seatButtons.get(seatCode);
                if (seatLabel != null) {
                    updateSeatStatus(seatCode, seatLabel, status);
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
                    case "restricted":
                    case "companion":
                    case "disability":
                    case "available":
                        dbStatus = "Available";
                        break;
                    default:
                        dbStatus = "Available";
                }

                Integer seatId = seatCodeToIdMap.get(seatCode);
                if (seatId != null) {
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