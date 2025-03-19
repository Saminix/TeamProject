package Swing;

import java.awt.Color;

/**
 * FUTURE USE?
 * A class representing an Icon with color schemes.
 */

public class Icon {

    private String icon;
    private Color color1;
    private Color color2;
    private String values;
    private String description;

    public Icon(String icon, Color color1, Color color2, String values, String description) {
        this.icon = icon;
        this.color1 = color1;
        this.color2 = color2;
        this.values = values;
        this.description = description;
    }

}