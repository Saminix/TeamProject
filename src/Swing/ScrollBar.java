package Swing;

import java.awt.Dimension;
import javax.swing.JScrollBar;

/**
 * The ScrollBar class customises the appearance and behavior of a JScrollBar.
 * It uses a custom ScrollBarUI for styling, sets a small preferred size,
 *
 */
public class ScrollBar extends JScrollBar {
    public ScrollBar() {
        setUI(new ScrollBarUI());
        setPreferredSize(new Dimension(5, 5));
        setOpaque(false);
        setUnitIncrement(20);
    }
}