package UI;
import db.dbConnection;

import javax.swing.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Refunds page
 * view refunded or cancelled bookings.
 * Search the refunds and issue a refund to a patron.
 * has an extensive search function to browse through patrons and shows/tickets for refunds
 * implementation of tabs for easy navigation
 */
public class PatronPage extends JPanel {
    // Colours scheme
    private static final Color PRIMARY_COLOR = new Color(99, 107, 99);
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 243);
    private static final Color CARD_COLOR = new Color(200, 200, 200);

    private Connection connection;

    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JTable patronsTable;
    private DefaultTableModel patronsTableModel;
    private JTable groupBookingsTable;
    private DefaultTableModel groupBookingsTableModel;

  
    public PatronPage() throws SQLException {
        connection = dbConnection.getConnection();
        setupUI();
        loadData();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Patron Information", createPatronPanel());
        tabbedPane.addTab("Group Bookings", createGroupBookingsPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Creates the display of searching up each patron/customer/friend of lancasters/paid subscription.
     * Search bar to search for patrons and filter them by discounts/LF/disabilities.
     * export data to a CSV file.
     *
     * @return patron information
     */
    private JPanel createPatronPanel() {
        JPanel patronPanel = new JPanel(new BorderLayout(10, 10));
        patronPanel.setBackground(BACKGROUND_COLOR);
        patronPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel searchLabel = new JLabel("Search Patrons:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterPatronsTable();}

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterPatronsTable();}

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterPatronsTable();
            }
        });

        String[] filterOptions = {"All", "Patron", "Military", "NHS", "Disability", "LancasterFriend"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> filterPatronsTable());

        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.setBackground(BACKGROUND_COLOR);
        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(new JLabel("Filter by:"));
        searchControlsPanel.add(filterComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CARD_COLOR);

        JButton PatronButton = new JButton("Full Details");
        JButton exportButton = new JButton("Export Data");

        styleButton(PatronButton);
        styleButton(exportButton);

        PatronButton.addActionListener(e -> viewSelectedPatron());
        exportButton.addActionListener(e -> exportPatronData());

        buttonPanel.add(PatronButton);
        buttonPanel.add(exportButton);

        searchPanel.add(searchControlsPanel, BorderLayout.WEST);
        searchPanel.add(buttonPanel, BorderLayout.EAST);

        String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Telephone", "DOB", "Type", "Lancaster Friend"};
        patronsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) {
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

        patronPanel.add(searchPanel, BorderLayout.NORTH);
        patronPanel.add(tableScrollPane, BorderLayout.CENTER);

        return patronPanel;
    }

    /**
     * Create group bookings tab with search bar and options to manage the entire group
     * include data retrival from database tables group bookings
     */
    private JPanel createGroupBookingsPanel() {
        JPanel groupBookingsPanel = new JPanel(new BorderLayout(10, 10));
        groupBookingsPanel.setBackground(BACKGROUND_COLOR);
        groupBookingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel("Search Groups:");
        JTextField groupSearchField = new JTextField(20);

        searchPanel.add(searchLabel);
        searchPanel.add(groupSearchField);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(BACKGROUND_COLOR);

        JButton manageGroupButton = new JButton("Manage Selected");
        JButton exportGroupsButton = new JButton("Export Groups");
        styleButton(manageGroupButton);
        styleButton(exportGroupsButton);

        manageGroupButton.addActionListener(e -> manageSelectedGroup());
        exportGroupsButton.addActionListener(e -> exportGroupData());

        buttonsPanel.add(manageGroupButton);
        buttonsPanel.add(exportGroupsButton);

        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        controlsPanel.add(searchPanel, BorderLayout.WEST);
        controlsPanel.add(buttonsPanel, BorderLayout.EAST);

        String[] groupColumns = {"Group ID", "Group Name", "Contact Name", "Contact Email", "Group Size", "Booking ID", "Show Title", "Date", "Status"};
        groupBookingsTableModel = new DefaultTableModel(groupColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells uneditable
            }
        };
        groupBookingsTable = new JTable(groupBookingsTableModel);
        groupBookingsTable.setRowHeight(30);
        groupBookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        groupBookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane groupsScrollPane = new JScrollPane(groupBookingsTable);
        groupsScrollPane.setBorder(BorderFactory.createEmptyBorder());

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

        groupBookingsPanel.add(controlsPanel, BorderLayout.NORTH);
        groupBookingsPanel.add(groupsScrollPane, BorderLayout.CENTER);

        return groupBookingsPanel;
    }

    // Load data from database
    private void loadData() {
        loadPatronsData();
        loadGroupBookingsData();
    }

    // Load patrons data from database
    private void loadPatronsData() {
        patronsTableModel.setRowCount(0);

        try {
            String query = "SELECT * FROM Patron";
            try (Statement stmt = connection.createStatement();
                 ResultSet data = stmt.executeQuery(query)) {
                while (data.next()) {
                    Vector<Object> row = new Vector<>(); row.add(data.getInt("Patron_ID"));
                    row.add(data.getString("First_Name"));
                    row.add(data.getString("Last_Name"));
                    row.add(data.getString("Email"));
                    row.add(data.getString("Telephone"));
                    row.add(data.getDate("Date_of_Birth"));
                    row.add(data.getString("Patron_Type"));
                    row.add(data.getBoolean("IsLancasterFriend"));
                    patronsTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading patron data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load patron data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * get group booking data from the database to show group bookings on display.
     */
    private void loadGroupBookingsData() {
        groupBookingsTableModel.setRowCount(0);

        try {
            String query = "SELECT gb.Group_Booking_ID, gb.Group_Name, gb.Contact_Name, gb.Contact_Email, " + "gb.Group_Size, gb.Booking_ID, gb.IsConfirmed, s.Show_Title, s.Show_Date " +
                    "FROM GroupBooking gb " +
                    "JOIN Booking b ON gb.Booking_ID = b.Booking_ID " +
                    "JOIN Shows s ON b.Show_ID = s.Show_ID " +
                    "ORDER BY s.Show_Date";
            try (Statement stmt = connection.createStatement();
                 ResultSet data = stmt.executeQuery(query)) {
                while (data.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(data.getInt("Group_Booking_ID"));
                    row.add(data.getString("Group_Name"));
                    row.add(data.getString("Contact_Name"));
                    row.add(data.getString("Contact_Email"));
                    row.add(data.getInt("Group_Size"));
                    row.add(data.getInt("Booking_ID"));
                    row.add(data.getString("Show_Title"));
                    row.add(data.getDate("Show_Date"));
                    row.add(data.getBoolean("IsConfirmed") ? "Confirmed" : "Pending");
                    groupBookingsTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading group bookings: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load group bookings: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Filters the patrons by filter by patron
     */
    private void filterPatronsTable() {
        String searchText = searchField.getText().toLowerCase();
        String filterType = (String) filterComboBox.getSelectedItem();

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(patronsTable.getModel());
        patronsTable.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 1, 2, 3, 4));
        }
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

    /**
     * Filter the group bookings
     * search by bar
     */
    private void filterGroupBookings(String searchText) {
        if (searchText.isEmpty()) {
            groupBookingsTable.setRowSorter(null);
            return;
        }
        searchText = searchText.toLowerCase();
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(groupBookingsTable.getModel());
        groupBookingsTable.setRowSorter(sorter);

        RowFilter<Object, Object> filter = RowFilter.regexFilter("(?i)" + searchText, 1, 2, 3, 6);
        sorter.setRowFilter(filter);
    }

    /**
     * create modern button
     */
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

    /**
     * get detailed information for each selected patron and the type.
     */
    private void viewSelectedPatron() {
        int selectedRow = patronsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patron to view", "No Selection", JOptionPane.WARNING_MESSAGE);
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
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(cancelButton);

        dialog.add(viewPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * option to export to CSV.
     * @return the export  patron data 
     */
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
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < patronsTableModel.getColumnCount(); i++) {
                    header.append(patronsTableModel.getColumnName(i));
                    if (i < patronsTableModel.getColumnCount() - 1) {
                        header.append(",");
                    }
                }writer.println(header.toString());

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
                        "Patron data exported successfully to " + filePath, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * manage the selected group booking by extracting the core details of the booking.
     * confirm or cancel the booking with real-time databse updates.
     */
    private void manageSelectedGroup() {
        int selectedRow = groupBookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a group booking to manage", "No Selection", JOptionPane.WARNING_MESSAGE);
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
            StringBuilder details = new StringBuilder(); details.append("Group ID: ").append(groupId).append("\n");
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
            JOptionPane.showMessageDialog(dialog, scrollPane, "Group Booking Details", JOptionPane.INFORMATION_MESSAGE);
        });

        confirmButton.addActionListener(e -> {
            try {
                String updateQuery = "UPDATE GroupBooking SET IsConfirmed = 1 WHERE Group_Booking_ID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setInt(1, groupId);
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(dialog, "Group booking confirmed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);dialog.dispose();
                        loadGroupBookingsData();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error confirming group booking: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to cancel this group booking?\nThis action cannot be undone.", "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                try {
                    String updateQuery = "UPDATE GroupBooking SET IsConfirmed = 0 WHERE Group_Booking_ID = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                        pstmt.setInt(1, groupId);
                        int result = pstmt.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(dialog, "Group booking has been cancelled.", "Booking Cancelled", JOptionPane.INFORMATION_MESSAGE);dialog.dispose();
                            loadGroupBookingsData();
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error cancelling group booking: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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


    /**
     * Export group bookings to a CSV.
     */
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
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < groupBookingsTableModel.getColumnCount(); i++) {
                    header.append(groupBookingsTableModel.getColumnName(i));
                    if (i < groupBookingsTableModel.getColumnCount() - 1) {
                        header.append(",");
                    }
                }
                writer.println(header.toString());
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
                        "Group bookings data exported successfully to " + filePath, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}