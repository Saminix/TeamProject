import UI.*;
// import the database connection
import db.dbConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

/**
 * This is the Dashboard file.
 * Enhanced with database-driven metric cards, Alerts and Notifications.
 * main homepage for the manager to see first.
 */
public class Dashboard {
    // initialise the basic colour scheme
    private static final Color PRIMARY_COLOR = new Color(34, 70, 26);
    private static final Color SECONDARY_COLOR = new Color(82, 109, 165);
    private static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color ALERT_COLOR = new Color(172, 28, 28);

    private static Connection connection;

    /**
     * Main method to establish connection and set panel
     * for testing purposes this class has a main method also for direct access to the dashboard page
     */

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            connection = dbConnection.getConnection(); // initialize database connection from db class
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setUndecorated(true);

            JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
            TitleBar titleBar = createCustomTitleBar();
            mainPanel.add(titleBar, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
            CardLayout cardLayout = new CardLayout();
            JPanel cardPanel = new JPanel(cardLayout);

            cardPanel.add(createDashboardContent(), "Home");
            cardPanel.add(createReportsPage(), "Reports");
            cardPanel.add(createTicketsPage(), "Tickets");
            cardPanel.add(createPatronPage(), "Patron");
            cardPanel.add(createSeatingPage(), "Seating");
            cardPanel.add(createRefundsPage(), "Refunds");


            // implement the navigation bar on the side of the dashboard
            // will be initialized for all relevant pages.
            NavigationBar sidebar = new NavigationBar(cardLayout, cardPanel);
            contentPanel.add(sidebar, BorderLayout.WEST);
            contentPanel.add(cardPanel, BorderLayout.CENTER);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            DragListener dragListener = new DragListener();
            titleBar.addMouseListener(dragListener);
            titleBar.addMouseMotionListener(dragListener);
            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }


    // Title bar - is the top panel bar that does not change when switching between pages.
    private static TitleBar createCustomTitleBar() {
        TitleBar titleBar = new TitleBar();
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setPreferredSize(new Dimension(0, 40));

        JLabel titleLabel = new JLabel("Lancaster Music Hall OS");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        titleBar.add(titleLabel, BorderLayout.WEST);

        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> System.exit(0));
        titleBar.add(closeButton, BorderLayout.EAST);

        return titleBar;
    }


    /**
     * creation of the dashboard, which is the homepage for the manager.
     * will have all relevant data on standby
     * @return the configured dashboard content
    */

    private static JPanel createDashboardContent() {
        JPanel dashboardContent = new JPanel(new BorderLayout());
        dashboardContent.setBackground(BACKGROUND_COLOR);
        dashboardContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // set the managers name here.
        JLabel welcomeLabel = new JLabel("Welcome back, Muhammad!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 23));
        welcomeLabel.setForeground(TEXT_COLOR);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
        // real- time date formatter
        String formattedDate = now.format(formatter);
        JLabel dateLabel = new JLabel(formattedDate);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(120, 120, 120));
        JPanel headerTextPanel = new JPanel(new GridLayout(2, 1));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(welcomeLabel);
        headerTextPanel.add(dateLabel);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);

        topSection.add(headerPanel, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardPanel.setOpaque(false);


        // Fetch data from the database
        try {
            // calculate the Total Revenue. the sum from box office reports table is received by year
            String revenueQuery = "SELECT SUM(Total_Revenue) as totalRevenue " +
                    "FROM BoxOfficeReport " +
                    "WHERE YEAR(Report_Date) = YEAR(CURDATE())";
            double totalRevenue = 0;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(revenueQuery)) {
                if (rs.next()) {
                    totalRevenue = rs.getDouble("totalRevenue");
                }
            }
            String revenueText = String.format("£%,.2f", totalRevenue);
            cardPanel.add(createMetricCard(revenueText, "Total Revenue", "This year", PRIMARY_COLOR));

            // Query for new Patrons - counting of new patrons added this month
            String patronsQuery = "SELECT COUNT(*) as newPatrons " +
                    "FROM Patron " +
                    "WHERE EXISTS (SELECT 1 FROM Booking b WHERE b.Patron_ID = Patron.Patron_ID " +
                    "AND b.Booking_Date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH))";
            int newPatrons = 0;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(patronsQuery)) {
                if (rs.next()) {
                    newPatrons = rs.getInt("newPatrons");
                }
            }
            cardPanel.add(createMetricCard(String.valueOf(newPatrons), "New Patrons", "This month", SECONDARY_COLOR));

            // Query for Ticket Sales
            String ticketSalesQuery = "SELECT COUNT(*) as ticketSales " +
                    "FROM Ticket " +
                    "WHERE Booking_ID IN (SELECT Booking_ID FROM Booking WHERE DATE(Booking_Date) = CURDATE()) " +
                    "AND IsSold = 1";
            int ticketSales = 0;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(ticketSalesQuery)) {
                if (rs.next()) {
                    ticketSales = rs.getInt("ticketSales");
                }
            }
            cardPanel.add(createMetricCard(String.valueOf(ticketSales), "Ticket Sales", "Today", new Color(255, 153, 0)));

            //  Query for profits calculates, by revenue - discounts - others
            String profitQuery = "SELECT (SUM(Total_Revenue) - COALESCE(SUM(Total_Discounted_Amount), 0)) as profit " +
                    "FROM BoxOfficeReport " +
                    "WHERE Report_Date >= DATE_SUB(CURDATE(), INTERVAL 1 WEEK)";
            double currentProfit = 0;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(profitQuery)) {
                if (rs.next()) {
                    currentProfit = rs.getDouble("profit");
                }
            }
            String profitText = String.format("£%,.2f", currentProfit);
            cardPanel.add(createMetricCard(profitText, "Current Profit", "This term", new Color(0, 153, 102)));

            // query for each card panel under correct label the data. error handling also
        } catch (SQLException e) {
            System.err.println("Error fetching dashboard data: " + e.getMessage());
            cardPanel.add(createMetricCard("£0.00", "Total Revenue", "Error", PRIMARY_COLOR));
            cardPanel.add(createMetricCard("0", "New Patrons", "Error", SECONDARY_COLOR));
            cardPanel.add(createMetricCard("0", "Ticket Sales", "Error", new Color(255, 153, 0)));
            cardPanel.add(createMetricCard("£0.00", "Current Profit", "Error", new Color(0, 153, 102)));
        }

        topSection.add(cardPanel, BorderLayout.CENTER);
        dashboardContent.add(topSection, BorderLayout.NORTH);
        // alerts and Notifications Panel - mainly to show new data being queried into the database. since box office handles new patrons
        // and the seating + refunds, good little functionality to add when user logs in.
        JPanel alertsPanel = createAlertsPanel();
        dashboardContent.add(alertsPanel, BorderLayout.SOUTH);
        return dashboardContent;
    }

    // notification panel, queues the data and formats the styling, to be able to scroll and see
    // priorities in colour coded format.
    private static JPanel createAlertsPanel() {
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.setBackground(CARD_COLOR);
        alertsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ALERT_COLOR),
                        "Alerts & Notifications",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12),
                        ALERT_COLOR
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));
        String[] alertsColumns = {"Priority", "Notice", "Message", "Date"};
        DefaultTableModel alertsTableModel = new DefaultTableModel(alertsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable alertsTable = new JTable(alertsTableModel);
        alertsTable.setRowHeight(30);
        alertsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        alertsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        alertsTable.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
            private final JLabel label = new JLabel();
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                label.setText(value.toString());
                label.setOpaque(true);
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
        alertsPanel.add(alertsScrollPane, BorderLayout.CENTER);
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPanel.setBackground(CARD_COLOR);
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton);
        refreshButton.addActionListener(e -> loadAlerts(alertsTableModel));
        controlsPanel.add(refreshButton);
        alertsPanel.add(controlsPanel, BorderLayout.SOUTH);
        loadAlerts(alertsTableModel);

        return alertsPanel;
    }

    // mixture and a compilation of SQL Queries to fetch data to the dashboard.

    private static void loadAlerts(DefaultTableModel alertsTableModel) {
        alertsTableModel.setRowCount(0);
        try {
            String patronQuery = "SELECT COUNT(*) as new_patrons FROM Patron " +
                    "WHERE Patron_ID NOT IN (SELECT DISTINCT Patron_ID FROM Booking)";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(patronQuery)) {
                if (rs.next()) {
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
            // for bookings ones
            String bookingQuery = "SELECT COUNT(*) as recent_cancellations FROM Booking " +
                    "WHERE IsCancelled = 1 AND Booking_Date >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(bookingQuery)) {
                if (rs.next()) {
                    int recentCancellations = rs.getInt("recent_cancellations");
                    if (recentCancellations > 0) {
                        Vector<Object> row = new Vector<>();
                        row.add("High");
                        row.add("Cancellations");
                        row.add(recentCancellations + " bookings cancelled in the last 7 days");
                        row.add(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        alertsTableModel.addRow(row);
                    }
                }
            }

            // Group booking data
            String groupQuery = "SELECT COUNT(*) as pending_groups FROM GroupBooking WHERE IsConfirmed = 0";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(groupQuery)) {
                if (rs.next()) {
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

            // LancasterFriend data
            String friendsQuery = "SELECT COUNT(*) as expiring_friends FROM LancasterFriend " +
                    "WHERE Subscription_End_Date <= DATE_ADD(NOW(), INTERVAL 30 DAY)";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(friendsQuery)) {
                if (rs.next()) {
                    int expiringFriends = rs.getInt("expiring_friends");
                    if (expiringFriends > 0) {
                        Vector<Object> row = new Vector<>();
                        row.add("Medium");
                        row.add("Subscriptions");
                        row.add(expiringFriends + " Lancaster Friend subscriptions expiring soon");
                        row.add(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        alertsTableModel.addRow(row);
                    }
                }
            }

            // Nearly sold-out shows - based on the seating config data.
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
            JOptionPane.showMessageDialog(null,
                    "Failed to load alerts data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }



    private static JPanel createMetricCard(String value, String title, String subtitle, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(4, 4, getWidth(), getHeight(), 15, 15);
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setOpaque(false);

        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        contentPanel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(120, 120, 120));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_COLOR);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(accentColor);
        contentPanel.add(titleLabel);
        contentPanel.add(valueLabel);
        contentPanel.add(subtitleLabel);
        card.add(contentPanel, BorderLayout.CENTER);

        JPanel indicator = new JPanel();
        indicator.setBackground(accentColor);
        indicator.setPreferredSize(new Dimension(0, 4));
        card.add(indicator, BorderLayout.NORTH);

        return card;
    }
    private static void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    // Page Creation Methods - Creates the page of every navigation bar - requirements.
    // DO NOT EDIT THIS to (programmers) It will call all relevant pages you need to implement.
    // - team leader

    private static JPanel createReportsPage() {
        return new ReportsPage();
    }
    private static JPanel createTicketsPage() {
        return new TicketsPage();
    }
    private static JPanel createPatronPage() {
        try {
            return new PatronPage();
        } catch (SQLException e) {
            e.printStackTrace();
            JPanel errorPanel = new JPanel();
            errorPanel.add(new JLabel("Database error: " + e.getMessage()));
            return errorPanel;
        }
    }
    private static JPanel createSeatingPage() {
        try {
            return new SeatingPage();
        } catch (SQLException e) {
            e.printStackTrace();
            JPanel errorPanel = new JPanel();
            errorPanel.add(new JLabel("Database error: " + e.getMessage()));
            return errorPanel;
        }
    }

    private static JPanel createRefundsPage() {
        try {
            return new RefundsPage();
        } catch (SQLException e) {
            e.printStackTrace();
            JPanel errorPanel = new JPanel();
            errorPanel.add(new JLabel("Database error: " + e.getMessage()));
            return errorPanel;
        }
    }

    private static class DragListener extends MouseAdapter {
        private Point startPoint;
        @Override
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            Point currentPoint = e.getLocationOnScreen();
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
            if (frame != null) {
                frame.setLocation(
                        currentPoint.x - startPoint.x,
                        currentPoint.y - startPoint.y
                );
            }
        }
    }
    private static class TitleBar extends JPanel {
        public TitleBar() {
            setLayout(new BorderLayout());
        }
    }
}