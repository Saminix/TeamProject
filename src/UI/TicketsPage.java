package UI;
import db.dbConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Tickets Page - handles Extensive management and search of all ticket data, revenue, cost, production, seats, discounts,
 * has an extensive layout of displaying the ticket data
 * manager can successfully add a new show to site, manage the shows, and fetch the sales report.
 * filters are made to search through ticket data.
 * implementation of tabs for easy navigation
 */
public class TicketsPage extends JPanel {
    // Colors from PatronPage
    private static final Color PRIMARY_COLOR = new Color(99, 107, 99);
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 243);
    private static final Color CARD_COLOR = new Color(200, 200, 200);
    private static final Color TEXT_COLOR = new Color(0, 0, 0);
    private static final Color HEADER_COLOR = new Color(41, 15, 77);
    private static final Color GREEN_BUTTON_COLOR = new Color(0, 128, 0);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 12);

    private JPanel ticketListPanel;
    private JComboBox<String> showSelector;
    private JComboBox<String> venueSelector;
    private JTextField dateField;
    private JButton datePickerButton;
    private Map<Integer, JSpinner> quantitySpinners;
    private JLabel totalLabel;
    private double currentTotal;
    private int selectedPatronId = -1;
    private JTabbedPane tabbedPane;
    private JTextField ticketSearchField;
    private JTable ticketsTable;
    private JTextField discountSearchField;
    private JTable discountsTable;


    // initialise the tickets page
    public TicketsPage() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        quantitySpinners = new HashMap<>();
        currentTotal = 0.0;
        setupUI();
        loadInitialData();
        loadTicketsData("");
    }


    // add tabbed panes = tabs
    private void setupUI() {
        tabbedPane = new JTabbedPane();
        JPanel purchaseTab = createPurchaseTab();
        tabbedPane.addTab("Purchase Tickets", purchaseTab);
        JPanel ticketsTab = createTicketsTab();
        tabbedPane.addTab("All Tickets", ticketsTab);
        JPanel discountsTab = createDiscountsTab();
        tabbedPane.addTab("Discounts", discountsTab);
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**purchase tickets tab - shows the costs of different types of tickets depending on the cost type or the patron type,
     * @return the purchase tab
    */

    private JPanel createPurchaseTab() {
        JPanel purchasePanel = new JPanel(new BorderLayout(10, 10));
        purchasePanel.setBackground(Color.white);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        showSelector = new JComboBox<>();
        showSelector.setPreferredSize(new Dimension(200, 30));
        showSelector.addActionListener(e -> updateTicketDisplay());
        venueSelector = new JComboBox<>();
        venueSelector.setPreferredSize(new Dimension(150, 30));
        venueSelector.addActionListener(e -> updateTicketDisplay());
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.setBackground(Color.WHITE);
        dateField = new JTextField(10);
        dateField.setPreferredSize(new Dimension(90, 30));
        dateField.setEditable(false);
        datePickerButton = new JButton("Select Date");
        datePickerButton.setPreferredSize(new Dimension(100, 30));
        datePickerButton.setFocusPainted(false);
        datePickerButton.addActionListener(e -> showDatePicker());
        datePanel.add(dateField);
        datePanel.add(datePickerButton);
        controlPanel.add(new JLabel("Show:"));
        controlPanel.add(showSelector);
        controlPanel.add(new JLabel("Venue:"));
        controlPanel.add(venueSelector);
        controlPanel.add(new JLabel("Date:"));
        controlPanel.add(datePanel);

        purchasePanel.add(controlPanel, BorderLayout.NORTH);
        ticketListPanel = new JPanel();
        ticketListPanel.setLayout(new BoxLayout(ticketListPanel, BoxLayout.Y_AXIS));
        ticketListPanel.setBackground(Color.WHITE);
        ticketListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(ticketListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        purchasePanel.add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // panel to purchase a ticket for a recurring customer - implements the manager being in charge of ticket data.
        totalLabel = new JLabel("Purchase Total: £0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton purchaseButton = createStyledButton("Purchase Tickets", GREEN_BUTTON_COLOR);
        purchaseButton.addActionListener(e -> processTicketPurchase());
        Color DARK_GREEN_BUTTON_COLOR = new Color(0, 100, 0);
        JButton addShowButton = createStyledButton("Add New Show", DARK_GREEN_BUTTON_COLOR);
        addShowButton.addActionListener(e -> showAddShowDialog());
        JButton manageShowsButton = createStyledButton("Manage Shows", DARK_GREEN_BUTTON_COLOR);
        manageShowsButton.addActionListener(e -> showManageShowsDialog());

        JButton salesReportButton = createStyledButton("Sales Report", DARK_GREEN_BUTTON_COLOR);
        salesReportButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                salesReportButton.setForeground(Color.YELLOW);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                salesReportButton.setForeground(Color.WHITE);
                showSalesReportDialog();
            }
        });

        buttonPanel.add(purchaseButton);
        buttonPanel.add(addShowButton);
        buttonPanel.add(manageShowsButton);
        buttonPanel.add(salesReportButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        purchasePanel.add(bottomPanel, BorderLayout.SOUTH);

        return purchasePanel;
    }

    private JPanel createTicketsTab() {
        JPanel ticketsPanel = new JPanel(new BorderLayout(10, 10));
        ticketsPanel.setBackground(BACKGROUND_COLOR);
        ticketsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.setBackground(BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel("Search Tickets:");
        searchLabel.setFont(HEADER_FONT);
        searchLabel.setForeground(TEXT_COLOR);

        ticketSearchField = new JTextField(20);
        ticketSearchField.setFont(CONTENT_FONT);

        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        searchButton.addActionListener(e -> loadTicketsData(ticketSearchField.getText().trim()));

        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(ticketSearchField);
        searchControlsPanel.add(searchButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CARD_COLOR);

        JButton exportButton = createStyledButton("Export Data", PRIMARY_COLOR);
        exportButton.addActionListener(e -> exportTicketsData());

        buttonPanel.add(exportButton);

        searchPanel.add(searchControlsPanel, BorderLayout.WEST);
        searchPanel.add(buttonPanel, BorderLayout.EAST);

        // Tickets Table
        String[] columnNames = {
                "Ticket ID", "Price", "Seat Code", "Row", "Discount", "Wheelchair",
                "Restricted View", "Sold", "Show Title", "Booking ID", "Seat ID"
        };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketsTable = new JTable(tableModel);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketsTable.setRowHeight(30);
        ticketsTable.getTableHeader().setFont(HEADER_FONT);
        ticketsTable.getTableHeader().setBackground(PRIMARY_COLOR);
        ticketsTable.getTableHeader().setForeground(Color.WHITE);
        ticketsTable.setFont(CONTENT_FONT);
        ticketsTable.setBackground(Color.WHITE);
        ticketsTable.setForeground(TEXT_COLOR);
        ticketsTable.setShowGrid(false);

        JScrollPane tableScrollPane = new JScrollPane(ticketsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        ticketsPanel.add(searchPanel, BorderLayout.NORTH);
        ticketsPanel.add(tableScrollPane, BorderLayout.CENTER);

        return ticketsPanel;
    }


    /**
     * searchable table displaying all discount data with an option to export to CSV.
     * @return the discounts tab
     */

    private JPanel createDiscountsTab() {
        JPanel discountsPanel = new JPanel(new BorderLayout(10, 10));
        discountsPanel.setBackground(BACKGROUND_COLOR);
        discountsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.setBackground(BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel("Search Discounts:");
        searchLabel.setFont(HEADER_FONT);
        searchLabel.setForeground(TEXT_COLOR);
        discountSearchField = new JTextField(20);
        discountSearchField.setFont(CONTENT_FONT);
        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        searchButton.addActionListener(e -> loadDiscountsData(discountSearchField.getText().trim()));
        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(discountSearchField);
        searchControlsPanel.add(searchButton);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CARD_COLOR);
        JButton exportButton = createStyledButton("Export Data", PRIMARY_COLOR);
        exportButton.addActionListener(e -> exportDiscountsData());
        buttonPanel.add(exportButton);
        searchPanel.add(searchControlsPanel, BorderLayout.WEST);
        searchPanel.add(buttonPanel, BorderLayout.EAST);
        String[] columnNames = {
                "Discount ID", "Type", "Percentage", "Date Issued", "Date Expiry", "Used"
        };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        discountsTable = new JTable(tableModel);
        discountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        discountsTable.setRowHeight(30);
        discountsTable.getTableHeader().setFont(HEADER_FONT);
        discountsTable.getTableHeader().setBackground(PRIMARY_COLOR);
        discountsTable.getTableHeader().setForeground(Color.WHITE);
        discountsTable.setFont(CONTENT_FONT);
        discountsTable.setBackground(Color.WHITE);
        discountsTable.setForeground(TEXT_COLOR);
        discountsTable.setShowGrid(false);
        JScrollPane tableScrollPane = new JScrollPane(discountsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        discountsPanel.add(searchPanel, BorderLayout.NORTH);
        discountsPanel.add(tableScrollPane, BorderLayout.CENTER);

        loadDiscountsData("");
        return discountsPanel;
    }



    /**
     * search ticket data into the tickets
     *
     * @param searchTerm to filter tickets
     */
    private void loadTicketsData(String searchTerm) {
        DefaultTableModel model = (DefaultTableModel) ticketsTable.getModel();
        model.setRowCount(0);
        // query the database for the tickets data.
        String query = "SELECT t.Ticket_ID, t.Ticket_Price, t.Seat_Code, t.Row_Number, t.Discount_Amount, " +
                "t.IsWheelchairAccessible, t.IsRestrictedView, t.IsSold, s.Show_Title, t.Booking_ID, t.Seat_ID " +
                "FROM Ticket t " +
                "LEFT JOIN Shows s ON t.Show_ID = s.Show_ID " +
                (searchTerm.isEmpty() ? "" : "WHERE s.Show_Title LIKE ? OR t.Seat_Code LIKE ? OR t.Row_Number LIKE ?") +
                " ORDER BY t.Ticket_ID";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (!searchTerm.isEmpty()) {
                String likeTerm = "%" + searchTerm + "%";
                pstmt.setString(1, likeTerm);
                pstmt.setString(2, likeTerm);
                pstmt.setString(3, likeTerm);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Ticket_ID"),
                        String.format("£%.2f", rs.getDouble("Ticket_Price")),
                        rs.getString("Seat_Code"),
                        rs.getString("Row_Number"),
                        String.format("£%.2f", rs.getDouble("Discount_Amount")),
                        rs.getBoolean("IsWheelchairAccessible") ? "Yes" : "No",
                        rs.getBoolean("IsRestrictedView") ? "Yes" : "No",
                        rs.getBoolean("IsSold") ? "Yes" : "No",
                        rs.getString("Show_Title"),
                        rs.getObject("Booking_ID") != null ? rs.getInt("Booking_ID") : "N/A",
                        rs.getInt("Seat_ID")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading tickets: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * search discount data
     */
    private void loadDiscountsData(String searchTerm) {
        DefaultTableModel model = (DefaultTableModel) discountsTable.getModel();
        model.setRowCount(0);

        String query = "SELECT Discount_ID, Discount_Type, Discount_Percentage, Date_Issued, Date_Expiry, IsUsed " +
                "FROM Discount " +
                (searchTerm.isEmpty() ? "" : "WHERE Discount_Type LIKE ?") +
                " ORDER BY Discount_ID";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            if (!searchTerm.isEmpty()) {
                String likeTerm = "%" + searchTerm + "%";
                pstmt.setString(1, likeTerm);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Discount_ID"),
                        rs.getString("Discount_Type"),
                        String.format("%.2f%%", rs.getDouble("Discount_Percentage")),
                        rs.getDate("Date_Issued"),
                        rs.getDate("Date_Expiry"),
                        rs.getBoolean("IsUsed") ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading discounts: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * tickets table data to a export CSV file.
     */
    private void exportTicketsData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Tickets Data");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            if (!filePath.endsWith(".csv")) filePath += ".csv";

            try (PrintWriter writer = new PrintWriter(new File(filePath))) {
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < ticketsTable.getColumnCount(); i++) {
                    header.append(ticketsTable.getColumnName(i));
                    if (i < ticketsTable.getColumnCount() - 1) header.append(",");
                }
                writer.println(header.toString());
                for (int row = 0; row < ticketsTable.getRowCount(); row++) {
                    StringBuilder sb = new StringBuilder();
                    for (int col = 0; col < ticketsTable.getColumnCount(); col++) {
                        Object value = ticketsTable.getValueAt(row, col);
                        sb.append(value != null ? value.toString() : "");
                        if (col < ticketsTable.getColumnCount() - 1) sb.append(",");
                    }
                    writer.println(sb.toString());
                }
                JOptionPane.showMessageDialog(this, "Tickets data exported to " + filePath,
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting tickets: " + e.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * Exports discounts to CSV file.
     */
    private void exportDiscountsData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Discounts Data");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            if (!filePath.endsWith(".csv")) filePath += ".csv";
            try (PrintWriter writer = new PrintWriter(new File(filePath))) {
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < discountsTable.getColumnCount(); i++) {
                    header.append(discountsTable.getColumnName(i));
                    if (i < discountsTable.getColumnCount() - 1) header.append(",");
                }
                writer.println(header.toString());
                for (int row = 0; row < discountsTable.getRowCount(); row++) {
                    StringBuilder sb = new StringBuilder();
                    for (int col = 0; col < discountsTable.getColumnCount(); col++) {
                        Object value = discountsTable.getValueAt(row, col);
                        sb.append(value != null ? value.toString() : "");
                        if (col < discountsTable.getColumnCount() - 1) sb.append(",");
                    }
                    writer.println(sb.toString());
                }
                JOptionPane.showMessageDialog(this, "Discounts data exported to " + filePath,
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting discounts: " + e.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void loadInitialData() {
        showSelector.addItem("Select a Show");
        for (Map<String, Object> show : getAllShows()) {
            String showInfo = show.get("id") + " - " + show.get("title");
            showSelector.addItem(showInfo);
        }

        venueSelector.addItem("Select a Venue");
        for (Map<String, Object> venue : getVenues()) {
            String venueName = venue.get("name").toString();
            venueSelector.addItem(venueName);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(dateFormat.format(new Date()));
    }
    private void updateTicketDisplay() {
        ticketListPanel.removeAll();
        quantitySpinners.clear();
        currentTotal = 0.0;
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setLayout(new GridLayout(1, 4));
        headerPanel.add(createHeaderLabel("Ticket Name"));
        headerPanel.add(createHeaderLabel("Base Price"));
        headerPanel.add(createHeaderLabel("Discounted Price"));
        headerPanel.add(createHeaderLabel("Quantity"));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        ticketListPanel.add(headerPanel);
        JPanel infoBanner = new JPanel();
        infoBanner.setBackground(HEADER_COLOR);
        infoBanner.add(new JLabel("All ticket prices are per person. All sales are final.") {{
            setForeground(Color.WHITE);
            setFont(CONTENT_FONT);
        }});
        infoBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        ticketListPanel.add(infoBanner);
        String selectedShow = (String) showSelector.getSelectedItem();
        if (selectedShow == null || selectedShow.equals("Select a Show")) {
            return;
        }
        int showId = Integer.parseInt(selectedShow.split(" - ")[0]);
        List<Map<String, Object>> tickets = getTicketTypes(showId, selectedPatronId);
        for (Map<String, Object> ticket : tickets) {
            String name = (String) ticket.get("name");
            double basePrice = (double) ticket.get("basePrice");
            double discountAmount = (double) ticket.get("discountAmount");
            boolean isAccessible = (boolean) ticket.get("isWheelchairAccessible");
            boolean requiresId = (boolean) ticket.get("requiresId");
            addTicketType(name, basePrice, discountAmount, isAccessible, requiresId);
        }
        JPanel termsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        termsPanel.setBackground(Color.WHITE);
        JLabel termsLink = new JLabel("(View Terms)");
        termsLink.setForeground(Color.BLUE);
        termsLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        termsLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTermsAndConditions();
            }
        });
        termsPanel.add(termsLink);
        ticketListPanel.add(termsPanel);
        ticketListPanel.revalidate();
        ticketListPanel.repaint();
    }


    /**
     * Adds a ticket.
     * shows ticket details
     * @param name the name of the ticket
     * @param basePrice the price of the ticket
     * @param discountAmount the discount amount
     * @param isAccessible whether the ticket is wheelchair accessible - for disabilities
     * @param requiresId whether ID is required for the ticket
     */
    private void addTicketType(String name, double basePrice, double discountAmount,
                               boolean isAccessible, boolean requiresId) {
        JPanel ticketPanel = new JPanel();
        ticketPanel.setLayout(new GridLayout(1, 4));
        ticketPanel.setBackground(Color.WHITE);
        ticketPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBackground(Color.WHITE);
        namePanel.add(new JLabel(name), BorderLayout.NORTH);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a z");
        JLabel dateLabel = new JLabel("Last Purchase Date: " + sdf.format(new Date()));
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        dateLabel.setForeground(Color.GRAY);
        namePanel.add(dateLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        infoPanel.setBackground(Color.WHITE);
        namePanel.add(infoPanel, BorderLayout.SOUTH);
        JLabel basePriceLabel = new JLabel(String.format("£%.2f", basePrice));
        basePriceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        double finalPrice = basePrice - discountAmount;
        JLabel discountedPriceLabel = new JLabel(String.format("£%.2f", finalPrice));
        discountedPriceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (discountAmount > 0) {
            discountedPriceLabel.setForeground(new Color(0, 150, 0));
            discountedPriceLabel.setFont(discountedPriceLabel.getFont().deriveFont(Font.BOLD));
        }
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 99, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.addChangeListener(e -> updateTotal());
        quantitySpinners.put(ticketPanel.hashCode(), quantitySpinner);
        ticketPanel.add(namePanel);
        ticketPanel.add(basePriceLabel);
        ticketPanel.add(discountedPriceLabel);
        ticketPanel.add(quantitySpinner);
        ticketPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        ticketListPanel.add(ticketPanel);
    }

    // update the purchase based on the quantity needed to purchase
    private void updateTotal() {
        currentTotal = 0.0;
        boolean hasGroupTickets = false;
        int totalTickets = 0;
        for (JSpinner spinner : quantitySpinners.values()) {
            totalTickets += (Integer) spinner.getValue();
        }
        hasGroupTickets = totalTickets >= 10;
        for (Map.Entry<Integer, JSpinner> entry : quantitySpinners.entrySet()) {
            int quantity = (Integer) entry.getValue().getValue();
            if (quantity > 0) {
                double basePrice = 25.00;
                double discount = hasGroupTickets ? 7.00 : 0.0;
                currentTotal += quantity * (basePrice - discount);
            }
        }
        totalLabel.setText(String.format("Purchase Total: £%.2f", currentTotal));
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(HEADER_FONT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.black);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(40, 90, 25));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }
    /**
     * displays a dialog for adding a new show - a new panel box
     */
    private void showAddShowDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Show", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(null);
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;gbc.insets = new Insets(5, 5, 5, 5);gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Title:"), gbc);gbc.gridx = 1; gbc.weightx = 1.0; JTextField titleField = new JTextField(20); formPanel.add(titleField, gbc);gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(new JLabel("Description:"), gbc);gbc.gridx = 1; gbc.weightx = 1.0; JTextArea descriptionArea = new JTextArea(3, 20); descriptionArea.setLineWrap(true); descriptionArea.setWrapStyleWord(true); formPanel.add(new JScrollPane(descriptionArea), gbc);gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; formPanel.add(new JLabel("Duration (minutes):"), gbc);gbc.gridx = 1; gbc.weightx = 1.0; JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(60, 1, 480, 5)); formPanel.add(durationSpinner, gbc);gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); formPanel.add(dateField, gbc);gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; formPanel.add(new JLabel("Time:"), gbc);gbc.gridx = 1; gbc.weightx = 1.0; JTextField timeField = new JTextField("19:00"); formPanel.add(timeField, gbc);gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; formPanel.add(new JLabel("Venue:"), gbc);gbc.gridx = 1; gbc.weightx = 1.0; JComboBox<String> venueSelector = new JComboBox<>(); List<Map<String, Object>> venues = getVenues(); for (Map<String, Object> venue : venues) { venueSelector.addItem(venue.get("name").toString()); } formPanel.add(venueSelector, gbc);gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0; formPanel.add(new JLabel("Hall Type:"), gbc);gbc.gridx = 1; gbc.weightx = 1.0; JTextField hallTypeField = new JTextField("Main Hall"); formPanel.add(hallTypeField, gbc);gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0; formPanel.add(new JLabel("Base Price:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; JTextField priceField = new JTextField("10.00"); formPanel.add(priceField, gbc);gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0; formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; JCheckBox isFilmCheckbox = new JCheckBox("Is Film"); formPanel.add(isFilmCheckbox, gbc);gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2; gbc.weightx = 1.0; JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); JButton saveButton = createStyledButton("Save Show", GREEN_BUTTON_COLOR); saveButton.setPreferredSize(new Dimension(120, 30)); buttonPanel.add(saveButton); formPanel.add(buttonPanel, gbc);
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            String dateTime = date + " " + time;
            String selectedVenue = (String) venueSelector.getSelectedItem();
            String hallType = hallTypeField.getText().trim();
            boolean isFilm = isFilmCheckbox.isSelected();
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) { JOptionPane.showMessageDialog(dialog, "Price must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE); return; }

                // adding shows and venues and prices for adding the show to the site.
                int venueId = -1;
                for (Map<String, Object> venue : venues) {
                    if (venue.get("name").toString().equals(selectedVenue)) { venueId = (int) venue.get("id"); break; }
                }
                if (title.isEmpty() || date.isEmpty() || time.isEmpty() || venueId == -1) { JOptionPane.showMessageDialog(dialog, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE); return; }
                String duration = String.valueOf(durationSpinner.getValue()) + " minutes";
                int showId = addNewShow(title, description, dateTime, venueId, hallType, price, duration, isFilm);
                if (showId != -1) {
                    JOptionPane.showMessageDialog(dialog, "Show added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadInitialData();
                    updateTicketDisplay();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add show", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(formPanel);
        dialog.setVisible(true);
    }

    // managing the existing shows
    private void showManageShowsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Shows", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"ID", "Title", "Date", "Venue", "Hall Type", "Base Price", "Total Seats", "Sold Seats", "Available"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable showTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(showTable);
        dialog.add(scrollPane, BorderLayout.CENTER);
        loadShowsIntoTable(tableModel);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = createStyledButton("Edit Show", GREEN_BUTTON_COLOR);
        editButton.addActionListener(e -> {
            int selectedRow = showTable.getSelectedRow();
            if (selectedRow >= 0) {
                showEditShowDialog(
                        (int) tableModel.getValueAt(selectedRow, 0),
                        (String) tableModel.getValueAt(selectedRow, 1),
                        (String) tableModel.getValueAt(selectedRow, 2),
                        (String) tableModel.getValueAt(selectedRow, 3),
                        (String) tableModel.getValueAt(selectedRow, 4),
                        Double.parseDouble(((String) tableModel.getValueAt(selectedRow, 5)).substring(1))
                );
                loadShowsIntoTable(tableModel);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a show to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        JButton closeButton = createStyledButton("Close", GREEN_BUTTON_COLOR);
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    private void loadShowsIntoTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Map<String, Object>> shows = getAllShows();
        for (Map<String, Object> show : shows) {
            tableModel.addRow(new Object[]{
                    show.get("id"), show.get("title"), show.get("date"), show.get("venue"), show.get("hallType"),
                    String.format("£%.2f", show.get("price")), show.get("totalSeats"), show.get("soldSeats"), show.get("availableSeats")
            });
        }
    }

    /**
     * Displays a panel for existing shows
     * manage the shows and updates the database.
     * @param showId the ID
     * @param title title
     * @param date date
     * @param venue venue
     * @param hallType hall
     * @param price price
     */
    private void showEditShowDialog(int showId, String title, String date, String venue, String hallType, double price) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Show", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Show Title:")); JTextField titleField = new JTextField(title); formPanel.add(titleField);
        formPanel.add(new JLabel("Show Date (YYYY-MM-DD):")); JTextField dateField = new JTextField(date); formPanel.add(dateField);

        formPanel.add(new JLabel("Venue:"));
        JComboBox<String> venueComboBox = new JComboBox<>();
        List<Map<String, Object>> venues = getVenues();
        Map<String, Integer> venueIdMap = new HashMap<>();
        for (Map<String, Object> v : venues) {
            String name = (String) v.get("name");
            venueComboBox.addItem(name);
            venueIdMap.put(name, (Integer) v.get("id"));
            if (name.equals(venue)) venueComboBox.setSelectedItem(name);
        }
        formPanel.add(venueComboBox);

        formPanel.add(new JLabel("Hall Type:"));
        JComboBox<String> hallTypeComboBox = new JComboBox<>(new String[]{"Main Hall", "Small Hall"});
        hallTypeComboBox.setSelectedItem(hallType);
        formPanel.add(hallTypeComboBox);

        formPanel.add(new JLabel("Base Price:"));
        JTextField priceField = new JTextField(String.format("%.2f", price));
        formPanel.add(priceField);
        dialog.add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", GREEN_BUTTON_COLOR);
        JButton cancelButton = createStyledButton("Cancel", GREEN_BUTTON_COLOR);

        saveButton.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(dialog, "Please enter a show title", "Error", JOptionPane.ERROR_MESSAGE); return; }

            try {
                double newPrice = Double.parseDouble(priceField.getText().trim());
                if (newPrice <= 0) { JOptionPane.showMessageDialog(dialog, "Price must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE); return; }

                String selectedVenue = (String) venueComboBox.getSelectedItem();
                int venueId = venueIdMap.get(selectedVenue);
                String newHallType = (String) hallTypeComboBox.getSelectedItem();

                boolean success = updateShow(showId, titleField.getText().trim(), dateField.getText().trim(), venueId, newHallType, newPrice);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Show updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadInitialData();
                    updateTicketDisplay();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update show", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Displays the terms and conditions in a dialog.
     */
    private void showTermsAndConditions() {
        String terms = """
            Terms and Conditions:
            1. All sales are final. No refunds or exchanges.
            2. Tickets are non-transferable.
            3. ID may be required for discounted tickets.
            4. Group discounts apply only when purchasing 10 or more tickets.
            5. Disability access tickets include one free companion ticket.
            6. Student and senior discounts require valid identification.
            7. Early bird and last-minute deals are subject to availability.
            8. Family packages must include at least one child under 12.
            9. Lost or stolen tickets will not be replaced.
            10. Management reserves the right to refuse entry.
            """;
        JTextArea textArea = new JTextArea(terms);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Terms and Conditions", JOptionPane.INFORMATION_MESSAGE);
    }

    private void processTicketPurchase() {
        if (currentTotal <= 0) { JOptionPane.showMessageDialog(this, "Please select at least one ticket to purchase", "No Tickets Selected", JOptionPane.WARNING_MESSAGE); return; }
        showPatronSelectionDialog();
        if (selectedPatronId < 0) return;
        String selectedShow = (String) showSelector.getSelectedItem();
        if (selectedShow == null || selectedShow.equals("Select a Show")) { JOptionPane.showMessageDialog(this, "Please select a show", "No Show Selected", JOptionPane.WARNING_MESSAGE); return; }
        int showId = Integer.parseInt(selectedShow.split(" - ")[0]);
        List<Map<String, Object>> ticketsToPurchase = new ArrayList<>();
        List<Map<String, Object>> ticketTypes = getTicketTypes(showId, selectedPatronId);
        int ticketTypeIndex = 0;

        for (Map.Entry<Integer, JSpinner> entry : quantitySpinners.entrySet()) {
            int quantity = (Integer) entry.getValue().getValue();
            if (quantity > 0) {
                Map<String, Object> ticketType = ticketTypes.get(ticketTypeIndex);
                Map<String, Object> ticketInfo = new HashMap<>();
                ticketInfo.put("quantity", quantity);
                ticketInfo.put("price", ticketType.get("basePrice"));
                ticketInfo.put("discount", ticketType.get("discountAmount"));
                ticketInfo.put("isWheelchairAccessible", ticketType.get("isWheelchairAccessible"));
                ticketInfo.put("requiresId", ticketType.get("requiresId"));
                ticketsToPurchase.add(ticketInfo);
            }
            ticketTypeIndex++;
        }
        try {
            boolean success = processSale(showId, selectedPatronId, ticketsToPurchase, currentTotal);
            if (success) {
                JOptionPane.showMessageDialog(this, String.format("Purchase processed successfully!\n\nTotal: £%.2f\nTickets: %d\nPatron ID: %d", currentTotal, ticketsToPurchase.stream().mapToInt(t -> (int)t.get("quantity")).sum(), selectedPatronId), "Success", JOptionPane.INFORMATION_MESSAGE);
                for (JSpinner spinner : quantitySpinners.values()) { spinner.setValue(0); }
                updateTotal();
                updateTicketDisplay();
                loadTicketsData(ticketSearchField.getText().trim());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to process purchase. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing purchase: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPatronSelectionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Patron", true);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> patronListModel = new DefaultListModel<>();
        JList<String> patronList = new JList<>(patronListModel);
        patronList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        try {
            List<Map<String, Object>> patrons = getAllPatrons();
            for (Map<String, Object> patron : patrons) {
                patronListModel.addElement(patron.get("Patron_ID") + " - " + patron.get("First_Name") + " " + patron.get("Last_Name"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        JScrollPane scrollPane = new JScrollPane(patronList);
        dialog.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton selectButton = createStyledButton("Select", GREEN_BUTTON_COLOR);
        selectButton.addActionListener(e -> {
            String selected = patronList.getSelectedValue();
            if (selected != null) {
                selectedPatronId = Integer.parseInt(selected.split(" - ")[0]);
                updateTicketDisplay();
                dialog.dispose();
            }
        });

        JButton cancelButton = createStyledButton("Cancel", GREEN_BUTTON_COLOR);
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDatePicker() {
        JDialog picker = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Date", true);
        picker.setLayout(new BorderLayout());
        JPanel headerPanel = new JPanel(new FlowLayout());
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        Integer[] years = new Integer[10];
        for (int i = 0; i < 10; i++) { years[i] = currentYear + i; }
        JComboBox<Integer> yearCombo = new JComboBox<>(years);
        headerPanel.add(monthCombo);
        headerPanel.add(yearCombo);
        JPanel calendarPanel = new JPanel(new GridLayout(7, 7, 5, 5));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] dayHeaders = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayHeaders) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            calendarPanel.add(label);
        }
        JButton[] dateButtons = new JButton[42];
        for (int i = 0; i < 42; i++) {
            dateButtons[i] = new JButton();
            dateButtons[i].setFocusPainted(false);
            calendarPanel.add(dateButtons[i]);
        }
        ActionListener updateCalendar = e -> {
            int month = monthCombo.getSelectedIndex();
            int year = (Integer) yearCombo.getSelectedItem();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, 1);

            int firstDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int i = 0; i < 42; i++) {
                dateButtons[i].setText("");
                dateButtons[i].setEnabled(false);
            }

            for (int i = 0; i < daysInMonth; i++) {
                final int day = i + 1;
                dateButtons[firstDay + i].setText(String.valueOf(day));
                dateButtons[firstDay + i].setEnabled(true);
                dateButtons[firstDay + i].addActionListener(ae -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    calendar.set(year, month, day);
                    dateField.setText(sdf.format(calendar.getTime()));
                    picker.dispose();
                    updateTicketDisplay();
                });
            }
        };
        monthCombo.addActionListener(updateCalendar);
        yearCombo.addActionListener(updateCalendar);
        updateCalendar.actionPerformed(null);

        picker.add(headerPanel, BorderLayout.NORTH);
        picker.add(calendarPanel, BorderLayout.CENTER);
        picker.pack();
        picker.setLocationRelativeTo(datePickerButton);
        picker.setVisible(true);
    }

    // search the ticket sales data report by dates
    private void showSalesReportDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ticket Sales Report", true);
        dialog.setBackground(Color.gray);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        dateRangePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel startLabel = new JLabel("Start Date:");
        JTextField startDateField = new JTextField(10);
        startLabel.setForeground(Color.black);
        JButton startDateButton = createStyledButton("date", GREEN_BUTTON_COLOR);
        startDateButton.addActionListener(e -> showDatePickerForField(startDateField));

        JLabel endLabel = new JLabel("End Date:");
        JTextField endDateField = new JTextField(10);
        endLabel.setForeground(Color.black);
        JButton endDateButton = createStyledButton("date", GREEN_BUTTON_COLOR);
        endDateButton.addActionListener(e -> showDatePickerForField(endDateField));

        JButton searchButton = createStyledButton("Search", GREEN_BUTTON_COLOR);
        searchButton.addActionListener(e -> updateSalesReport(dialog, startDateField.getText(), endDateField.getText()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        endDateField.setText(sdf.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, -30);
        startDateField.setText(sdf.format(cal.getTime()));

        dateRangePanel.add(startLabel);
        dateRangePanel.add(startDateField);
        dateRangePanel.add(startDateButton);
        dateRangePanel.add(endLabel);
        dateRangePanel.add(endDateField);
        dateRangePanel.add(endDateButton);
        dateRangePanel.add(searchButton);

        String[] columnNames = {"ID", "Title", "Date", "Time", "Type", "Base Price", "Tickets Sold", "Discounts", "Discount Amount", "Group Tickets", "Group Revenue", "Friends Tickets", "Friends Revenue", "Total Revenue"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel totalTicketsLabel = new JLabel("Total Tickets: 0");
        JLabel totalRevenueLabel = new JLabel("Total Revenue: £0.00");
        summaryPanel.add(totalTicketsLabel);
        summaryPanel.add(totalRevenueLabel);

        dialog.add(dateRangePanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(summaryPanel, BorderLayout.SOUTH);

        updateSalesReport(dialog, startDateField.getText(), endDateField.getText());
        dialog.setVisible(true);
    }

    private void showDatePickerForField(JTextField field) {
        JDialog picker = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Date", true);
        picker.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout());
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        Integer[] years = new Integer[10];
        for (int i = 0; i < 10; i++) { years[i] = currentYear + i - 5; }
        JComboBox<Integer> yearCombo = new JComboBox<>(years);

        headerPanel.add(monthCombo);
        headerPanel.add(yearCombo);

        JPanel calendarPanel = new JPanel(new GridLayout(7, 7, 5, 5));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] dayHeaders = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayHeaders) { calendarPanel.add(new JLabel(day, SwingConstants.CENTER)); }

        JButton[] dateButtons = new JButton[42];
        for (int i = 0; i < 42; i++) {
            dateButtons[i] = new JButton();
            dateButtons[i].setFocusPainted(false);
            calendarPanel.add(dateButtons[i]);
        }

        ActionListener updateCalendar = e -> {
            int month = monthCombo.getSelectedIndex();
            int year = (Integer) yearCombo.getSelectedItem();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, 1);

            int firstDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int i = 0; i < 42; i++) {
                dateButtons[i].setText("");
                dateButtons[i].setEnabled(false);
            }

            for (int i = 0; i < daysInMonth; i++) {
                final int day = i + 1;
                dateButtons[firstDay + i].setText(String.valueOf(day));
                dateButtons[firstDay + i].setEnabled(true);
                dateButtons[firstDay + i].addActionListener(ae -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    calendar.set(year, month, day);
                    field.setText(sdf.format(calendar.getTime()));
                    picker.dispose();
                });
            }
        };

        monthCombo.addActionListener(updateCalendar);
        yearCombo.addActionListener(updateCalendar);
        updateCalendar.actionPerformed(null);

        picker.add(headerPanel, BorderLayout.NORTH);
        picker.add(calendarPanel, BorderLayout.CENTER);
        picker.pack();
        picker.setLocationRelativeTo(field);
        picker.setVisible(true);
    }

    private void updateSalesReport(JDialog dialog, String startDate, String endDate) {
        JTable table = (JTable) ((JScrollPane) dialog.getContentPane().getComponent(1)).getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        JPanel summaryPanel = (JPanel) dialog.getContentPane().getComponent(2);

        model.setRowCount(0);

        List<Map<String, Object>> salesData = getTicketSalesInDateRange(startDate, endDate);

        int totalTickets = 0;
        double totalBaseRevenue = 0.0;
        double totalGroupRevenue = 0.0;
        double totalFriendsRevenue = 0.0;
        double totalDiscounts = 0.0;

        for (Map<String, Object> show : salesData) {
            model.addRow(new Object[]{
                    show.get("id"), show.get("title"), show.get("date"), show.get("time"), show.get("type"),
                    String.format("£%.2f", show.get("price")), show.get("ticketsSold"), show.get("discountsApplied"),
                    String.format("£%.2f", show.get("discountedAmount")), show.get("groupTickets"),
                    String.format("£%.2f", show.get("groupRevenue")), show.get("friendsTickets"),
                    String.format("£%.2f", show.get("friendsRevenue")), String.format("£%.2f", show.get("revenue"))
            });

            totalTickets += (Integer) show.get("ticketsSold");
            totalBaseRevenue += (Double) show.get("revenue");
            totalGroupRevenue += (Double) show.get("groupRevenue");
            totalFriendsRevenue += (Double) show.get("friendsRevenue");
            totalDiscounts += (Double) show.get("discountedAmount");
        }

        double actualTotalRevenue = totalBaseRevenue + totalGroupRevenue + totalFriendsRevenue - totalDiscounts;

        summaryPanel.removeAll();
        summaryPanel.setLayout(new GridLayout(2, 3, 10, 5));
        summaryPanel.add(new JLabel("Total Tickets: " + totalTickets));
        summaryPanel.add(new JLabel(String.format("Base Revenue: £%.2f", totalBaseRevenue)));
        summaryPanel.add(new JLabel(String.format("Group Revenue: £%.2f", totalGroupRevenue)));
        summaryPanel.add(new JLabel(String.format("Friends Revenue: £%.2f", totalFriendsRevenue)));
        summaryPanel.add(new JLabel(String.format("Total Discounts: £%.2f", totalDiscounts)));
        summaryPanel.add(new JLabel(String.format("Actual Revenue: £%.2f", actualTotalRevenue)));
        for (Component comp : summaryPanel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 12));
                if (label.getText().startsWith("Actual Revenue")) label.setForeground(new Color(0, 100, 0));
            }
        }
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    /**
     * get the ticket type for a given show and patron.
     * @param showId the ID o
     * @param patronId the ID of the patron
     * @return a list of ticket - containing name, price, discount, and accessibility data
     */
    public List<Map<String, Object>> getTicketTypes(int showId, int patronId) {
        List<Map<String, Object>> ticketTypes = new ArrayList<>();
        String showQuery = "SELECT s.Show_Title, s.Show_Price, s.Hall_Type, s.Venue_ID FROM Shows s WHERE s.Show_ID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement showStmt = connection.prepareStatement(showQuery)) {
            showStmt.setInt(1, showId);
            ResultSet showRs = showStmt.executeQuery();

            if (showRs.next()) {
                double basePrice = showRs.getDouble("Show_Price");
                String hallType = showRs.getString("Hall_Type");

                boolean isLancasterFriend = false;
                double membershipDiscount = 0.25;
                if (patronId > 0) {
                    String memberQuery = "SELECT * FROM LancasterFriend WHERE Patron_ID = ? AND CURDATE() BETWEEN Subscription_Start_Date AND Subscription_End_Date";
                    try (PreparedStatement memberStmt = connection.prepareStatement(memberQuery)) {
                        memberStmt.setInt(1, patronId);
                        ResultSet memberRs = memberStmt.executeQuery();
                        isLancasterFriend = memberRs.next();
                    }
                }
                Map<String, Object> standard = new HashMap<>();
                standard.put("name", "Standard Ticket");
                standard.put("basePrice", basePrice);
                standard.put("discountAmount", 0.0);
                standard.put("isWheelchairAccessible", false);
                standard.put("requiresId", false);
                ticketTypes.add(standard);

                String discountQuery = "SELECT * FROM Discount WHERE IsUsed = 0";
                try (Statement discountStmt = connection.createStatement();
                     ResultSet discountRs = discountStmt.executeQuery(discountQuery)) {

                    while (discountRs.next()) {
                        Map<String, Object> discountTicket = new HashMap<>();
                        String discountType = discountRs.getString("Discount_Type");
                        double discountPercentage = discountRs.getDouble("Discount_Percentage") / 100.0;

                        discountTicket.put("name", discountType + " Ticket");
                        discountTicket.put("basePrice", basePrice);
                        discountTicket.put("discountAmount", basePrice * discountPercentage);
                        discountTicket.put("isWheelchairAccessible", discountType.equals("Disability"));
                        discountTicket.put("requiresId", true);
                        ticketTypes.add(discountTicket);
                    }
                }

                if (isLancasterFriend) {
                    Map<String, Object> memberTicket = new HashMap<>();
                    memberTicket.put("name", "LancasterFriend Member Ticket");
                    memberTicket.put("basePrice", basePrice);
                    memberTicket.put("discountAmount", basePrice * membershipDiscount);
                    memberTicket.put("isWheelchairAccessible", false);
                    memberTicket.put("requiresId", true);
                    ticketTypes.add(memberTicket);
                }

                if (hallType.equals("Main Hall")) {
                    Map<String, Object> vip = new HashMap<>();
                    vip.put("name", "VIP Experience");
                    vip.put("basePrice", basePrice * 1.5);
                    vip.put("discountAmount", 0.0);
                    vip.put("isWheelchairAccessible", false);
                    vip.put("requiresId", false);
                    ticketTypes.add(vip);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return ticketTypes;
    }

    /**
     * Processes a ticket sale -- will go to the teams database
     *
     * @param showId the ID
     * @param patronId the ID patron
     * @param tickets the list of tickets
     * @param totalPrice the total price
     * @return true if the sale was successful
     */
    public boolean processSale(int showId, int patronId, List<Map<String, Object>> tickets, double totalPrice) {
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false);
            String bookingSql = "INSERT INTO Booking (Patron_ID, Show_ID, Venue_ID, Booking_Date, TotalCost, IsCancelled, Discount_Applied, IsGroupBooking, GroupSize) VALUES (?, ?, 1, NOW(), ?, 0, ?, ?, ?)";
            PreparedStatement bookingStmt = connection.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
            double totalDiscount = tickets.stream().mapToDouble(t -> (double)t.get("discount") * (int)t.get("quantity")).sum();
            int totalQuantity = tickets.stream().mapToInt(t -> (int)t.get("quantity")).sum();
            boolean isGroupBooking = totalQuantity >= 10;
            bookingStmt.setInt(1, patronId);
            bookingStmt.setInt(2, showId);
            bookingStmt.setDouble(3, totalPrice);
            bookingStmt.setDouble(4, totalDiscount);
            bookingStmt.setBoolean(5, isGroupBooking);
            bookingStmt.setInt(6, isGroupBooking ? totalQuantity : 0);
            int affectedRows = bookingStmt.executeUpdate();
            if (affectedRows == 0) { connection.rollback(); return false; }

            int bookingId;
            try (ResultSet generatedKeys = bookingStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) { bookingId = generatedKeys.getInt(1); } else { connection.rollback(); return false; }
            }
            String ticketSql = "INSERT INTO Ticket (Ticket_Price, Seat_Code, Row_Number, Discount_Amount, IsWheelchairAccessible, IsRestrictedView, IsSold, Show_ID, Booking_ID, Seat_ID) VALUES (?, ?, ?, ?, ?, ?, 1, ?, ?, ?)";
            PreparedStatement ticketStmt = connection.prepareStatement(ticketSql);

            String updateSeatsSql = "UPDATE Seat_Availability SET Status = ?, Booking_ID = ? WHERE Show_ID = ? AND Seat_ID = ? AND (Status IS NULL OR Status = 'Available')";
            PreparedStatement updateSeatsStmt = connection.prepareStatement(updateSeatsSql);

            for (Map<String, Object> ticket : tickets) {
                int quantity = (int) ticket.get("quantity");
                double price = (double) ticket.get("price");
                double discount = (double) ticket.get("discount");
                boolean isWheelchairAccessible = (boolean) ticket.get("isWheelchairAccessible");

                String getSeatsSQL = "SELECT sa.Seat_ID, s.Seat_Code, s.Row_Number, s.Is_Restricted_View FROM Seat_Availability sa JOIN Seat s ON sa.Seat_ID = s.Seat_ID WHERE sa.Show_ID = ? AND (sa.Status IS NULL OR sa.Status = 'Available') " + (isWheelchairAccessible ? "AND s.Is_Wheelchair_Accessible = 1 " : "") + "LIMIT ?";
                PreparedStatement getSeatsStmt = connection.prepareStatement(getSeatsSQL);
                getSeatsStmt.setInt(1, showId);
                getSeatsStmt.setInt(2, quantity);
                ResultSet seatsRs = getSeatsStmt.executeQuery();
                while (seatsRs.next()) {
                    int seatId = seatsRs.getInt("Seat_ID");
                    String seatCode = seatsRs.getString("Seat_Code");
                    String rowNumber = seatsRs.getString("Row_Number");
                    boolean isRestrictedView = seatsRs.getBoolean("Is_Restricted_View");

                    updateSeatsStmt.setString(1, discount > 0 ? "Discounted" : "Sold");
                    updateSeatsStmt.setInt(2, bookingId);
                    updateSeatsStmt.setInt(3, showId);
                    updateSeatsStmt.setInt(4, seatId);
                    updateSeatsStmt.executeUpdate();

                    ticketStmt.setDouble(1, price);
                    ticketStmt.setString(2, seatCode);
                    ticketStmt.setString(3, rowNumber);
                    ticketStmt.setDouble(4, discount);
                    ticketStmt.setBoolean(5, isWheelchairAccessible);
                    ticketStmt.setBoolean(6, isRestrictedView);
                    ticketStmt.setInt(7, showId);
                    ticketStmt.setInt(8, bookingId);
                    ticketStmt.setInt(9, seatId);
                    ticketStmt.executeUpdate();
                }
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) { try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) { try { connection.setAutoCommit(true); connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    // get the venue data
    public List<Map<String, Object>> getVenues() {
        List<Map<String, Object>> venues = new ArrayList<>();
        String query = "SELECT Venue_ID AS id, Venue_Name AS name FROM Venue";
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Map<String, Object> venue = new HashMap<>();
                venue.put("id", resultSet.getInt("id"));
                venue.put("name", resultSet.getString("name"));
                venues.add(venue);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        if (venues.isEmpty()) {
            Map<String, Object> venue1 = new HashMap<>(); venue1.put("id", 1); venue1.put("name", "Main Music Hall"); venues.add(venue1);
            Map<String, Object> venue2 = new HashMap<>(); venue2.put("id", 2); venue2.put("name", "Small Music Hall"); venues.add(venue2);
        }
        return venues;
    }

    // get all show data
    public List<Map<String, Object>> getAllShows() {
        List<Map<String, Object>> shows = new ArrayList<>();
        String query = "SELECT s.Show_ID AS id, s.Show_Title AS title, s.Show_Date AS date, s.Show_Start_Time AS time, s.Show_Duration AS duration, s.Show_Price AS price, s.Hall_Type AS hallType, s.Show_Description AS description, v.Venue_Name AS venue, (SELECT COUNT(*) FROM Seat WHERE Venue_ID = s.Venue_ID) AS totalSeats, (SELECT COUNT(*) FROM Seat_Availability sa JOIN Seat se ON sa.Seat_ID = se.Seat_ID WHERE sa.Show_ID = s.Show_ID AND sa.Status = 'Sold' AND se.Venue_ID = s.Venue_ID) AS soldSeats FROM Shows s JOIN Venue v ON s.Venue_ID = v.Venue_ID ORDER BY s.Show_Date";
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Map<String, Object> show = new HashMap<>();
                show.put("id", resultSet.getInt("id"));
                show.put("title", resultSet.getString("title"));
                show.put("date", resultSet.getString("date"));
                show.put("time", resultSet.getString("time"));
                show.put("duration", resultSet.getString("duration"));
                show.put("price", resultSet.getDouble("price"));
                show.put("hallType", resultSet.getString("hallType"));
                show.put("venue", resultSet.getString("venue"));
                show.put("description", resultSet.getString("description"));
                show.put("totalSeats", resultSet.getInt("totalSeats"));
                show.put("soldSeats", resultSet.getInt("soldSeats"));
                show.put("availableSeats", resultSet.getInt("totalSeats") - resultSet.getInt("soldSeats"));
                shows.add(show);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return shows;
    }


    // get all patron data
    public List<Map<String, Object>> getAllPatrons() {
        List<Map<String, Object>> patrons = new ArrayList<>();
        String query = "SELECT Patron_ID, First_Name, Last_Name FROM Patron ORDER BY Last_Name, First_Name";
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Map<String, Object> patron = new HashMap<>();
                patron.put("Patron_ID", resultSet.getInt("Patron_ID"));
                patron.put("First_Name", resultSet.getString("First_Name"));
                patron.put("Last_Name", resultSet.getString("Last_Name"));
                patrons.add(patron);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return patrons;
    }


    /**
     * get ticket sales data for a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of sales data with revenue data
     */
    public List<Map<String, Object>> getTicketSalesInDateRange(String startDate, String endDate) {
        List<Map<String, Object>> salesData = new ArrayList<>();
        String query = "SELECT s.Show_ID AS id, s.Show_Title AS title, s.Show_Date AS date, s.Show_Start_Time AS time, s.Show_Duration AS duration, s.Show_Price AS price, s.IsFilm, bor.Total_Tickets_Sold AS ticketsSold, bor.Total_Revenue AS revenue, bor.Report_Type AS type, bor.Total_Discounts_Applied AS discountsApplied, bor.Total_Discounted_Amount AS discountedAmount, bor.Total_Group_Tickets_Sold AS groupTickets, bor.Total_Group_Revenue AS groupRevenue, bor.Total_Friends_Tickets_Sold AS friendsTickets, bor.Total_Friends_Revenue AS friendsRevenue FROM Shows s LEFT JOIN BoxOfficeReport bor ON s.Show_ID = bor.Show_ID WHERE s.Show_Date BETWEEN ? AND ? ORDER BY s.Show_Date, s.Show_Start_Time";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> show = new HashMap<>();
                    show.put("id", rs.getInt("id"));
                    show.put("title", rs.getString("title"));
                    show.put("date", rs.getString("date"));
                    show.put("time", rs.getString("time"));
                    show.put("duration", rs.getString("duration"));
                    show.put("price", rs.getDouble("price"));
                    show.put("type", rs.getBoolean("IsFilm") ? "Film" : "Show");
                    show.put("ticketsSold", rs.getInt("ticketsSold"));
                    show.put("revenue", rs.getDouble("revenue"));
                    show.put("discountsApplied", rs.getInt("discountsApplied"));
                    show.put("discountedAmount", rs.getDouble("discountedAmount"));
                    show.put("groupTickets", rs.getInt("groupTickets"));
                    show.put("groupRevenue", rs.getDouble("groupRevenue"));
                    show.put("friendsTickets", rs.getInt("friendsTickets"));
                    show.put("friendsRevenue", rs.getDouble("friendsRevenue"));
                    salesData.add(show);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return salesData;
    }

    public int addNewShow(String title, String description, String dateTime, int venueId, String hallType, double price, String duration, boolean isFilm) {
        String sql = "INSERT INTO Shows (Show_Title, Show_Description, Show_Date, Show_Start_Time, Venue_ID, Hall_Type, Show_Price, Show_Duration, IsFilm) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String[] parts = dateTime.split(" ");
            String date = parts[0];
            String time = parts.length > 1 ? parts[1] : "19:00";
            int minutes = Integer.parseInt(duration.split(" ")[0]);
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            String formattedDuration = String.format("%02d:%02d:00", hours, remainingMinutes);
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, date);
            pstmt.setString(4, time);
            pstmt.setInt(5, venueId);
            pstmt.setString(6, hallType);
            pstmt.setDouble(7, price);
            pstmt.setString(8, formattedDuration);
            pstmt.setBoolean(9, isFilm);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // fix the show timings or date - managing the shows to the site
    public boolean updateShow(int showId, String showTitle, String showDate, int venueId, String hallType, double ticketPrice) {
        String query = "UPDATE Shows SET Show_Title = ?, Show_Date = ?, Venue_ID = ?, Hall_Type = ?, Show_Price = ? WHERE Show_ID = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, showTitle);
            preparedStatement.setString(2, showDate);
            preparedStatement.setInt(3, venueId);
            preparedStatement.setString(4, hallType);
            preparedStatement.setDouble(5, ticketPrice);
            preparedStatement.setInt(6, showId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}