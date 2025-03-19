package Swing;


import javax.swing.*;
import java.awt.*;



/**
 * This is the TitleBar Class.
 * Subject to the 3 dots at the top of the GUI panel
 * Close (red): Closes the application.
 * Minimize (yellow): Minimizes the window.
 * Resize (green): Toggles between normal and maximized window states.
 */


public class TitleBar extends JPanel {
    public TitleBar() {
        init();
    }

    private void init() {


        JPanel panel = new JPanel();
        panel.setOpaque(false);
        add(panel);

        Item close = new Item(new Color(235, 47, 47));
        Item minimize = new Item(new Color(220, 213, 53));
        Item resize = new Item(new Color(44, 203, 87));

        panel.add(close);
        panel.add(minimize);
        panel.add(resize);

        close.addActionListener(e -> System.exit(0));

        minimize.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(TitleBar.this);
            if (frame != null) {
                frame.setState(JFrame.ICONIFIED);
            }
        });

        resize.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(TitleBar.this);
            if (frame != null) {
                if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    frame.setExtendedState(JFrame.NORMAL);
                } else {
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            }
        });
    }

    private static class Item extends JButton {
        public Item(Color color) {
            setBackground(color);
            setPreferredSize(new Dimension(11, 11));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setBorder(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
