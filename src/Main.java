import javax.swing.*;

/**
 * Main file that runs the application
 * @main starts from the main application and opens up the login page.
 *
 */

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login();
        });
    }
}