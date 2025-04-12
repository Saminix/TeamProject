package UI;

import db.dbConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * Refunds page
 * view refunded or cancelled bookings.
 * Search the refunds and issue a refund to a patron.
 * has an extensive search function to browse through patrons and shows/tickets for refunds
 * implementation of tabs for easy navigation
 */
public class RefundsPage extends JPanel {
    // Colors from PatronPage
    private static final Color PRIMARY_COLOR = new Color(89, 111, 89);
    private static final Color SECONDARY_COLOR = new Color(103, 120, 94);
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 243);
    private static final Color CARD_COLOR = new Color(200, 200, 200);
    private static final Color TEXT_COLOR = new Color(0, 0, 0);


    private Connection connection;
    private JTable refundableTable;
    private DefaultTableModel refundableTableModel;
    private JTextField searchField;
    private JButton searchButton, refundButton;
    private JComboBox<String> refundMethodBox;
    private JLabel selectedInfo;

    private JTable confirmedCancelledTable;
    private DefaultTableModel confirmedCancelledTableModel;
    private JTextField confirmedSearchField;
    private JButton confirmedSearchButton;

    /**
     * Create RefundsPage
     * database connection
     */

    public RefundsPage() throws SQLException {
        // Get database connection from dbConnection class + set the gui for the refunds
        connection = dbConnection.getConnection();
        setupUI();
        loadBookingData();
        loadConfirmedCancelledRefundedData();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Tabs for going between pages of refunds
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));

        // refundable Bookings Tabs
        JPanel refundableTab = createRefundableBookingsTab();
        tabbedPane.addTab("Refundable Bookings", refundableTab);

        // confirmed Cancelled/Refunded Bookings Tab
        JPanel confirmedCancelledTab = createConfirmedCancelledRefundedTab();
        tabbedPane.addTab("Confirmed Cancelled/Refunded Bookings", confirmedCancelledTab);
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     *  refundable bookings.
     * Search by table of refundable bookings
     *
     * @return the refundable bookings
     */
    private JPanel createRefundableBookingsTab() {
        JPanel refundablePanel = new JPanel(new BorderLayout());
        refundablePanel.setBackground(BACKGROUND_COLOR);
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.setBackground(BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchLabel.setForeground(TEXT_COLOR);
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchButton = new JButton("Search");
        styleButton(searchButton);

        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(searchButton);

        searchPanel.add(searchControlsPanel, BorderLayout.WEST);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(SECONDARY_COLOR), "Refundable Bookings", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), SECONDARY_COLOR), new EmptyBorder(10, 10, 10, 10)));String[] columns = {"Booking ID", "Patron Name", "Total Cost (£)", "Booking Date", "Cancelled", "Refunded"};
        refundableTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refundableTable = new JTable(refundableTableModel);
        refundableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refundableTable.setRowHeight(30);
        refundableTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        refundableTable.getTableHeader().setBackground(SECONDARY_COLOR);
        refundableTable.getTableHeader().setForeground(Color.WHITE);
        refundableTable.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(refundableTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(CARD_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel refundLabel = new JLabel("Refund Method:");
        refundLabel.setFont(new Font("Arial", Font.BOLD, 12));
        refundLabel.setForeground(TEXT_COLOR);
        refundMethodBox = new JComboBox<>(new String[]{"Select Method", "Card", "Cash", "Voucher"});
        refundMethodBox.setFont(new Font("Arial", Font.PLAIN, 12));
        refundButton = new JButton("Process Refund");
        styleButton(refundButton);
        refundButton.setEnabled(false);
        selectedInfo = new JLabel("Select a booking to refund.");
        selectedInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        selectedInfo.setForeground(TEXT_COLOR);
        bottomPanel.add(refundLabel);
        bottomPanel.add(refundMethodBox);
        bottomPanel.add(refundButton);
        bottomPanel.add(selectedInfo);
        refundableTable.getSelectionModel().addListSelectionListener(e -> {
            int row = refundableTable.getSelectedRow();
            if (row != -1) {
                int bookingId = (int) refundableTable.getValueAt(row, 0);selectedInfo.setText("Selected Booking ID: " + bookingId);refundButton.setEnabled(true);
            }
        });

        refundButton.addActionListener(e -> {
            int row = refundableTable.getSelectedRow();
            if (row != -1 && refundMethodBox.getSelectedIndex() > 0) {
                int bookingId = (int) refundableTable.getValueAt(row, 0);
                boolean bookingUpdated = markBookingAsRefunded(bookingId);
                boolean paymentUpdated = markPaymentAsRefunded(bookingId);
                boolean ticketUpdated = markTicketsAsUnsold(bookingId);
                boolean seatingUpdated = freeUpSeating(bookingId);
                if (bookingUpdated && paymentUpdated && ticketUpdated && seatingUpdated) {refundableTableModel.setValueAt("Yes", row, 4);refundableTableModel.setValueAt("Yes", row, 5);
                    JOptionPane.showMessageDialog(this, "Refund processed successfully!.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBookingData();
                    loadConfirmedCancelledRefundedData();
                } else {
                    JOptionPane.showMessageDialog(this, "Refund failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a refund method.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(refundableTableModel);
        refundableTable.setRowSorter(sorter);
        searchButton.addActionListener(e -> {
            String text = searchField.getText().trim();
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        refundablePanel.add(searchPanel, BorderLayout.NORTH);
        refundablePanel.add(tablePanel, BorderLayout.CENTER);
        refundablePanel.add(bottomPanel, BorderLayout.SOUTH);
        return refundablePanel;
    }
    /**
     * viewing confirmed cancelled or refunded bookings
     * @return the cancelled/refunds
     */
    private JPanel createConfirmedCancelledRefundedTab() {
        JPanel confirmedCancelledPanel = new JPanel(new BorderLayout());
        confirmedCancelledPanel.setBackground(BACKGROUND_COLOR);
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.setBackground(BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchLabel.setForeground(TEXT_COLOR);
        confirmedSearchField = new JTextField(20);
        confirmedSearchField.setFont(new Font("Arial", Font.PLAIN, 12));
        confirmedSearchButton = new JButton("Search");
        styleButton(confirmedSearchButton);
        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(confirmedSearchField);
        searchControlsPanel.add(confirmedSearchButton);

        searchPanel.add(searchControlsPanel, BorderLayout.WEST);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(SECONDARY_COLOR), "Confirmed Cancelled/Refunded Bookings", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), SECONDARY_COLOR), new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columns = {"Booking ID", "Patron Name", "Total Cost (£)", "Booking Date", "Cancelled", "Refunded"};
        confirmedCancelledTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        confirmedCancelledTable = new JTable(confirmedCancelledTableModel);
        confirmedCancelledTable.setRowHeight(30);
        confirmedCancelledTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        confirmedCancelledTable.getTableHeader().setBackground(SECONDARY_COLOR);
        confirmedCancelledTable.getTableHeader().setForeground(Color.WHITE);
        confirmedCancelledTable.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(confirmedCancelledTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(confirmedCancelledTableModel);
        confirmedCancelledTable.setRowSorter(sorter);
        confirmedSearchButton.addActionListener(e -> {
            String text = confirmedSearchField.getText().trim();
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        confirmedCancelledPanel.add(searchPanel, BorderLayout.NORTH);
        confirmedCancelledPanel.add(tablePanel, BorderLayout.CENTER);
        return confirmedCancelledPanel;
    }
    private void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40, 90, 25));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    // query to find all bookings - so refund is recorded in bookings and tickets data.
    private ResultSet getAllBookings() throws SQLException {
        String query = "SELECT b.Booking_ID, CONCAT(p.First_Name, ' ', p.Last_Name) AS Patron_Name, " +
                "b.TotalCost, b.Booking_Date, b.IsCancelled, " +
                "CASE WHEN pm.IsRefunded IS NULL THEN 0 ELSE pm.IsRefunded END as IsRefunded " +
                "FROM Booking b " +
                "LEFT JOIN Payment pm ON b.Booking_ID = pm.Booking_ID " +
                "LEFT JOIN Patron p ON b.Patron_ID = p.Patron_ID " +
                "WHERE b.IsCancelled = 1 AND (pm.IsRefunded = 0 OR pm.IsRefunded IS NULL)";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }
    /**
     * get confirmed cancelled/ refunded bookings from the relational database
     *
     * @return a the confirmed cancelled/refunded
     * @throws SQLException = if error
     */
    private ResultSet getConfirmedCancelledRefundedBookings() throws SQLException {
        String query = "SELECT b.Booking_ID, CONCAT(p.First_Name, ' ', p.Last_Name) AS Patron_Name, " +
                "b.TotalCost, b.Booking_Date, b.IsCancelled, pm.IsRefunded " +
                "FROM Booking b " +
                "LEFT JOIN Payment pm ON b.Booking_ID = pm.Booking_ID " +
                "LEFT JOIN Patron p ON b.Patron_ID = p.Patron_ID " +
                "WHERE b.IsCancelled = 1 AND pm.IsRefunded = 1";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }
    /**
     * check the booking as cancelled 
     * @param bookingId the ID 
     * @return update true/false
     */
    private boolean markBookingAsRefunded(int bookingId) {
        // First check if the booking exists
        try {
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT IsCancelled FROM Booking WHERE Booking_ID = ?");
            checkStmt.setInt(1, bookingId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                return false;
            }

            boolean alreadyCancelled = rs.getInt("IsCancelled") == 1;
            rs.close();
            checkStmt.close();

            if (alreadyCancelled) {
                return true;
            }

            PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE Booking SET IsCancelled = 1, Seat_ID = NULL WHERE Booking_ID = ?");
            updateStmt.setInt(1, bookingId);
            return updateStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // mark as refunded 
    private boolean markPaymentAsRefunded(int bookingId) {
        try {
            // First check if payment exists
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT IsRefunded FROM Payment WHERE Booking_ID = ?");
            checkStmt.setInt(1, bookingId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                PreparedStatement insertStmt = connection.prepareStatement(
                        "INSERT INTO Payment (Payment_Method, Payment_Date, Amount_Paid, IsRefunded, Booking_ID, Patron_ID) " +
                                "SELECT 'Refund', NOW(), -TotalCost, 1, Booking_ID, Patron_ID FROM Booking WHERE Booking_ID = ?");
                insertStmt.setInt(1, bookingId);
                return insertStmt.executeUpdate() > 0;
            }

            boolean alreadyRefunded = rs.getInt("IsRefunded") == 1;
            rs.close();
            checkStmt.close();

            if (alreadyRefunded) {
                // Already refunded, consider this a success
                return true;
            }

            // Update the payment to refunded
            PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE Payment SET IsRefunded = 1 WHERE Booking_ID = ?");
            updateStmt.setInt(1, bookingId);
            return updateStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // mark the ticket is now unsold
    private boolean markTicketsAsUnsold(int bookingId) {
        try {
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM Ticket WHERE Booking_ID = ?");
            checkStmt.setInt(1, bookingId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int ticketCount = rs.getInt(1);
            rs.close();
            checkStmt.close();

            if (ticketCount == 0) {
                return true;
            }
            PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE Ticket SET IsSold = 0, Seat_ID = NULL WHERE Booking_ID = ?");
            updateStmt.setInt(1, bookingId);
            return updateStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // seating will be updated.
    private boolean freeUpSeating(int bookingId) {
        try {
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM Seat_Availability WHERE Booking_ID = ?");
            checkStmt.setInt(1, bookingId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int seatCount = rs.getInt(1);
            rs.close();
            checkStmt.close();

            if (seatCount == 0) {
                return true;
            }

            PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE Seat_Availability SET Status = 'Available', Booking_ID = NULL WHERE Booking_ID = ?");
            updateStmt.setInt(1, bookingId);
            return updateStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // fetch the booking data 
    private void loadBookingData() {
        refundableTableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("£#,##0.00");

        try (ResultSet data = getAllBookings()) {
            while (data.next()) {
                Object[] row = {
                        data.getInt("Booking_ID"), data.getString("Patron_Name"), df.format(data.getDouble("TotalCost")), data.getTimestamp("Booking_Date"), data.getInt("IsCancelled") == 1 ? "Yes" : "No", data.getInt("IsRefunded") == 1 ? "Yes" : "No"
                };refundableTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading booking data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // confirm the refunded/cancelled data
    private void loadConfirmedCancelledRefundedData() {
        confirmedCancelledTableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("£#,##0.00");
        try (ResultSet data = getConfirmedCancelledRefundedBookings()) {
            while (data.next()) {
                Object[] row = {data.getInt("Booking_ID"), data.getString("Patron_Name"), df.format(data.getDouble("TotalCost")), data.getTimestamp("Booking_Date"), data.getInt("IsCancelled") == 1 ? "Yes" : "No", data.getInt("IsRefunded") == 1 ? "Yes" : "No"
                };confirmedCancelledTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading confirmed cancelled/refunded data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}