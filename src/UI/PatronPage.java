package UI;
import db.dbConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Patron Page
 * Handles the Customer Information, alerts, upcoming events,
 * Very Extensive data fetching.
 * and group bookings management
 */
public class PatronPage extends JPanel {
    // Colors
    private static final Color PRIMARY_COLOR = new Color(99, 107, 99);
    private static final Color SECONDARY_COLOR = new Color(58, 83, 135);
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 243);
    private static final Color CARD_COLOR = new Color(161, 188, 161);
    private static final Color TEXT_COLOR = new Color(0, 0, 0);
    private static final Color ALERT_COLOR = new Color(172, 28, 28);

    // Database connection
    private Connection connection;

    // UI Components
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JTable patronsTable;
    private DefaultTableModel patronsTableModel;
    private JTable alertsTable;
    private DefaultTableModel alertsTableModel;
    private JTable upcomingEventsTable;
    private DefaultTableModel upcomingEventsTableModel;
    private JTable groupBookingsTable;
    private DefaultTableModel groupBookingsTableModel;

    // Constructor
    public PatronPage() throws SQLException {
        // Get database connection from DBConnection class instead of creating a new one
        connection = dbConnection.getConnection();

        setupUI();
        loadData();
    }

    // Set up the UI components
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs - switch between the 3
        tabbedPane.addTab("Patron Information", createPatronPanel());
        tabbedPane.addTab("Alerts and Notifications", createAlertsPanel());
        tabbedPane.addTab("Group Bookings", createGroupBookingsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Create the patron information panel
    private JPanel createPatronPanel() {
        JPanel patronPanel = new JPanel(new BorderLayout(10, 10));
        patronPanel.setBackground(BACKGROUND_COLOR);
        patronPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Label for search
        JLabel searchLabel = new JLabel("Search Patrons:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Search field
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterPatronsTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterPatronsTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterPatronsTable();
            }
        });

        // Filter combo box
        String[] filterOptions = {"All", "Patron", "Military", "NHS", "Disability", "LancasterFriend"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> filterPatronsTable());

        // Search controls panel
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.setBackground(BACKGROUND_COLOR);
        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(new JLabel("Filter by:"));
        searchControlsPanel.add(filterComboBox);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CARD_COLOR);

        JButton PatronButton = new JButton("Full Details");
        JButton exportButton = new JButton("Export Data");

        // Style buttons

        styleButton(PatronButton);
        styleButton(exportButton);

        // Add button actions

        PatronButton.addActionListener(e -> viewSelectedPatron());
        exportButton.addActionListener(e -> exportPatronData());


        buttonPanel.add(PatronButton);
        buttonPanel.add(exportButton);

        searchPanel.add(searchControlsPanel, BorderLayout.WEST);
        searchPanel.add(buttonPanel, BorderLayout.EAST);

        // Create patrons table
        String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Telephone", "DOB", "Type", "Lancaster Friend"};
        patronsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) { // Lancaster Friend column
                    return Boolean.class;
                }
                return String.class;
            }
        };

        patronsTable = new JTable(patronsTableModel);
        patronsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patronsTable.setRowHeight(30);
        patronsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        patronsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane tableScrollPane = new JScrollPane(patronsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Patron details panel (shown when a patron is selected)


        // Add components to the patron panel
        patronPanel.add(searchPanel, BorderLayout.NORTH);
        patronPanel.add(tableScrollPane, BorderLayout.CENTER);


        return patronPanel;
    }


    // Create alerts panel
    private JPanel createAlertsPanel() {
        JPanel alertsPanel = new JPanel(new BorderLayout(10, 10));
        alertsPanel.setBackground(BACKGROUND_COLOR);
        alertsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 12));

        // Create a split pane for alerts and upcoming events
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setBackground(BACKGROUND_COLOR);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        // Alerts table
        JPanel alertsTablePanel = new JPanel(new BorderLayout());
        alertsTablePanel.setBackground(Color.white);
        alertsTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ALERT_COLOR),
                        "Alerts & Reminders",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12),
                        ALERT_COLOR
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String[] alertsColumns = {"Priority", "Notice", "Message", "Date"};
        alertsTableModel = new DefaultTableModel(alertsColumns, 0);
        alertsTable = new JTable(alertsTableModel);
        alertsTable.setRowHeight(30);
        alertsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        alertsTable.setFont(new Font("Arial", Font.PLAIN, 12));


        alertsTable.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
            private final JLabel label = new JLabel();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                label.setText(value.toString());
                label.setOpaque(true);


                //priority bookings and patrons - colours show the levels
                if (value.toString().equals("High")) {
                    label.setBackground(new Color(255, 200, 200));
                } else if (value.toString().equals("Medium")) {
                    label.setBackground(new Color(255, 235, 156));
                } else {
                    label.setBackground(new Color(200, 255, 200));
                }

                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                } else {
                    label.setForeground(table.getForeground());
                }

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return label;
            }
        });

        JScrollPane alertsScrollPane = new JScrollPane(alertsTable);
        alertsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        alertsTablePanel.add(alertsScrollPane, BorderLayout.CENTER);

        // Upcoming events panel
        JPanel upcomingEventsPanel = new JPanel(new BorderLayout());
        upcomingEventsPanel.setBackground(Color.white);
        upcomingEventsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR),
                        "Upcoming Events/Shows",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12),
                        SECONDARY_COLOR
                ),
                new EmptyBorder(10, 10, 10, 12)
        ));

        String[] eventsColumns = {"Show Title", "Date", "Time", "Venue", "Tickets Sold", "Capacity", "Status"};
        upcomingEventsTableModel = new DefaultTableModel(eventsColumns, 0);
        upcomingEventsTable = new JTable(upcomingEventsTableModel);
        upcomingEventsTable.setRowHeight(30);
        upcomingEventsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        upcomingEventsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        // Custom renderer for status column
        upcomingEventsTable.getColumnModel().getColumn(6).setCellRenderer(new TableCellRenderer() {
            private final JLabel label = new JLabel();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                label.setText(value.toString());
                label.setOpaque(true);

                // shows up if a show is sold out = all seats gone
                if (value.toString().equals("Sold Out")) {
                    label.setBackground(new Color(255, 200, 200));
                } else if (value.toString().equals("Almost Full")) {
                    label.setBackground(new Color(255, 235, 156));
                } else {
                    label.setBackground(new Color(200, 255, 200));
                }

                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                } else {
                    label.setForeground(table.getForeground());
                }

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return label;
            }
        });

        JScrollPane eventsScrollPane = new JScrollPane(upcomingEventsTable);
        eventsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        upcomingEventsPanel.add(eventsScrollPane, BorderLayout.CENTER);

        // Add components to split pane
        splitPane.setTopComponent(alertsTablePanel);
        splitPane.setBottomComponent(upcomingEventsPanel);

        // Add controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPanel.setBackground(BACKGROUND_COLOR);

        JButton refreshButton = new JButton("Refresh Data");


        styleButton(refreshButton);


        refreshButton.addActionListener(e -> loadAlertsAndEvents());


        controlsPanel.add(refreshButton);


        // Add to main panel
        alertsPanel.add(splitPane, BorderLayout.CENTER);
        alertsPanel.add(controlsPanel, BorderLayout.SOUTH);

        return alertsPanel;
    }

    // Create group bookings panel
    private JPanel createGroupBookingsPanel() {
        JPanel groupBookingsPanel = new JPanel(new BorderLayout(10, 10));
        groupBookingsPanel.setBackground(BACKGROUND_COLOR);
        groupBookingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel("Search Groups:");
        JTextField groupSearchField = new JTextField(20);

        searchPanel.add(searchLabel);
        searchPanel.add(groupSearchField);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(BACKGROUND_COLOR);


        //  manage the group bookings under 12

        JButton manageGroupButton = new JButton("Manage Selected");
        JButton exportGroupsButton = new JButton("Export Groups");


        styleButton(manageGroupButton);
        styleButton(exportGroupsButton);


        manageGroupButton.addActionListener(e -> manageSelectedGroup());
        exportGroupsButton.addActionListener(e -> exportGroupData());


        buttonsPanel.add(manageGroupButton);
        buttonsPanel.add(exportGroupsButton);

        // Controls panel combining search and buttons
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        controlsPanel.add(searchPanel, BorderLayout.WEST);
        controlsPanel.add(buttonsPanel, BorderLayout.EAST);

        // Group bookings table
        String[] groupColumns = {"Group ID", "Group Name", "Contact Name", "Contact Email", "Group Size", "Booking ID", "Show Title", "Date", "Status"};
        groupBookingsTableModel = new DefaultTableModel(groupColumns, 0);
        groupBookingsTable = new JTable(groupBookingsTableModel);
        groupBookingsTable.setRowHeight(30);
        groupBookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        groupBookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane groupsScrollPane = new JScrollPane(groupBookingsTable);
        groupsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Custom renderer for status column
        groupBookingsTable.getColumnModel().getColumn(8).setCellRenderer(new TableCellRenderer() {
            private final JLabel label = new JLabel();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                label.setText(value.toString());
                label.setOpaque(true);

                if (value.toString().equals("Confirmed")) {
                    label.setBackground(new Color(200, 255, 200));
                } else if (value.toString().equals("Pending")) {
                    label.setBackground(new Color(255, 235, 156));
                } else {
                    label.setBackground(new Color(255, 200, 200));
                }

                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                } else {
                    label.setForeground(table.getForeground());
                }

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return label;
            }
        });


        // Add search functionality
        groupSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterGroupBookings(groupSearchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterGroupBookings(groupSearchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterGroupBookings(groupSearchField.getText());
            }
        });

        // Add components to the panel
        groupBookingsPanel.add(controlsPanel, BorderLayout.NORTH);
        groupBookingsPanel.add(groupsScrollPane, BorderLayout.CENTER);

        return groupBookingsPanel;
    }

    // Load data from database - call heper methods
    private void loadData() {
        loadPatronsData();
        loadAlertsAndEvents();
        loadGroupBookingsData();
    }

    // Load patrons data from database
    private void loadPatronsData() {
        // Clear existing data
        patronsTableModel.setRowCount(0);

        try {
            // intiate a SQL statement
            String query = "SELECT * FROM Patron";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("Patron_ID"));
                    row.add(rs.getString("First_Name"));
                    row.add(rs.getString("Last_Name"));
                    row.add(rs.getString("Email"));
                    row.add(rs.getString("Telephone"));
                    row.add(rs.getDate("Date_of_Birth"));
                    row.add(rs.getString("Patron_Type"));
                    row.add(rs.getBoolean("IsLancasterFriend"));

                    patronsTableModel.addRow(row);
                    // add to the model row
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading patron data: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Failed to load patron data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load alerts and upcoming events
    private void loadAlertsAndEvents() {
        loadAlerts();
        loadUpcomingEvents();
    }

    // Load alerts data
    private void loadAlerts() {
        // Clear existing data
        alertsTableModel.setRowCount(0);

        try {
            // Add patron-related alerts
            String patronQuery = "SELECT COUNT(*) as new_patrons FROM Patron " +
                    "WHERE Patron_ID NOT IN (SELECT DISTINCT Patron_ID FROM Booking)";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(patronQuery)) {

                if (rs.next()) {
                    // notice of new patrons or new customers -
                    int newPatrons = rs.getInt("new_patrons");
                    if (newPatrons > 0) {
                        Vector<Object> row = new Vector<>();
                        row.add("Medium");
                        row.add("New Patrons");
                        row.add(newPatrons + " new patrons have not made bookings yet");
                        row.add(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        alertsTableModel.addRow(row);
                    }
                }
            }

            // Add booking-related alerts
            String bookingQuery = "SELECT COUNT(*) as recent_cancellations FROM Booking " +
                    "WHERE IsCancelled = 1 AND Booking_Date >= DATE_SUB(NOW(), INTERVAL 7 DAY)";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(bookingQuery)) {

                if (rs.next()) {
                    //canceled != refund.
                    // implemented this so that the manager can see general cancellations
                    // the refunds page is the actual functionality for the manager to accept or reject.
                    int recentCancellations = rs.getInt("recent_cancellations");
                    if (recentCancellations > 0) {
                        Vector<Object> row = new Vector<>();
                        // cancellations could be a priority, since a refund could be waiting..
                        row.add("High");
                        row.add("Cancellations");
                        //check the week
                        row.add(recentCancellations + " bookings cancelled in the last 7 days");
                        row.add(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        alertsTableModel.addRow(row);
                    }
                }
            }

            // Add group booking alerts
            String groupQuery = "SELECT COUNT(*) as pending_groups FROM GroupBooking WHERE IsConfirmed = 0";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(groupQuery)) {

                if (rs.next()) {
                    // to handle group bookings to determine if this group booking is ideally one under 12
                    // managing these group bookings - the manager can confirm himself to accept.
                    int pendingGroups = rs.getInt("pending_groups");
                    if (pendingGroups > 0) {
                        Vector<Object> row = new Vector<>();
                        row.add("High");
                        row.add("Group Bookings");
                        row.add(pendingGroups + " pending confirmation for group bookings");
                        row.add(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        alertsTableModel.addRow(row);
                    }
                }
            }

            // Add LancasterFriend expiration alerts
            String friendsQuery = "SELECT COUNT(*) as expiring_friends FROM LancasterFriend " +
                    "WHERE Subscription_End_Date <= DATE_ADD(NOW(), INTERVAL 30 DAY)";

            // this functionality was not really needed, but since lancasterfriends implements it way as a
            // subscription - an alert to the manager makes more sense.
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(friendsQuery)) {

                if (rs.next()) {


                    int expiringFriends = rs.getInt("expiring_friends");
                    if (expiringFriends > 0) {
                        Vector<Object> row = new Vector<>();
                        row.add("Medium");
                        row.add("Subscriptions");
                        row.add(expiringFriends + " Lancaster Friend subscriptions expiring soon :(");
                        row.add(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        alertsTableModel.addRow(row);
                    }
                }
            }

            // for updating the show. shows to add to site.
            // Add reminder for nearly sold-out shows and seating vacancy. more implementation is gonna be on the seatin page.
            String soldOutQuery = "SELECT s.Show_Title, s.Show_Date, " +
                    "(SELECT COUNT(*) FROM Seat_Availability sa WHERE sa.Show_ID = s.Show_ID AND sa.Status = 'Sold') as sold, " +
                    "v.Capacity " +
                    "FROM Shows s " +
                    "JOIN Venue v ON s.Venue_ID = v.Venue_ID " +
                    "WHERE s.Show_Date >= CURDATE() " +
                    "ORDER BY s.Show_Date";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(soldOutQuery)) {

                while (rs.next()) {
                    String showTitle = rs.getString("Show_Title");
                    java.sql.Date showDate = rs.getDate("Show_Date");
                    int soldTickets = rs.getInt("sold");
                    int capacity = rs.getInt("Capacity");

                    double soldPercentage = (double) soldTickets / capacity * 100;

                    if (soldPercentage >= 85) {
                        Vector<Object> row = new Vector<>();

                        if (soldPercentage >= 95) {
                            row.add("High");
                            row.add("Almost Sold Out");
                            row.add(showTitle + " (" + showDate + ") is " + Math.round(soldPercentage) + "% sold out");
                        } else {
                            row.add("Medium");
                            row.add("High Demand");
                            row.add(showTitle + " (" + showDate + ") is " + Math.round(soldPercentage) + "% sold out");
                        }

                        row.add(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        alertsTableModel.addRow(row);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading alerts data: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Failed to load alerts data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load upcoming events
    // date set to 2025 ahead. for real time
    private void loadUpcomingEvents() {
        // Clear existing data
        upcomingEventsTableModel.setRowCount(0);

        try {
            String query = "SELECT s.Show_ID, s.Show_Title, s.Show_Date, s.Show_Start_Time, " +
                    "v.Venue_Name, v.Capacity, " +
                    "(SELECT COUNT(*) FROM Booking b WHERE b.Show_ID = s.Show_ID AND b.IsCancelled = 0) as tickets_sold " +
                    "FROM Shows s " +
                    "JOIN Venue v ON s.Venue_ID = v.Venue_ID " +
                    "WHERE s.Show_Date >= CURDATE() " +
                    "ORDER BY s.Show_Date LIMIT 20";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("Show_Title"));
                    row.add(rs.getDate("Show_Date"));
                    row.add(rs.getTime("Show_Start_Time"));
                    row.add(rs.getString("Venue_Name"));

                    int ticketsSold = rs.getInt("tickets_sold");
                    int capacity = rs.getInt("Capacity");

                    row.add(ticketsSold);
                    row.add(capacity);

                    // Calculate status
                    // correct for small hall.
                    double percentage = (double) ticketsSold / capacity * 100;
                    if (percentage >= 95) {
                        row.add("Sold Out");
                    } else if (percentage >= 75) {
                        row.add("Almost Full");
                    } else {
                        row.add("Available");
                    }

                    upcomingEventsTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading upcoming events: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Failed to load upcoming events: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load group bookings data
    private void loadGroupBookingsData() {
        // Clear existing data
        groupBookingsTableModel.setRowCount(0);

        try {
            String query = "SELECT gb.Group_Booking_ID, gb.Group_Name, gb.Contact_Name, gb.Contact_Email, " +
                    "gb.Group_Size, gb.Booking_ID, gb.IsConfirmed, s.Show_Title, s.Show_Date " +
                    "FROM GroupBooking gb " +
                    "JOIN Booking b ON gb.Booking_ID = b.Booking_ID " +
                    "JOIN Shows s ON b.Show_ID = s.Show_ID " +
                    "ORDER BY s.Show_Date";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("Group_Booking_ID"));
                    row.add(rs.getString("Group_Name"));
                    row.add(rs.getString("Contact_Name"));
                    row.add(rs.getString("Contact_Email"));
                    row.add(rs.getInt("Group_Size"));
                    row.add(rs.getInt("Booking_ID"));
                    row.add(rs.getString("Show_Title"));
                    row.add(rs.getDate("Show_Date"));

                    // Status based on confirmation
                    boolean isConfirmed = rs.getBoolean("IsConfirmed");
                    row.add(isConfirmed ? "Confirmed" : "Pending");

                    groupBookingsTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading group bookings: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Failed to load group bookings: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Filter patrons table based on search text and filter
    private void filterPatronsTable() {
        String searchText = searchField.getText().toLowerCase();
        String filterType = (String) filterComboBox.getSelectedItem();

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(patronsTable.getModel());
        patronsTable.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Add search filter if text is provided
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 1, 2, 3, 4)); // First name, last name, email, telephone
        }

        // Add type filter if not "All"
        if (!filterType.equals("All")) {
            filters.add(RowFilter.regexFilter("^" + filterType + "$", 6));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filters.size() == 1) {
            sorter.setRowFilter(filters.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    // Filter group bookings based on search text
    private void filterGroupBookings(String searchText) {
        if (searchText.isEmpty()) {
            groupBookingsTable.setRowSorter(null);
            return;
        }

        searchText = searchText.toLowerCase();
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(groupBookingsTable.getModel());
        groupBookingsTable.setRowSorter(sorter);

        RowFilter<Object, Object> filter = RowFilter.regexFilter("(?i)" + searchText, 1, 2, 3, 6); // Group name, contact name, email, show title
        sorter.setRowFilter(filter);
    }


    private void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.black);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Arial", Font.BOLD, 12));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40, 90, 25));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.gray);
            }
        });
    }


    // Edit the selected patron
    private void viewSelectedPatron() {
        int selectedRow = patronsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patron to view",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        int modelRow = patronsTable.convertRowIndexToModel(selectedRow);

        int patronId = (int) patronsTableModel.getValueAt(modelRow, 0);
        String firstName = (String) patronsTableModel.getValueAt(modelRow, 1);
        String lastName = (String) patronsTableModel.getValueAt(modelRow, 2);
        String email = (String) patronsTableModel.getValueAt(modelRow, 3);
        String phone = (String) patronsTableModel.getValueAt(modelRow, 4);
        Object dob = patronsTableModel.getValueAt(modelRow, 5);
        String type = (String) patronsTableModel.getValueAt(modelRow, 6);
        boolean isLancasterFriend = (boolean) patronsTableModel.getValueAt(modelRow, 7);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Patron Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        // view patron details
        JPanel viewPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        viewPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        JLabel patronIdValueLabel = new JLabel(String.valueOf(patronId));
        JLabel firstNameValueLabel = new JLabel(firstName);
        JLabel lastNameValueLabel = new JLabel(lastName);
        JLabel emailValueLabel = new JLabel(email);
        JLabel phoneValueLabel = new JLabel(phone);
        JLabel dobValueLabel = new JLabel(dob != null ? dob.toString() : "");
        JLabel typeValueLabel = new JLabel(type);
        JLabel friendValueLabel = new JLabel(isLancasterFriend ? "Yes" : "No");


        String[] patronTypes = {"Patron", "Military", "NHS Personal", "Disability", "LancasterFriend"};
        JComboBox<String> typeComboBox = new JComboBox<>(patronTypes);
        typeComboBox.setSelectedItem(type);



        // Add fields to form
        viewPanel.add(new JLabel("Patron ID:"));
        viewPanel.add(patronIdValueLabel);
        viewPanel.add(new JLabel("First Name:"));
        viewPanel.add(firstNameValueLabel);
        viewPanel.add(new JLabel("Last Name:"));
        viewPanel.add(lastNameValueLabel);
        viewPanel.add(new JLabel("Email:"));
        viewPanel.add(emailValueLabel);
        viewPanel.add(new JLabel("Phone:"));
        viewPanel.add(phoneValueLabel);
        viewPanel.add(new JLabel("Date of Birth:"));
        viewPanel.add(dobValueLabel);
        viewPanel.add(new JLabel("Patron Type:"));
        viewPanel.add(typeValueLabel);
        viewPanel.add(new JLabel("Lancaster Friend:"));
        viewPanel.add(friendValueLabel);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");


        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));


        buttonsPanel.add(cancelButton);

        dialog.add(viewPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }


    // Export patron data to CSV - good for data export.
    private void exportPatronData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Patron Data");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }

            try (PrintWriter writer = new PrintWriter(new File(filePath))) {
                // Write header
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < patronsTableModel.getColumnCount(); i++) {
                    header.append(patronsTableModel.getColumnName(i));
                    if (i < patronsTableModel.getColumnCount() - 1) {
                        header.append(",");
                    }
                }
                writer.println(header.toString());

                // Write data
                for (int row = 0; row < patronsTableModel.getRowCount(); row++) {
                    StringBuilder sb = new StringBuilder();
                    for (int col = 0; col < patronsTableModel.getColumnCount(); col++) {
                        Object value = patronsTableModel.getValueAt(row, col);
                        sb.append(value != null ? value.toString() : "");
                        if (col < patronsTableModel.getColumnCount() - 1) {
                            sb.append(",");
                        }
                    }
                    writer.println(sb.toString());
                }

                JOptionPane.showMessageDialog(this,
                        "Patron data exported successfully to " + filePath,
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting data: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Manage selected group booking
    private void manageSelectedGroup() {
        int selectedRow = groupBookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a group booking to manage",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        int modelRow = groupBookingsTable.convertRowIndexToModel(selectedRow);

        int groupId = (int) groupBookingsTableModel.getValueAt(modelRow, 0);
        String groupName = (String) groupBookingsTableModel.getValueAt(modelRow, 1);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Group Booking: " + groupName, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        // maange the group bookings
        JButton viewDetailsButton = new JButton("View Full Details");
        JButton confirmButton = new JButton("Confirm Booking");
        JButton cancelButton = new JButton("Cancel Booking");
        JButton closeButton = new JButton("Close");

        styleButton(viewDetailsButton);
        styleButton(confirmButton);


        cancelButton.setBackground(new Color(255, 100, 100));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));

        closeButton.setBackground(new Color(200, 200, 200));
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));

        viewDetailsButton.addActionListener(e -> {
          // show details of booking
            StringBuilder details = new StringBuilder();
            details.append("Group ID: ").append(groupId).append("\n");
            details.append("Group Name: ").append(groupName).append("\n");
            details.append("Contact: ").append(groupBookingsTableModel.getValueAt(modelRow, 2)).append("\n");
            details.append("Email: ").append(groupBookingsTableModel.getValueAt(modelRow, 3)).append("\n");
            details.append("Size: ").append(groupBookingsTableModel.getValueAt(modelRow, 4)).append("\n");
            details.append("Booking ID: ").append(groupBookingsTableModel.getValueAt(modelRow, 5)).append("\n");
            details.append("Show: ").append(groupBookingsTableModel.getValueAt(modelRow, 6)).append("\n");
            details.append("Date: ").append(groupBookingsTableModel.getValueAt(modelRow, 7)).append("\n");
            details.append("Status: ").append(groupBookingsTableModel.getValueAt(modelRow, 8));

            JTextArea detailsArea = new JTextArea(details.toString());
            detailsArea.setEditable(false);
            detailsArea.setLineWrap(true);
            detailsArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setPreferredSize(new Dimension(300, 200));

            JOptionPane.showMessageDialog(dialog,
                    scrollPane,
                    "Group Booking Details",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        confirmButton.addActionListener(e -> {
            try {
                String updateQuery = "UPDATE GroupBooking SET IsConfirmed = 1 WHERE Group_Booking_ID = ?";

                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setInt(1, groupId);

                    int result = pstmt.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(dialog,
                                "Group booking confirmed successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadGroupBookingsData(); // Refresh table
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error confirming group booking: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });



        cancelButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to cancel this group booking?\nThis action cannot be undone.",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                try {
                    // In a real application, you might not delete the booking but mark it as cancelled
                    String updateQuery = "UPDATE GroupBooking SET IsConfirmed = 0 WHERE Group_Booking_ID = ?";

                    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                        pstmt.setInt(1, groupId);

                        int result = pstmt.executeUpdate();

                        if (result > 0) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Group booking has been cancelled.",
                                    "Booking Cancelled",
                                    JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                            loadGroupBookingsData(); // Refresh table
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error cancelling group booking: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(viewDetailsButton);
        buttonsPanel.add(confirmButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(closeButton);

        dialog.add(buttonsPanel, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    // Export group booking data to CSV
    private void exportGroupData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Group Bookings Data");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }

            try (PrintWriter writer = new PrintWriter(new File(filePath))) {
                // Write header
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < groupBookingsTableModel.getColumnCount(); i++) {
                    header.append(groupBookingsTableModel.getColumnName(i));
                    if (i < groupBookingsTableModel.getColumnCount() - 1) {
                        header.append(",");
                    }
                }
                writer.println(header.toString());

                // Write data
                for (int row = 0; row < groupBookingsTableModel.getRowCount(); row++) {
                    StringBuilder sb = new StringBuilder();
                    for (int col = 0; col < groupBookingsTableModel.getColumnCount(); col++) {
                        Object value = groupBookingsTableModel.getValueAt(row, col);
                        sb.append(value != null ? value.toString() : "");
                        if (col < groupBookingsTableModel.getColumnCount() - 1) {
                            sb.append(",");
                        }
                    }
                    writer.println(sb.toString());
                }

                JOptionPane.showMessageDialog(this,
                        "Group bookings data exported successfully to " + filePath,
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting data: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}