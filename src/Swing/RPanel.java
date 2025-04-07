package Swing;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * The RPanel class is a custom JPanel with rounded corners and a gradient background.
 * It allows customization of the corner roundness and paints a gradient from one color to another.
 */

public class RPanel extends JPanel {
    private int round;

    public RPanel() {
        setOpaque(false);
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Area area = new Area(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), round, round));
        g2.setColor(getBackground());
        g2.fill(area);
        area.subtract(new Area(new Rectangle2D.Double(0, 0, getWidth(), getHeight() - 3)));
        g2.setPaint(new GradientPaint(0, 0, SystemColour.MAIN_COLOR_1, getWidth(), 0, SystemColour.MAIN_COLOR_2));
        g2.fill(area);
        g2.dispose();
        super.paintComponent(g);
    }
}