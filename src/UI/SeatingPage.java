package UI;
import db.dbConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Seating Page - handles Extensive management and search of all seating data with a seating plan for main hall and small hall.
 * has an extensive layout of displaying the seating configuration and heavy production of choosing a seat based on seat types.
 * all data goes to relational database to be updated/ saved for configurations for each seat for each show.
 * manager can successfully add a new show to site, manage the shows, and fetch the sales report.
 * filters are made to search for seats or rooms outside the main hall/small hall
 * bookings can be searched for these rooms that are to be booked by third parties.
 * implementation of tabs for easy navigation
 */

public class SeatingPage extends JPanel {
    private JTabbedPane tabbedPane;
    private JPanel mainHallPanel;
    private JPanel smallHallPanel;
    private JPanel roomsPanel;
    private JComboBox<String> showComboBox;
    private JButton saveButton;
    private Connection connection;
    private Map<String, JLabel> seatButtons;
    private Map<Integer, Map<String, String>> showSeatStatuses;
    private int selectedShowId;
    private int selectedVenueId;

    // initialise the colour scheme of each seat
    private static final Color AVAILABLE_COLOR = new Color(200, 230, 201);
    private static final Color OCCUPIED_COLOR = new Color(239, 83, 80);
    private static final Color RESTRICTED_COLOR = new Color(66, 66, 66);
    private static final Color COMPANION_COLOR = new Color(171, 71, 188);
    private static final Color DISABILITY_COLOR = new Color(255, 202, 40);
    private static final Color DISCOUNTED_COLOR = new Color(255, 165, 0);
    private static final Color DARK_GREEN = new Color(0, 100, 0);


    /**
     * Construction of the seating base panel
     * Initialises database connection, and the seating map.
     * Sets up the UI
     * @throws SQLException if a database connection error occurs - error handling
     */
    public SeatingPage() throws SQLException {
        connection = dbConnection.getConnection();
        seatButtons = new HashMap<>();
        showSeatStatuses = new HashMap<>();
        setupUI();
        loadShows();
    }

    /**
     * Set up the user interface for the SeatingPage and the seating plan for main/small hall/ rooms
     *  show selection and saving the configurations
     *  tabs for venue seating views, and a colour option selector below for each seat tyoe.
     */
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // set the panels background to be white.
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

        // implement the save button = backend database will save this configuration for each seat chosen
        saveButton = new JButton("Save Configuration");
        saveButton.setFont(new Font("Arial", Font.BOLD, 13));
        saveButton.setBackground(DARK_GREEN);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        saveButton.setPreferredSize(new Dimension(160, 35));
        saveButton.setOpaque(true);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> saveSeatingConfiguration());
        leftPanel.add(saveButton);
        topPanel.add(leftPanel, BorderLayout.WEST);
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tabPanel.setBackground(Color.WHITE);
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setOpaque(true);

        // these methods are important - was implemented because its easier to initialise each tab with the direct method to call it.
        mainHallPanel = createMainHallPanel();
        smallHallPanel = createSmallHallPanel();
        roomsPanel = createRoomsPanel();

        tabbedPane.addTab("Main Hall", new JScrollPane(mainHallPanel) {{
            setBorder(BorderFactory.createEmptyBorder());
        }});
        tabbedPane.addTab("Small Hall", new JScrollPane(smallHallPanel) {{
            setBorder(BorderFactory.createEmptyBorder());
        }});
        tabbedPane.addTab("Rooms", new JScrollPane(roomsPanel) {{
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

    // create the legend which is the colour format guidance at the bottom
    // helps the manager pick the seat colour.
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // each panel has the relative colour.
        addLegendItem(legendPanel, "Available", AVAILABLE_COLOR);
        addLegendItem(legendPanel, "Occupied",OCCUPIED_COLOR );
        addLegendItem(legendPanel, "Restricted", RESTRICTED_COLOR);
        addLegendItem(legendPanel, "Companion", COMPANION_COLOR);
        addLegendItem(legendPanel, "Disability", DISABILITY_COLOR);
        addLegendItem(legendPanel, "Discounted", DISCOUNTED_COLOR);
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


    /**
     * Creates the Main Hall seating panel with balcony and stalls sections -
     * seats are shown as buttons
     * @return the the main hall seating plan
     */
    private JPanel createMainHallPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        JLabel balconyLabel = new JLabel("BALCONY", SwingConstants.CENTER);
        balconyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balconyLabel.setForeground(new Color(33, 33, 33));
        balconyLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        contentPanel.add(balconyLabel);

        JPanel balconyPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        balconyPanel.setBackground(Color.WHITE);

        balconyPanel.add(createRow("CC", 1, 8));
        balconyPanel.add(createRow("BB", 6, 23));
        balconyPanel.add(createRow("AA", 21, 33));

        contentPanel.add(balconyPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        JLabel stallsLabel = new JLabel("STALLS", SwingConstants.CENTER);
        stallsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        stallsLabel.setForeground(new Color(33, 33, 33));
        stallsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        contentPanel.add(stallsLabel);
        JPanel stallsWithBalconies = new JPanel(new BorderLayout());
        stallsWithBalconies.setBackground(Color.WHITE);
        // Left Balconies
        JPanel leftBalcony = new JPanel(new GridLayout(1, 2, 5, 0));
        leftBalcony.setBackground(Color.WHITE);

        // Left BB balconies
        // for loop around them
        JPanel leftBB = new JPanel();
        leftBB.setLayout(new BoxLayout(leftBB, BoxLayout.Y_AXIS));
        leftBB.setBackground(Color.WHITE);
        leftBB.add(Box.createVerticalStrut(15));
        JLabel leftBBLabel = new JLabel("BB", SwingConstants.CENTER);
        leftBBLabel.setFont(new Font("Arial", Font.BOLD, 12));
        leftBBLabel.setForeground(new Color(33, 33, 33));
        leftBB.add(leftBBLabel);
        for (int i = 5; i >= 1; i--) {
            String seatCode = "BB" + i;
            JLabel seat = createSeatLabel(seatCode, String.valueOf(i), 35, 28);
            leftBB.add(seat);
            if (i > 1) leftBB.add(Box.createVerticalStrut(3));
        }

        // Left AA balconies
        JPanel leftAA = new JPanel();
        leftAA.setLayout(new BoxLayout(leftAA, BoxLayout.Y_AXIS));
        leftAA.setBackground(Color.WHITE);
        leftAA.add(Box.createVerticalStrut(15));
        JLabel leftAALabel = new JLabel("AA", SwingConstants.CENTER);
        leftAALabel.setFont(new Font("Arial", Font.BOLD, 12));
        leftAALabel.setForeground(new Color(33, 33, 33));
        leftAA.add(leftAALabel);
        for (int i = 20; i >= 6; i--) {
            String seatCode = "AA" + i;
            JLabel seat = createSeatLabel(seatCode, String.valueOf(i), 35, 28);
            leftAA.add(seat);
            if (i > 6) leftAA.add(Box.createVerticalStrut(3));
        }
        JLabel seat5 = createSeatLabel("AA5", "5", 35, 28);
        leftAA.add(seat5);
        leftAA.add(Box.createVerticalStrut(20));
        leftAA.add(Box.createVerticalStrut(20));
        leftAA.add(Box.createVerticalStrut(20));
        for (int i = 4; i >= 1; i--) {
            String seatCode = "AA" + i;
            JLabel seat = createSeatLabel(seatCode, String.valueOf(i), 35, 28);
            leftAA.add(seat);
            if (i > 1) leftAA.add(Box.createVerticalStrut(3));
        }

        leftBalcony.add(leftBB);
        leftBalcony.add(leftAA);
        stallsWithBalconies.add(leftBalcony, BorderLayout.WEST);

        // Right Balconies
        JPanel rightBalcony = new JPanel(new GridLayout(1, 2, 5, 0));
        rightBalcony.setBackground(Color.WHITE);

        // Right AA balconies
        JPanel rightAA = new JPanel();
        rightAA.setLayout(new BoxLayout(rightAA, BoxLayout.Y_AXIS));
        rightAA.setBackground(Color.WHITE);
        rightAA.add(Box.createVerticalStrut(15));
        JLabel rightAALabel = new JLabel("AA", SwingConstants.CENTER);
        rightAALabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightAALabel.setForeground(new Color(33, 33, 33));
        rightAA.add(rightAALabel);
        for (int i = 34; i <= 49; i++) {
            String seatCode = "AA" + i;
            JLabel seat = createSeatLabel(seatCode, String.valueOf(i), 35, 28);
            rightAA.add(seat);
            if (i < 49) rightAA.add(Box.createVerticalStrut(3));
        }
        rightAA.add(Box.createVerticalStrut(20));
        rightAA.add(Box.createVerticalStrut(20));
        rightAA.add(Box.createVerticalStrut(20));
        for (int i = 50; i <= 53; i++) {
            String seatCode = "AA" + i;
            JLabel seat = createSeatLabel(seatCode, String.valueOf(i), 35, 28);
            rightAA.add(seat);
            if (i < 53) rightAA.add(Box.createVerticalStrut(3));
        }

        // Right BB balconies
        JPanel rightBB = new JPanel();
        rightBB.setLayout(new BoxLayout(rightBB, BoxLayout.Y_AXIS));
        rightBB.setBackground(Color.WHITE);
        rightBB.add(Box.createVerticalStrut(15));
        JLabel rightBBLabel = new JLabel("BB", SwingConstants.CENTER);
        rightBBLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightBBLabel.setForeground(new Color(33, 33, 33));
        rightBB.add(rightBBLabel);
        for (int i = 24; i <= 28; i++) {
            String seatCode = "BB" + i;
            JLabel seat = createSeatLabel(seatCode, String.valueOf(i), 35, 28);
            rightBB.add(seat);
            if (i < 28) rightBB.add(Box.createVerticalStrut(3));
        }
        rightBalcony.add(rightAA);
        rightBalcony.add(rightBB);
        //add the stall panels to the stalls
        stallsWithBalconies.add(rightBalcony, BorderLayout.EAST);
        JPanel stallsPanel = new JPanel(new GridLayout(16, 1, 2, 2));
        stallsPanel.setBackground(Color.WHITE);
        stallsPanel.add(createRow("Q", 1, 10));
        stallsPanel.add(createRow("P", 1, 11));
        stallsPanel.add(createSplitRow("O", 1, 16, 17, 20, 1, 0));
        stallsPanel.add(createSplitRow("N", 4, 14, 1, 3, 17, 19));
        stallsPanel.add(createSplitRow("M", 3, 12, 1, 2, 15, 16));

        // for loop around the lower bottom rows that have the same format as the balconies.
        char[] rows = {'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        for (char row : rows) {
            int endSeat = row == 'A' ? 19 : 19;
            stallsPanel.add(createSplitRow(row + "", 4, 16, 1, 3, 17, endSeat));
        }

        stallsWithBalconies.add(stallsPanel, BorderLayout.CENTER);
        contentPanel.add(stallsWithBalconies);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        // Stage - formatted with accordance to the lancasters seating plan.
        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBackground(Color.WHITE);
        JPanel stageWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        stageWrapper.setBackground(Color.WHITE);

        // set the size of the stage - size was intentional was done, so it fits the seats that can view from it.
        stageWrapper.setPreferredSize(new Dimension(500, 40));
        JLabel stageLabel = new JLabel("STAGE", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Arial", Font.BOLD, 22));
        stageLabel.setForeground(Color.WHITE);
        stageLabel.setBackground(new Color(193, 193, 193));
        stageLabel.setOpaque(true);
        stageLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        stageLabel.setPreferredSize(new Dimension(430, 30));

        stageWrapper.add(stageLabel);
        stagePanel.add(stageWrapper, BorderLayout.CENTER);
        mainPanel.add(stagePanel, BorderLayout.SOUTH);

        return mainPanel;
    }



    /**
     * implement the seat label for the seats - each are number coded.
     * @param seatCode the unique code
     * @param seatNumber the display number of the seats
     * @param width the width of the seat
     * @param height the height of the seat
     * @return seats.
     */
    private JLabel createSeatLabel(String seatCode, String seatNumber, int width, int height) {
        JLabel seat = new JLabel(seatNumber, SwingConstants.CENTER);
        seat.setPreferredSize(new Dimension(width, height));
        seat.setFont(new Font("Arial", Font.PLAIN, height > 25 ? 12 : 10));
        seat.setOpaque(true);
        seat.setBackground(AVAILABLE_COLOR);
        seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        seat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showSeatOptions(seatCode, seat);
            }
        });
        seatButtons.put(seatCode, seat);
        return seat;
    }


    /**
     * Creates the Small Hall seating plan
     * @return Small Hall
     */
    private JPanel createSmallHallPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // sound Desk at the top left.
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

        // left aisle with entrance included from the seating layout - was better to show this, otherwise the seating plan will not be a direct rep of the actual plan that was given to us in moodle.
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


            // design components for the small hall.
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

        // each row after will have a different format implemented, because they are in different parts of the seating order

        // fOR Row N - one chair seat is the only one in its column
        JPanel rowN = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        rowN.setBackground(Color.WHITE);
        rowN.add(Box.createHorizontalStrut(0));
        JLabel rowNLabel = new JLabel("N");
        rowNLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rowNLabel.setForeground(new Color(33, 33, 33));
        rowN.add(rowNLabel);
        for (int i = 1; i <= 3; i++) {
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

        // Row M - same columns as the other but reduced seat due to the sound desck
        JPanel rowM = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        rowM.setBackground(Color.WHITE);
        rowM.add(Box.createHorizontalStrut(45));
        JLabel rowMLabel = new JLabel("M");
        rowMLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rowMLabel.setForeground(new Color(33, 33, 33));
        rowM.add(rowMLabel);
        for (int i = 1; i <= 3; i++) {
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

        // Rows L to A =   5 seats per row
        char[] rowsToInclude = {'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        for (char rowLetter : rowsToInclude) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            row.setBackground(Color.WHITE);
            row.add(Box.createHorizontalStrut(45));
            JLabel rowLabel = new JLabel("" + rowLetter);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            rowLabel.setForeground(new Color(33, 33, 33));
            row.add(rowLabel);
            for (int i = 1; i <= 5; i++) {
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

        // stage
        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBackground(Color.WHITE);
        JPanel stageWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stageWrapper.setPreferredSize(new Dimension(300, 40));
        JLabel stageLabel = new JLabel("STAGE", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Arial", Font.BOLD, 22));
        stageLabel.setForeground(Color.WHITE);
        stageLabel.setBackground(new Color(193, 193, 193));
        stageLabel.setOpaque(true);
        stageLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        stageLabel.setPreferredSize(new Dimension(338, 30)); // Keep size

        stageWrapper.add(stageLabel);
        stagePanel.add(stageWrapper, BorderLayout.CENTER);
        mainPanel.add(stagePanel, BorderLayout.SOUTH);

        return mainPanel;
    }



    /**
     * Creates the 6 Rooms section with a searchable filter of rooms being booked.
     * @return  Rooms
     */
    private JPanel createRoomsPanel() {
        JPanel roomsPanel = new JPanel(new BorderLayout(0, 10));
        roomsPanel.setBackground(Color.WHITE);
        roomsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 11));
        searchLabel.setForeground(new Color(60, 60, 60));
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(400, 20));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        searchField.setBackground(new Color(250, 250, 250));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        roomsPanel.add(searchPanel, BorderLayout.NORTH);
        String[] columnNames = {"Meeting"," Venue", " Start", " End", " Day", " Duration", " Total Cost"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font("Arial", Font.ITALIC, 11));
        bookingsTable.setRowHeight(45);
        bookingsTable.setIntercellSpacing(new Dimension(13, 13));
        bookingsTable.setShowGrid(false);
        bookingsTable.setShowHorizontalLines(true);
        bookingsTable.setGridColor(new Color(230, 230, 230));
        bookingsTable.setBackground(Color.WHITE);
        bookingsTable.setSelectionBackground(new Color(232, 240, 254));
        bookingsTable.setSelectionForeground(new Color(30, 30, 30));

        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        bookingsTable.getTableHeader().setBackground(new Color(30, 71, 19));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        bookingsTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        bookingsTable.getTableHeader().setPreferredSize(new Dimension(0, 25));
        bookingsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            // add the row colours
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));}((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                ((JLabel) c).setHorizontalAlignment(column == 7 ? SwingConstants.RIGHT : SwingConstants.LEFT); if (column == 7 && value != null) {
                    try {
                        String text = value.toString();
                        if (!text.startsWith("£")) {
                            ((JLabel) c).setText("£" + text);
                        }
                    } catch (Exception e) {
                    }
                }return c;}
        });
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookingsTable.setRowSorter(sorter);

     //searching
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim().toLowerCase();
                if (searchText.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
            }
        });

        loadRoomsData(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true), BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JLabel totalBookingsLabel = new JLabel("Total Bookings: " + tableModel.getRowCount());
        totalBookingsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalBookingsLabel.setForeground(new Color(60, 60, 60));
        footerPanel.add(totalBookingsLabel, BorderLayout.WEST);
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(footerPanel, BorderLayout.SOUTH);
        roomsPanel.add(tableContainer, BorderLayout.CENTER);
        return roomsPanel;
    }


    /**
     * Creates the row of seats
     * @param row rows
     * @param start start of the row
     * @param end end of the row
     * @return the row
     */
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

    /**
     *due to the complex nature of the main hall, the rows have a very obtuse way int the stalls, each row starts off as a staircase end and beginning.
     * @param row the row
     * @param upperStart the starting seat abve
     * @param upperEnd the ending seat aboove
     * @param lowerStart1 the starting seat
     * @param lowerEnd1 the ending seat
     * @param lowerStart2 the starting seat lower
     * @param lowerEnd2 the ending seat lower
     * @return the configured split row {@link JPanel}
     */
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
            JLabel seat = createSeatLabel(seatCode, String.valueOf(i), 25, 18);
            upperRow.add(seat);
        }rowPanel.add(upperRow);JPanel lowerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        lowerRow.setBackground(Color.WHITE);
        int leftSpacer = row.equals("O") ? 465 : row.equals("N") ? 20 : row.equals("M") ? 50 : 20;
        lowerRow.add(Box.createHorizontalStrut(leftSpacer));
        for (int i = lowerStart1; i <= lowerEnd1; i++) {
            String seatCode = row + i;
            JLabel seat = createSeatLabel(seatCode, String.valueOf(i), 25, 18);
            lowerRow.add(seat);
        }
        int middleSpacer = row.equals("O") ? 25 : row.equals("N") ? 300 : row.equals("M") ? 268 : 355;
        lowerRow.add(Box.createHorizontalStrut(middleSpacer));
        if (lowerStart2 < lowerEnd2) {
            for (int i = lowerStart2; i <= lowerEnd2; i++) {
                if (i > 0) {
                    String seatCode = row + i;
                    JLabel seat = createSeatLabel(seatCode, String.valueOf(i), 25, 18);
                    lowerRow.add(seat);
                }
            }
        }rowPanel.add(lowerRow);return rowPanel;
    }
    private void showSeatOptions(String seatCode, JLabel seatLabel) {
        JPopupMenu popup = new JPopupMenu();
        String[] options = {"Available", "Occupied", "Restricted", "Companion", "Disability", "Discounted"};
        for (String option : options) {
            // restrict Disability to rows A and L - only book these seats no other. add a companion next to them also
            if ("Disability".equals(option) && !seatCode.startsWith("A") && !seatCode.startsWith("L")) {
                continue;
            }
            JMenuItem item = new JMenuItem(option);
            switch (option.toLowerCase()) {
                case "available": item.setBackground(AVAILABLE_COLOR); break;
                case "occupied": item.setBackground(OCCUPIED_COLOR); item.setForeground(Color.WHITE); break;
                case "restricted": item.setBackground(RESTRICTED_COLOR); item.setForeground(Color.WHITE); break;
                case "companion": item.setBackground(COMPANION_COLOR); item.setForeground(Color.WHITE); break;
                case "disability": item.setBackground(DISABILITY_COLOR); break;
                case "discounted": item.setBackground(DISCOUNTED_COLOR); break;
            }
            item.setOpaque(true);
            item.addActionListener(e -> {
                updateSeatStatus(seatCode, seatLabel, option);
                saveSeatStatusToDB(seatCode, option);
                seatLabel.repaint();
            });
            popup.add(item);
        }
        popup.show(seatLabel, seatLabel.getWidth() / 2, seatLabel.getHeight() / 2);
    }

    /**
     * colour stat of seat, choose from dropdown
     * @param seatCode code
     * @param seatLabel label;
     * @param status status
     */
    private void updateSeatStatus(String seatCode, JLabel seatLabel, String status) {
        Color backgroundColor;
        Color textColor = Color.BLACK;

        switch (status.toLowerCase()) {
            case "available":
                backgroundColor = AVAILABLE_COLOR;
                break;
            case "occupied":
                backgroundColor = OCCUPIED_COLOR;
                textColor = Color.WHITE;
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
                            Map<String, String> currentShowStatuses = showSeatStatuses.getOrDefault(selectedShowId, new HashMap<>());
                            String adjacentStatus = currentShowStatuses.getOrDefault(adjacentSeatCode, "Available");
                            if ("Available".equals(adjacentStatus)) {
                                updateSeatStatus(adjacentSeatCode, adjacentLabel, "Companion");
                                saveSeatStatusToDB(adjacentSeatCode, "Companion");
                                adjacentLabel.repaint();
                            } else {
                                JOptionPane.showMessageDialog(this, "Cannot set " + seatCode + " as Disability: Adjacent seat " + adjacentSeatCode + " is not available.");
                                return;
                            }
                        }
                    } catch (NumberFormatException ignored) {}
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
        Map<String, String> currentShowStatuses = showSeatStatuses.getOrDefault(selectedShowId, new HashMap<>());
        currentShowStatuses.put(seatCode, status);
        showSeatStatuses.put(selectedShowId, currentShowStatuses);
    }

    /**
     * confirm the seat status config to the database
     *
     * @param seatCode code
     * @param status the status
     *
     */
    private void saveSeatStatusToDB(String seatCode, String status) {
        if (selectedShowId <= 0 || selectedVenueId <= 0) return;
        try {
            connection.setAutoCommit(false);
            PreparedStatement psSeatId = connection.prepareStatement(
                    "SELECT Seat_ID FROM Seat WHERE Venue_ID = ? AND Seat_Code = ?"
            );
            psSeatId.setInt(1, selectedVenueId);
            psSeatId.setString(2, seatCode);
            ResultSet rs = psSeatId.executeQuery();
            int seatId;
            if (!rs.next()) {
                PreparedStatement psInsertSeat = connection.prepareStatement(
                        "INSERT INTO Seat (Venue_ID, Seat_Code, Row_Number, Venue_Name) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                psInsertSeat.setInt(1, selectedVenueId);
                psInsertSeat.setString(2, seatCode);
                psInsertSeat.setString(3, seatCode.replaceAll("[0-9]", ""));
                psInsertSeat.setString(4, selectedVenueId == 1 ? "Main Hall" : "Small Hall");
                psInsertSeat.executeUpdate();
                ResultSet generatedKeys = psInsertSeat.getGeneratedKeys();
                seatId = generatedKeys.next() ? generatedKeys.getInt(1) : -1;
            } else {
                seatId = rs.getInt("Seat_ID");
            }
            // availability of each seat will be different for each show.
            PreparedStatement del = connection.prepareStatement(
                    "DELETE FROM Seat_Availability WHERE Show_ID = ? AND Seat_ID = ?"
            );
            del.setInt(1, selectedShowId);
            del.setInt(2, seatId);
            del.executeUpdate();
            String dbStatus;
            switch (status.toLowerCase()) {
                case "occupied": dbStatus = "Sold"; break;
                case "discounted": dbStatus = "Discounted"; break;
                case "restricted": dbStatus = "Reserved"; break;
                case "disability": dbStatus = "Reserved"; break;
                case "companion": dbStatus = "Reserved"; break;
                case "available": dbStatus = "Available"; break;
                default: dbStatus = "Available";
            }
            // insert into Seat_Availability if not Available
            if (!"Available".equals(dbStatus)) {
                PreparedStatement psInsert = connection.prepareStatement(
                        "INSERT INTO Seat_Availability (Seat_ID, Show_ID, Status) VALUES (?, ?, ?)"
                );
                psInsert.setInt(1, seatId);
                psInsert.setInt(2, selectedShowId);
                psInsert.setString(3, dbStatus);
                psInsert.executeUpdate();

                // For Disability, ensure the adjacent seat is also reserved  and moved from the booking process
                if ("disability".equals(status.toLowerCase())) {
                    String[] parts = seatCode.split("(?<=\\D)(?=\\d)");
                    if (parts.length == 2) {
                        String row = parts[0];
                        int seatNum = Integer.parseInt(parts[1]);
                        String adjacentSeatCode = row + (seatNum + 1);
                        JLabel adjacentLabel = seatButtons.get(adjacentSeatCode);
                        if (adjacentLabel != null) {
                            PreparedStatement psAdjacentSeatId = connection.prepareStatement(
                                    "SELECT Seat_ID FROM Seat WHERE Venue_ID = ? AND Seat_Code = ?"
                            );
                            psAdjacentSeatId.setInt(1, selectedVenueId);
                            psAdjacentSeatId.setString(2, adjacentSeatCode);
                            ResultSet adjseat = psAdjacentSeatId.executeQuery();
                            int adjacentSeatId;
                            if (!adjseat.next()) {
                                PreparedStatement adj = connection.prepareStatement(
                                        "INSERT INTO Seat (Venue_ID, Seat_Code, Row_Number, Venue_Name) VALUES (?, ?, ?, ?)",
                                        Statement.RETURN_GENERATED_KEYS
                                );
                                adj.setInt(1, selectedVenueId);
                                adj.setString(2, adjacentSeatCode);
                                adj.setString(3, adjacentSeatCode.replaceAll("[0-9]", ""));
                                adj.setString(4, selectedVenueId == 1 ? "Main Hall" : "Small Hall");
                                adj.executeUpdate();
                                ResultSet adjacentKeys = adj.getGeneratedKeys();
                                adjacentSeatId = adjacentKeys.next() ? adjacentKeys.getInt(1) : -1;
                            } else {
                                adjacentSeatId = adjseat.getInt("Seat_ID");
                            }
                            PreparedStatement psDeleteAdjacent = connection.prepareStatement(
                                    "DELETE FROM Seat_Availability WHERE Show_ID = ? AND Seat_ID = ?"
                            );
                            psDeleteAdjacent.setInt(1, selectedShowId);
                            psDeleteAdjacent.setInt(2, adjacentSeatId);
                            psDeleteAdjacent.executeUpdate();

                         // adajacent seat = companion
                            PreparedStatement companSeat = connection.prepareStatement(
                                    "INSERT INTO Seat_Availability (Seat_ID, Show_ID, Status) VALUES (?, ?, ?)"
                            );
                            companSeat.setInt(1, adjacentSeatId);
                            companSeat.setInt(2, selectedShowId);
                            companSeat.setString(3, "Reserved"); // Companion status
                            companSeat.executeUpdate();
                        }
                    }
                }

                if ("Sold".equals(dbStatus)) {
                    PreparedStatement booking = connection.prepareStatement(
                            "INSERT INTO Booking (Patron_ID, Show_ID, Seat_ID, Venue_ID, Booking_Date, TotalCost) " +
                                    "VALUES (?, ?, ?, ?, NOW(), ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    booking.setInt(1, 1);
                    booking.setInt(2, selectedShowId);
                    booking.setInt(3, seatId);
                    booking.setInt(4, selectedVenueId);
                    booking.setDouble(5, 45.00);
                    booking.executeUpdate();
                    ResultSet bookingKeys = booking.getGeneratedKeys();
                    int bookingId = bookingKeys.next() ? bookingKeys.getInt(1) : -1;
                    PreparedStatement ticket = connection.prepareStatement(
                            "INSERT INTO Ticket (Ticket_Price, Seat_Code, Row_Number, Show_ID, Booking_ID, Seat_ID) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)"
                    );
                    ticket.setDouble(1, 45.00);
                    ticket.setString(2, seatCode);
                    ticket.setString(3, seatCode.replaceAll("[0-9]", ""));
                    ticket.setInt(4, selectedShowId);
                    ticket.setInt(5, bookingId);
                    ticket.setInt(6, seatId);
                    ticket.executeUpdate();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving seat status: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads available shows into the shows bar
     */
    private void loadShows() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Show_ID, Show_Title, Show_Date, Venue_ID, Hall_Type FROM Shows");
            showComboBox.removeAllItems();
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
            selectedShowId = Integer.parseInt(selectedShow.split(" - ")[0]);
            showSeatStatuses.put(selectedShowId, new HashMap<>()); // Clear previous statuses
            for (JLabel label : seatButtons.values()) {
                label.setBackground(AVAILABLE_COLOR);
                label.setForeground(Color.BLACK);
                label.setOpaque(true);
            }
            PreparedStatement psVenue = connection.prepareStatement(
                    "SELECT Venue_ID, Hall_Type FROM Shows WHERE Show_ID = ?"
            );
            psVenue.setInt(1, selectedShowId);
            ResultSet rsVenue = psVenue.executeQuery();
            if (rsVenue.next()) {
                selectedVenueId = rsVenue.getInt("Venue_ID");
                String hallType = rsVenue.getString("Hall_Type");
                tabbedPane.setSelectedIndex("Main Hall".equals(hallType) ? 0 : "Small Hall".equals(hallType) ? 1 : 2);
            }
            PreparedStatement avail = connection.prepareStatement(
                    "SELECT s.Seat_Code, sa.Status " +
                            "FROM Seat_Availability sa " +
                            "JOIN Seat s ON sa.Seat_ID = s.Seat_ID " +
                            "WHERE sa.Show_ID = ? AND s.Venue_ID = ?"
            );
            avail.setInt(1, selectedShowId);
            avail.setInt(2, selectedVenueId);
            ResultSet rs = avail.executeQuery();
            Map<String, String> currentShowStatuses = showSeatStatuses.get(selectedShowId);
            while (rs.next()) {
                String seatCode = rs.getString("Seat_Code");
                String dbStatus = rs.getString("Status");
                String status;
                switch (dbStatus) {
                    case "Sold": status = "Occupied"; break;
                    case "Discounted": status = "Discounted"; break;
                    case "Reserved":
                        if (seatCode.startsWith("A") || seatCode.startsWith("L")) {
                            String[] parts = seatCode.split("(?<=\\D)(?=\\d)");
                            if (parts.length == 2) {
                                int seatNum = Integer.parseInt(parts[1]);
                                String adjacentCode = parts[0] + (seatNum + 1);
                                PreparedStatement checkAdj = connection.prepareStatement(
                                        "SELECT Status FROM Seat_Availability WHERE Show_ID = ? AND Seat_ID = " +
                                                "(SELECT Seat_ID FROM Seat WHERE Venue_ID = ? AND Seat_Code = ?)"
                                );
                                checkAdj.setInt(1, selectedShowId);
                                checkAdj.setInt(2, selectedVenueId);
                                checkAdj.setString(3, adjacentCode);
                                ResultSet adjseat = checkAdj.executeQuery();
                                if (adjseat.next() && "Reserved".equals(adjseat.getString("Status"))) {
                                    status = seatNum % 2 == 0 ? "Companion" : "Disability";
                                } else {
                                    status = "Restricted";
                                }
                            } else {status = "Restricted";
                            }
                        } else {status = "Restricted";
                        }
                        break; default: status = "Available";
                }
                JLabel seatLabel = seatButtons.get(seatCode);
                if (seatLabel != null) {
                    updateSeatStatus(seatCode, seatLabel, status);
                    currentShowStatuses.put(seatCode, status);
                }
            }
            showSeatStatuses.put(selectedShowId, currentShowStatuses);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading seating: " + e.getMessage());
        }
    }

    /**
     * Saves the current seating configuration to the database for the selected show.
     */
    private void saveSeatingConfiguration() {
        if (selectedShowId <= 0 || selectedVenueId <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a show first.");
            return;
        }
        try {
            connection.setAutoCommit(false);

            Map<String, Integer> seatCodes = new HashMap<>();
            PreparedStatement seat = connection.prepareStatement(
                    "SELECT Seat_ID, Seat_Code FROM Seat WHERE Venue_ID = ?"
            );
            seat.setInt(1, selectedVenueId);
            ResultSet rsSeatIds = seat.executeQuery();
            while (rsSeatIds.next()) {
                seatCodes.put(rsSeatIds.getString("Seat_Code"), rsSeatIds.getInt("Seat_ID"));
            }
            PreparedStatement delAvail = connection.prepareStatement(
                    "DELETE FROM Seat_Availability WHERE Show_ID = ?"
            );
            delAvail.setInt(1, selectedShowId);
            delAvail.executeUpdate();
            PreparedStatement avail = connection.prepareStatement(
                    "INSERT INTO Seat_Availability (Seat_ID, Show_ID, Status) VALUES (?, ?, ?)"
            );
            Map<String, String> currentShowStatuses = showSeatStatuses.getOrDefault(selectedShowId, new HashMap<>());
            for (Map.Entry<String, String> entry : currentShowStatuses.entrySet()) {
                String seatCode = entry.getKey();
                String status = entry.getValue();
                Integer seatId = seatCodes.get(seatCode);
                if (seatId == null) continue;
                String dbStatus;
                switch (status.toLowerCase()) {
                    case "occupied": dbStatus = "Sold"; break;
                    case "discounted": dbStatus = "Discounted"; break;
                    case "restricted": dbStatus = "Reserved"; break;
                    case "disability": dbStatus = "Reserved"; break;
                    case "companion": dbStatus = "Reserved"; break;
                    case "available": dbStatus = "Available"; break;
                    default: dbStatus = "Available";
                }
                if (!"Available".equals(dbStatus)) {
                    avail.setInt(1, seatId);
                    avail.setInt(2, selectedShowId);
                    avail.setString(3, dbStatus);
                    avail.addBatch();
                    if ("Sold".equals(dbStatus)) {
                        PreparedStatement booking = connection.prepareStatement(
                                "INSERT INTO Booking (Patron_ID, Show_ID, Seat_ID, Venue_ID, Booking_Date, TotalCost) " +
                                        "VALUES (?, ?, ?, ?, NOW(), ?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        booking.setInt(1, 1);
                        booking.setInt(2, selectedShowId);
                        booking.setInt(3, seatId);
                        booking.setInt(4, selectedVenueId);
                        booking.setDouble(5, 45.00);
                        booking.executeUpdate();
                        ResultSet bookingKeys = booking.getGeneratedKeys();
                        int bookingId = bookingKeys.next() ? bookingKeys.getInt(1) : -1;
                        PreparedStatement ticket = connection.prepareStatement(
                                "INSERT INTO Ticket (Ticket_Price, Seat_Code, Row_Number, Show_ID, Booking_ID, Seat_ID) " +
                                        "VALUES (?, ?, ?, ?, ?, ?)"
                        );
                        ticket.setDouble(1, 45.00);
                        ticket.setString(2, seatCode);
                        ticket.setString(3, seatCode.replaceAll("[0-9]", ""));
                        ticket.setInt(4, selectedShowId);
                        ticket.setInt(5, bookingId);
                        ticket.setInt(6, seatId);
                        ticket.executeUpdate();
                    }
                }
            }
            avail.executeBatch();
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
    /**
     * room booking data
     */
    private void loadRoomsData(DefaultTableModel tableModel) {
        try {
            String query = "SELECT m.Patron_ID, m.Meeting_Name, v.Venue_Name, m.Start_DateTime, m.End_DateTime, " +
                    "m.Rate_Type, m.Duration_Type, m.Total_Cost " +
                    "FROM Meetings m " +
                    "JOIN Venue v ON m.Venue_ID = v.Venue_ID " +
                    "WHERE v.Venue_Name IN ('Rehearsal Room', 'The Green Room', 'Brontë Boardroom', " +
                    "'Dickens Den', 'Poe Parlour', 'Globe Room', 'Chekhov Chamber') " +
                    "ORDER BY m.Start_DateTime";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet res = ps.executeQuery();
            tableModel.setRowCount(0);
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("EEEE");

            while (res.next()) {
                String client = res.getString("Meeting_Name");
                String venueName = res.getString("Venue_Name");
                java.sql.Timestamp startTimestamp = res.getTimestamp("Start_DateTime");
                java.sql.Timestamp endTimestamp = res.getTimestamp("End_DateTime");
                String startDateTime = dateFormat.format(startTimestamp);
                String endDateTime = dateFormat.format(endTimestamp);
                String dayOfWeek = dayFormat.format(startTimestamp);
                String durationType = res.getString("Duration_Type");
                String totalCost = String.format("%.2f", res.getDouble("Total_Cost"));
                tableModel.addRow(new Object[]{
                        client, venueName, startDateTime, endDateTime, dayOfWeek, durationType, totalCost
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading rooms data: " + e.getMessage());
        }

    }
}