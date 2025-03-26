import javax.swing.*;

/**
 * Main file that runs the application
 *
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login();
        });
    }
}