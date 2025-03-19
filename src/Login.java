
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Login
 * Will allow the User to Login with username and password
 * DB connection is a priority.
 */
public class Login extends JFrame {
    private JTextField username;
    private JPasswordField password;
    private JLabel labelPassword, labelUsername, message;
    private JButton loginButton, resetButton;
    private JCheckBox showPassword;

    Login() {
        setTitle("Lancaster's Music Hall");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(970, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(17, 32, 35));
        imagePanel.setPreferredSize(new Dimension(480, 400));
        imagePanel.setLayout(new BorderLayout());


        ImageIcon imageIcon = new ImageIcon("data/Lancaster'sLogo.png");
        Image image = imageIcon.getImage().getScaledInstance(480, 400, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        imagePanel.add(imageLabel, BorderLayout.CENTER);


        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(229, 228, 226));
        loginPanel.setLayout(null);


        labelUsername = new JLabel("Username ");
        labelUsername.setFont(new Font("Arial", Font.PLAIN, 20));
        labelUsername.setBounds(55, 150, 150, 30);

        labelPassword = new JLabel("Password ");
        labelPassword.setFont(new Font("Arial", Font.PLAIN, 20));
        labelPassword.setBounds(55, 200, 150, 30);


        username = new JTextField();
        username.setFont(new Font("Arial", Font.PLAIN, 15));

        username.setBounds(165, 150, 250, 30);
        username.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));


        password = new JPasswordField();
        password.setFont(new Font("Arial", Font.PLAIN, 15));
        password.setBounds(165, 200, 250, 30);

        password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));


        showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(165, 240, 150, 30);
        showPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        showPassword.addActionListener(e -> {
            password.setEchoChar(showPassword.isSelected() ? (char) 0 : '*');
        });


        loginButton = new JButton("Login");
        loginButton.setBounds(165, 280, 100, 40);
        loginButton.addActionListener(e -> handleLogin());
        loginButton.setOpaque(true);
        loginButton.setBackground(new Color(2, 75, 48));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(1, 50, 32), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        resetButton = new JButton("Reset");
        resetButton.setBounds(275, 280, 100, 40);
        resetButton.addActionListener(e -> resetFields());
        resetButton.setOpaque(true);
        resetButton.setBackground(new Color(2, 75, 48));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Arial", Font.BOLD, 18));
        resetButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(1, 50, 32), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));


        message = new JLabel("Lancaster's Music Hall 2025", SwingConstants.CENTER);
        message.setBounds(105, 330, 300, 30);




        Border outerBorder = BorderFactory.createLineBorder(new Color(229, 228, 226), 50);
        Border innerBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border compoundBorder = BorderFactory.createCompoundBorder(outerBorder, innerBorder);

        loginPanel.setBorder(compoundBorder);



        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color( 1, 25, 16));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(2, 75, 48));
            }
        });


        resetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                resetButton.setBackground(new Color( 1, 25, 16));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                resetButton.setBackground(new Color(2, 75, 48));
            }
        });

        loginPanel.add(labelUsername);
        loginPanel.add(username);
        loginPanel.add(labelPassword);
        loginPanel.add(password);
        loginPanel.add(showPassword);
        loginPanel.add(loginButton);
        loginPanel.add(resetButton);
        loginPanel.add(message);


        add(imagePanel, BorderLayout.WEST);
        add(loginPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void handleLogin() {
        String user = username.getText();
        String pass = new String(password.getPassword());

        if (dbConnection.LoginUser(user, pass)) {
            message.setText("Login Successful");
            JOptionPane.showMessageDialog(this, "Welcome, " + user + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);

        } else {
            message.setText("Invalid Username or Password");
            JOptionPane.showMessageDialog(this, "Invalid login credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void resetFields() {
        username.setText("");
        password.setText("");
        message.setText("Lancaster's Music Hall 2025");
    }


}
