package UI;

import db.dbConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Reports - handles Extensive management and search of all reports by revenue, shows, tickets by date and months
 * has an extensive layout of displaying the data by bar chart, pie char etc.
 * all data goes to relational database to be updated/ saved for tickets sales to be extracted by relevant dates or shows
 * manager can successfully export his reports
 * filters are made to search for reports or relevant names and real-time data retrieval
 * implementation of tabs for easy navigation
 */

public class ReportsPage extends JPanel {
    // Colours scheme
    private static final Color PRIMARY_COLOR = new Color(40, 47, 41);
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 243);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(0, 0, 0);

    private JTextField searchField, fromDateField, toDateField;
    private JComboBox<String> showCombo;
    private JButton searchButton, exportCsvButton;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable table;
    private DefaultTableModel tableModel;
    private ReportsChartPanel chartPanel;
    private Connection connection;

    /**
     * ReportsPage tab
     * UI layout
     * connection
     */
    public ReportsPage() {
        try {
            connection = dbConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        setupTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        chartPanel = new ReportsChartPanel(tableModel);
        add(chartPanel, BorderLayout.SOUTH);
        loadReportData();
    }

    /**
     * Creates the search bar for reports and tickets data
     * exporting to CSV.
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(CARD_COLOR);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Search Reports",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                PRIMARY_COLOR
        ));

        // Search filter and bar at the top of the reports page.
        searchField = createStyledTextField("Search by show title...", 30);
        searchButton = createStyledButton("Search");
        exportCsvButton = createStyledButton("Export CSV");
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setBackground(CARD_COLOR);
        searchBar.add(searchField);
        searchBar.add(searchButton);
        searchBar.add(exportCsvButton);
        JPanel sidePanel = new JPanel(new GridBagLayout());
        sidePanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        fromDateField = createStyledTextField("MM", 10);
        toDateField = createStyledTextField("MM", 10);
        showCombo = new JComboBox<>(loadShowTitles());

        gbc.gridx = 0; gbc.gridy = 0;sidePanel.add(new JLabel("From Month:"), gbc);gbc.gridx = 1;sidePanel.add(fromDateField, gbc);gbc.gridx = 0; gbc.gridy = 1;sidePanel.add(new JLabel("To Month:"), gbc);
        gbc.gridx = 1;sidePanel.add(toDateField, gbc);gbc.gridx = 0; gbc.gridy = 2;sidePanel.add(new JLabel("Show:"), gbc);gbc.gridx = 1;sidePanel.add(showCombo, gbc);
        searchPanel.add(searchBar, BorderLayout.CENTER);
        searchPanel.add(sidePanel, BorderLayout.EAST);
        searchButton.addActionListener(e -> generateReport());
        exportCsvButton.addActionListener(e -> exportToCSV());

        return searchPanel;
    }

    private JTextField createStyledTextField(String placeholder, int columns) {
        JTextField field = new JTextField(columns);
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 5, 5, 5)));
        return field;
    }

    /**
     * a styled button / highlights
     * @param text text
     * @return button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
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
        return button;
    }

    /**
     * Loads shows by titles from the database.
     * @return show titles = all relevant shows
     */
    private String[] loadShowTitles() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT DISTINCT Show_Title FROM Shows");
             ResultSet rs = stmt.executeQuery()) {
            java.util.List<String> shows = new java.util.ArrayList<>();
            shows.add("All Shows");
            while (rs.next()) {
                shows.add(rs.getString("Show_Title"));
            }
            return shows.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[]{"All Shows"};
        }
    }

    // creation of table of columns for reports
    private void setupTable() {
        String[] columns = {
                "Month", "Show Title", "Tickets Sold", "Total Revenue",
                "Discounts Applied", "Refunds", "Profit"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
    }

    // create the report - visual
    private void generateReport() {
        loadReportData();
        applyFilters();
        chartPanel.updateChart();
    }

    /**
     * gets report data
     * Retrieves data for dates, shows.etc.
     */
    private void loadReportData() {
        // query for this data
        String query = "SELECT MONTHNAME(s.Show_Date) as Month, s.Show_Title, " +
                "COUNT(t.Ticket_ID) as Tickets_Sold, " +
                "SUM(t.Ticket_Price) as Total_Revenue, " +
                "SUM(b.Discount_Applied) as Discounts_Applied, " +
                "SUM(CASE WHEN p.IsRefunded = 1 THEN t.Ticket_Price ELSE 0 END) as Refunds " +
                "FROM Shows s " +
                "LEFT JOIN Ticket t ON s.Show_ID = t.Show_ID " +
                "LEFT JOIN Booking b ON t.Booking_ID = b.Booking_ID " +
                "LEFT JOIN Payment p ON b.Booking_ID = p.Booking_ID " +
                "WHERE YEAR(s.Show_Date) = YEAR(CURDATE()) " +
                "GROUP BY MONTH(s.Show_Date), MONTHNAME(s.Show_Date), s.Show_Title " +
                "ORDER BY MONTH(s.Show_Date)";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                double totalRevenue = rs.getDouble("Total_Revenue");
                double discounts = rs.getDouble("Discounts_Applied");
                double refunds = rs.getDouble("Refunds");
                double profit = totalRevenue - discounts - refunds;
                // form into objects
                Object[] row = {
                        rs.getString("Month"),
                        rs.getString("Show_Title"),
                        rs.getInt("Tickets_Sold"),
                        String.format("%.2f", totalRevenue),
                        String.format("%.2f", discounts),
                        String.format("%.2f", refunds),
                        String.format("%.2f", profit)
                };
                tableModel.addRow(row);
            }
        }catch (SQLException e) {e.printStackTrace();JOptionPane.showMessageDialog(this, "Failed to load reports: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // apply necessary filters
    private void applyFilters() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedShow = (String) showCombo.getSelectedItem();
        String from = fromDateField.getText().trim();
        String to = toDateField.getText().trim();
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String monthStr = entry.getStringValue(0).toLowerCase();
                String showTitle = entry.getStringValue(1).toLowerCase();
                boolean matchesSearch = searchText.isEmpty() || "search by show title...".equals(searchText) ||
                        showTitle.contains(searchText);
                boolean matchesShow = "All Shows".equals(selectedShow) || showTitle.equalsIgnoreCase(selectedShow);
                boolean matchesMonth = true;
                try {
                    DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMMM");
                    LocalDate tempDate = LocalDate.parse(monthStr.substring(0, 1).toUpperCase() + monthStr.substring(1) + " 1", monthFormat);
                    int monthNum = tempDate.getMonthValue();

                    if (!from.isEmpty() && !"mm".equalsIgnoreCase(from)) {
                        int fromMonth = Integer.parseInt(from);
                        matchesMonth &= monthNum >= fromMonth;
                    }
                    if (!to.isEmpty() && !"mm".equalsIgnoreCase(to)) {
                        int toMonth = Integer.parseInt(to);
                        matchesMonth &= monthNum <= toMonth;
                    }
                } catch (Exception ex) {matchesMonth = true;
                } return matchesSearch && matchesShow && matchesMonth;
            }
        });
    }
    /**
     * Exports a CSV file for based reports
     */
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report As CSV");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }try (FileWriter fw = new FileWriter(fileToSave)) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    fw.write("\"" + tableModel.getColumnName(i) + "\"");
                    if (i != tableModel.getColumnCount() - 1) fw.write(",");
                }fw.write("\n");
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        fw.write("\"" + String.valueOf(tableModel.getValueAt(row, col)) + "\"");
                        if (col != tableModel.getColumnCount() - 1) fw.write(",");
                    }
                    fw.write("\n");
                }
                JOptionPane.showMessageDialog(this, "Exported successfully to:\n" + fileToSave.getAbsolutePath(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}