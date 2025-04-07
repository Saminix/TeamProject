package Swing;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A custom JButton used for menu items with hover effects and a selection indicator.
 * It changes color when hovered and draws a line on the left when selected.
 */

public class Item extends JButton {

    private final Color mainColor = SystemColour.MAIN_COLOR_2;

    public Item(boolean mainMenu) {
        init(mainMenu);
    }

    private void init(boolean mainMenu) {
        setContentAreaFilled(false);
        setHorizontalAlignment(JButton.LEFT);
        setForeground(new Color(50, 50, 50));
        if (mainMenu) {
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        } else {
            setBorder(BorderFactory.createEmptyBorder(0, 51, 0, 0));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(mainColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected()) {
                    setForeground(new Color(50, 50, 50));
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isSelected()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(mainColor);
            g2.fillRect(0, 3, 3, getHeight() - 6);
            g2.dispose();
        }
    }
}