
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * This is the Dashboard file. Run this file.
 * Subject to change for better implementation and focus of data.
 */
public class Dashboard {

    private static final Color PRIMARY_COLOR = new Color(30, 71, 19);
    private static final Color SECONDARY_COLOR = new Color(82, 109, 165);
    private static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BORDER_COLOR = new Color(230, 230, 230);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setUndecorated(true);

            // Create and add components
            JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

            // Title bar
            TitleBar titleBar = createCustomTitleBar();
            mainPanel.add(titleBar, BorderLayout.NORTH);

            // Main content area with sidebar and dashboard
            JPanel contentPanel = new JPanel(new BorderLayout(0, 0));

            // Use CardLayout for switching pages
            CardLayout cardLayout = new CardLayout();
            JPanel cardPanel = new JPanel(cardLayout);

            //dashboard = home
            cardPanel.add(createDashboardContent(), "Home");
            cardPanel.add(createReportsPage(), "Reports");
            cardPanel.add(createTicketsPage(), "Tickets");
            cardPanel.add(createPatronPage(), "Patron");
            cardPanel.add(createSeatingPage(), "Seating");
            cardPanel.add(createRefundsPage(), "Refunds");
            cardPanel.add(createSettingsPage(), "Settings");

            // Sidebar with navigation
            NavigationBar sidebar = new NavigationBar(cardLayout, cardPanel);
            contentPanel.add(sidebar, BorderLayout.WEST);

            contentPanel.add(cardPanel, BorderLayout.CENTER);

            mainPanel.add(contentPanel, BorderLayout.CENTER);

            // Add drag functionality to the title bar
            DragListener dragListener = new DragListener();
            titleBar.addMouseListener(dragListener);
            titleBar.addMouseMotionListener(dragListener);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }

    private static TitleBar createCustomTitleBar() {
        TitleBar titleBar = new TitleBar();
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setPreferredSize(new Dimension(0, 40));

        // Add custom title label
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        titleBar.add(titleLabel, BorderLayout.WEST);

        // Add close button
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

    private static JPanel createDashboardContent() {
        JPanel dashboardContent = new JPanel(new BorderLayout());
        dashboardContent.setBackground(BACKGROUND_COLOR);
        dashboardContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a panel for the top section
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel welcomeLabel = new JLabel("Welcome back, Muhammad!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 23));
        welcomeLabel.setForeground(TEXT_COLOR);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
        String formattedDate = now.format(formatter);

        JLabel dateLabel = new JLabel(formattedDate);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(120, 120, 120));

        JPanel headerTextPanel = new JPanel(new GridLayout(2, 1));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(welcomeLabel);
        headerTextPanel.add(dateLabel);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);

        // Add the header to the top section
        topSection.add(headerPanel, BorderLayout.NORTH);

        // Create metric cards
        JPanel cardPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardPanel.setOpaque(false);

        // Example data for future uses when integrated with the database - such as to calculate the revenue etc
        cardPanel.add(createMetricCard("$15,678", "Total Revenue", "📈 +12% from last month", PRIMARY_COLOR));
        cardPanel.add(createMetricCard("247", "New Patron", "👥 +8% from last month", SECONDARY_COLOR));
        cardPanel.add(createMetricCard("18", "Ticket Sales", "📦 -5% from last month", new Color(255, 153, 0)));

        topSection.add(cardPanel, BorderLayout.CENTER);


        dashboardContent.add(topSection, BorderLayout.NORTH);

        // Create a panel for the table
        JPanel tablePanel = new JPanel();
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tablePanel.setLayout(new BorderLayout());

        JLabel tableTitle = new JLabel("Recent Patron");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tableTitle.setForeground(TEXT_COLOR);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        tablePanel.add(tableTitle, BorderLayout.NORTH);

        // Table
        JTable table = new JTable();
        table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Name", "Email", "Personal", "Joined"}
        ));

        JScrollPane tableScrollPane = new JScrollPane(table);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add some data to the table
        ((javax.swing.table.DefaultTableModel) table.getModel()).addRow(new Object[]{"Mike Bhand", "mikebhand@gmail.com", "Patron", "25 Apr, 2021"});
        ((javax.swing.table.DefaultTableModel) table.getModel()).addRow(new Object[]{"Andrew Strauss", "andrewstrauss@gmail.com", "Patron", "25 Apr, 2021"});
        ((javax.swing.table.DefaultTableModel) table.getModel()).addRow(new Object[]{"Ross Kopelman", "rosskopelman@gmail.com", "FriendOfLancaster", "25 Apr, 2024"});
        ((javax.swing.table.DefaultTableModel) table.getModel()).addRow(new Object[]{"Mike Hussy", "mikehussy@gmail.com", "Admin", "Patron", "2024"});
        ((javax.swing.table.DefaultTableModel) table.getModel()).addRow(new Object[]{"Kevin Pietersen", "kevinpietersen@gmail.com", "Patron", "25 Apr, 2024"});

        dashboardContent.add(tablePanel, BorderLayout.CENTER);

        return dashboardContent;
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

        // Add a small colored indicator at the top
        JPanel indicator = new JPanel();
        indicator.setBackground(accentColor);
        indicator.setPreferredSize(new Dimension(0, 4));
        card.add(indicator, BorderLayout.NORTH);

        return card;
    }

    private static JPanel createReportsPage() {
        JPanel reportsPanel = new JPanel();
        reportsPanel.setBackground(BACKGROUND_COLOR);
        reportsPanel.add(new JLabel("Reports Page"));
        return reportsPanel;
    }

    private static JPanel createTicketsPage() {
        JPanel TicketsPanel = new JPanel();
        TicketsPanel.setBackground(BACKGROUND_COLOR);
        TicketsPanel.add(new JLabel("Tickets Page"));
        return TicketsPanel;
    }

    private static JPanel createPatronPage() {
        JPanel patronPanel = new JPanel();
        patronPanel.setBackground(BACKGROUND_COLOR);
        patronPanel.add(new JLabel("Patron Page"));
        return patronPanel;
    }


    private static JPanel createSeatingPage() {
        JPanel seatingPanel = new JPanel();
        seatingPanel.setBackground(BACKGROUND_COLOR);
        seatingPanel.add(new JLabel("Seating Page"));
        return seatingPanel;
    }


    private static JPanel createSettingsPage() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBackground(BACKGROUND_COLOR);
        settingsPanel.add(new JLabel("Settings Page"));
        return settingsPanel;
    }


    private static JPanel createRefundsPage() {
        JPanel refundPanel = new JPanel();
        refundPanel.setBackground(BACKGROUND_COLOR);
        refundPanel.add(new JLabel("Refunds Page"));
        return refundPanel;
    }


    // Class to handle window dragging
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