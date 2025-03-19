package Swing;

import javax.swing.*;
import java.awt.*;
/**
 * Form class is simple user input form with a label, text field, and submit button.
 *
 * Components:
 * - JLabel: Displays the form's title.
 * - JTextField: Allows user input.
 * - JButton: A submit button to trigger form submission.
 */

public class Form extends JPanel {

    private JLabel lb;
    private JTextField textField;
    private JButton submitButton;

    public Form(String name) {
        initComponents();
        lb.setText("Form " + name);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        lb = new JLabel();
        lb.setFont(new Font("Arial", Font.BOLD, 16));
        lb.setForeground(Color.BLACK);
        lb.setHorizontalAlignment(JLabel.CENTER);

        textField = new JTextField();
        textField.setColumns(20);

        submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0, 122, 204));
        submitButton.setForeground(Color.WHITE);


        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lb, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(textField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(submitButton, gbc);
    }
}
