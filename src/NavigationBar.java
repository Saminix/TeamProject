import Swing.ScrollBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.File;
/**
 * NavigationBar is for subpages and easy navigation.
 * These subpages are of Pages: Patron, reports, tickets, seating and Refunds
 * CardLayout is used to manage these contents
 */
public class NavigationBar extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(60, 90, 153);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BORDER_COLOR = new Color(230, 230, 230);

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel selectedNavItem = null;

    /**
     * Navigation bar is intialised to create navigation items
     * @param cardLayout content panels
     * @param contentPanel
     */

    public NavigationBar(CardLayout cardLayout, JPanel contentPanel) {
        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));
        setBackground(CARD_COLOR);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));


        JPanel profilePanel = createProfilePanel();
        add(profilePanel, BorderLayout.NORTH);

        JPanel navPanel = createNavigationPanel();

        JScrollPane scrollPane = new JScrollPane(navPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBar(new ScrollBar());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Muhammad's(Managers profile panel at the top of the navigation bar
     * @return the profile information
     */
    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(CARD_COLOR);
        profilePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        profilePanel.setPreferredSize(new Dimension(0, 100));

        JPanel profileImagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR);
                g2.fillOval(0, 0, 60, 60);
                g2.dispose();
            }
        };
        profileImagePanel.setPreferredSize(new Dimension(70, 70));
        profileImagePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Muhammad");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel roleLabel = new JLabel("Head Manager");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(120, 120, 120));

        JPanel profileInfoPanel = new JPanel(new GridLayout(2, 1));
        profileInfoPanel.setOpaque(false);
        profileInfoPanel.add(nameLabel);
        profileInfoPanel.add(roleLabel);

        JPanel profileContentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        profileContentPanel.setOpaque(false);
        profileContentPanel.add(profileImagePanel);
        profileContentPanel.add(profileInfoPanel);

        profilePanel.add(profileContentPanel, BorderLayout.CENTER);

        return profilePanel;
    }

    /**
     * create navigation bar with related pages
     * @return Navigation bar panel
     */
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(CARD_COLOR);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));


        String[] navItems = {"Home", "Tickets", "Reports", "Patron", "Seating", "Refunds", "Settings"};
        String[] iconPaths = {
                "Resources/home.png",
                "Resources/ticket.png",
                "Resources/report.png",
                "Resources/patron.png",
                "Resources/seats.png",
                "Resources/refund.png",
                "Resources/settings.png"
        };


        for (int i = 0; i < navItems.length; i++) {
            ImageIcon icon = null;
            try {
                icon = new ImageIcon(getClass().getResource(iconPaths[i]));
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            } catch (Exception e) {
                System.err.println("Error loading icon: " + iconPaths[i]);
            }

            JPanel navItem = createNavItem(icon, navItems[i], i == 0);
            navItem.setName(navItems[i]);
            navItem.addMouseListener(new NavItemClickListener());

            // Set first item as selected by default
            if (i == 0) {
                selectedNavItem = navItem;
            }

            navPanel.add(navItem);
        }

        return navPanel;
    }




    private JPanel createNavItem(ImageIcon icon, String text, boolean selected) {
        JPanel navItem = new JPanel(new BorderLayout());
        navItem.setMaximumSize(new Dimension(250, 50));
        navItem.setPreferredSize(new Dimension(250, 50));
        navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (selected) {
            navItem.setBackground(new Color(240, 242, 245));
            navItem.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, PRIMARY_COLOR));
        } else {
            navItem.setBackground(CARD_COLOR);
            navItem.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        }

        JLabel iconLabel = new JLabel();
        if (icon != null) {
            iconLabel.setIcon(icon);
        }
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        navItem.add(iconLabel, BorderLayout.WEST);
        navItem.add(textLabel, BorderLayout.CENTER);

        navItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (navItem != selectedNavItem) {
                    navItem.setBackground(new Color(245, 247, 250));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (navItem != selectedNavItem) {
                    navItem.setBackground(CARD_COLOR);
                }
            }
        });

        return navItem;
    }

    /**
     * add extra highlighting effects for the hover of switching between nav panels.
     */

    private class NavItemClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JPanel clickedPanel = (JPanel) e.getSource();
            if (clickedPanel == selectedNavItem) return;

            if (selectedNavItem != null) {
                selectedNavItem.setBackground(CARD_COLOR);
                selectedNavItem.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
            }

            clickedPanel.setBackground(new Color(240, 242, 245));
            clickedPanel.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, PRIMARY_COLOR));

            selectedNavItem = clickedPanel;
            cardLayout.show(contentPanel, clickedPanel.getName());
        }
    }
}
