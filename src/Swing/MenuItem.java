
package Swing;

import javax.swing.*;
import java.awt.*;

/**
 * POSSIBLE FUTURE USE
 * The MenuItem class represents a single menu item with an optional set of submenus.
 * It displays a main menu button along with any submenus defined in the ModelItem.
 * The appearance includes a dark background with contrasting text and buttons for each submenu.
 */


public class MenuItem extends JPanel {
    private final ModelItem item;

    public MenuItem(ModelItem item) {
        this.item = item;
        setLayout(new GridLayout(0, 1, 0, 5));
        setOpaque(true);
        setBackground(new Color(40, 40, 40));

        // The Main menu button
        JButton menuButton = new JButton(item.getMenuName());
        menuButton.setBackground(new Color(60, 60, 60));
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        add(menuButton);


        if (item.getSubMenu().length > 0) {
            for (String subMenu : item.getSubMenu()) {
                JButton subButton = new JButton("   " + subMenu);
                subButton.setBackground(new Color(80, 80, 80));
                subButton.setForeground(Color.WHITE);
                subButton.setFocusPainted(false);
                add(subButton);
            }
        }
    }
}
