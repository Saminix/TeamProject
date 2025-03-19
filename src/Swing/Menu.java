package Swing;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * The Menu class creates a sidebar menu with sections and items that can be selected.
 * It organiSes the menu into titles and submenu items, allowing for easy expansion of options.
 * Each menu item triggers events when selected.
 */

public class Menu extends JPanel {

    private final List<EventMenuSelected> events = new ArrayList<>();

    public Menu() {
        init();
    }

    private void init() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(null);
        scroll.setVerticalScrollBar(new ScrollBar());
        scroll.getViewport().setOpaque(false);

        JPanel panelMenu = new JPanel();
        panelMenu.setOpaque(false);
        panelMenu.setLayout(new GridLayout(0, 1, 0, 5)); // Vertical layout with 5px gap

        addTitle(panelMenu, "MAIN");
        addMenuItem(panelMenu, new ModelItem(null, "Dashboard"));
        addTitle(panelMenu, "WEB APPS");
        addMenuItem(panelMenu, new ModelItem(null, "Email", "Inbox", "Read", "Compose"));
        addMenuItem(panelMenu, new ModelItem(null, "Chat"));
        addMenuItem(panelMenu, new ModelItem(null, "Calendar"));

        scroll.setViewportView(panelMenu);
        add(scroll);
    }

    private void addTitle(JPanel panel, String title) {
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 5));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setForeground(new Color(170, 170, 170));
        panel.add(label);
    }

    private void addMenuItem(JPanel panel, ModelItem item) {
        Item menuItem = new Item(true);
        menuItem.setText("  " + item.getMenuName());
        menuItem.addActionListener(e -> {
            for (EventMenuSelected event : events) {
                event.menuSelected(0, 0); // Simplified index handling
            }
        });
        panel.add(menuItem);
    }

    public void addEvent(EventMenuSelected event) {
        events.add(event);
    }
}