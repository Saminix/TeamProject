package Swing;

/**
 * The ModelItem class represents a menu item with an optional icon, a menu name, and optional submenus.
 * It stores and provides access to the icon (path), the main menu name, and any associated submenus.
 */

public class ModelItem {
    private String icon;
    private String menuName;
    private String[] subMenu;

    public ModelItem(String icon, String menuName, String... subMenu) {
        this.icon = icon;
        this.menuName = menuName;
        this.subMenu = subMenu;
    }

    public String getIcon() {
        return icon;
    }

    public String getMenuName() {
        return menuName;
    }

    public String[] getSubMenu() {
        return subMenu;
    }
}
