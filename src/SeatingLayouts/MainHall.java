package SeatingLayouts;

import javax.swing.*;
import java.awt.*;

public class MainHall extends JFrame {

    public MainHall() {
        // Set up the JFrame
        setTitle("MainHall Seating Chart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Main panel with modern look
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(33, 33, 33), 2));

        // Balcony Section
        JLabel balconyLabel = new JLabel("BALCONY", SwingConstants.CENTER);
        balconyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balconyLabel.setForeground(new Color(33, 33, 33));
        mainPanel.add(balconyLabel);

        JPanel balconyPanel = new JPanel();
        balconyPanel.setLayout(new GridLayout(3, 1, 2, 2));
        balconyPanel.setBackground(Color.WHITE);

        // Row CC (1-8)
        JPanel rowCC = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        rowCC.setBackground(Color.WHITE);
        JLabel ccLabel = new JLabel("CC");
        ccLabel.setFont(new Font("Arial", Font.BOLD, 12));
        ccLabel.setForeground(new Color(33, 33, 33));
        rowCC.add(ccLabel);
        for (int i = 1; i <= 8; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22)); // Ensure size is respected
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rowCC.add(seat);
        }
        balconyPanel.add(rowCC);

        // Row BB (6-23)
        JPanel rowBB = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        rowBB.setBackground(Color.WHITE);
        JLabel bbLabel = new JLabel("BB");
        bbLabel.setFont(new Font("Arial", Font.BOLD, 12));
        bbLabel.setForeground(new Color(33, 33, 33));
        rowBB.add(bbLabel);
        for (int i = 6; i <= 23; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rowBB.add(seat);
        }
        balconyPanel.add(rowBB);

        // Row AA (21-33)
        JPanel rowAA = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        rowAA.setBackground(Color.WHITE);
        JLabel aaLabel = new JLabel("AA");
        aaLabel.setFont(new Font("Arial", Font.BOLD, 12));
        aaLabel.setForeground(new Color(33, 33, 33));
        rowAA.add(aaLabel);
        for (int i = 21; i <= 33; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rowAA.add(seat);
        }
        balconyPanel.add(rowAA);

        mainPanel.add(balconyPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Stalls Section
        JLabel stallsLabel = new JLabel("STALLS", SwingConstants.CENTER);
        stallsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        stallsLabel.setForeground(new Color(33, 33, 33));
        mainPanel.add(stallsLabel);

        JPanel stallsPanel = new JPanel();
        stallsPanel.setLayout(new GridLayout(16, 1, 2, 2));
        stallsPanel.setBackground(Color.WHITE);

        // Row Q (1-10)
        JPanel rowQ = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        rowQ.setBackground(Color.WHITE);
        JLabel qLabel = new JLabel("Q");
        qLabel.setFont(new Font("Arial", Font.BOLD, 12));
        qLabel.setForeground(new Color(33, 33, 33));
        rowQ.add(qLabel);
        for (int i = 1; i <= 10; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rowQ.add(seat);
        }
        stallsPanel.add(rowQ);

        // Row P (1-11)
        JPanel rowP = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        rowP.setBackground(Color.WHITE);
        JLabel pLabel = new JLabel("P");
        pLabel.setFont(new Font("Arial", Font.BOLD, 12));
        pLabel.setForeground(new Color(33, 33, 33));
        rowP.add(pLabel);
        for (int i = 1; i <= 11; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rowP.add(seat);
        }
        stallsPanel.add(rowP);

        // Row O
        createRowO(stallsPanel);

        // Row N
        createRowN(stallsPanel);

        // Row M
        createRowM(stallsPanel);

        // Rows L to A (excluding I)
        char[] rowsToInclude = {'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        for (char rowLetter : rowsToInclude) {
            createSpecialRow(stallsPanel, rowLetter);
        }

        mainPanel.add(stallsPanel);

        // Side balconies
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBackground(Color.WHITE);
        sidePanel.add(mainPanel, BorderLayout.CENTER);

        // Left Balcony - BB 1-5, AA 1-20
        JPanel leftBalcony = new JPanel();
        leftBalcony.setLayout(new BoxLayout(leftBalcony, BoxLayout.X_AXIS)); // Use BoxLayout to respect component sizes
        leftBalcony.setBackground(Color.WHITE);
        leftBalcony.setPreferredSize(new Dimension(160, 0)); // Increased width to fit seats

        JPanel leftBB = new JPanel();
        leftBB.setLayout(new BoxLayout(leftBB, BoxLayout.Y_AXIS));
        leftBB.setBackground(Color.WHITE);
        JLabel leftBBLabel = new JLabel("BB", SwingConstants.CENTER);
        leftBBLabel.setFont(new Font("Arial", Font.BOLD, 12));
        leftBBLabel.setForeground(new Color(33, 33, 33));
        leftBB.add(leftBBLabel);
        for (int i = 5; i >= 1; i--) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            leftBB.add(seat);
            if (i > 1) leftBB.add(Box.createVerticalStrut(2));
        }
        leftBalcony.add(leftBB);
        leftBalcony.add(Box.createHorizontalStrut(10)); // Small gap between BB and AA

        JPanel leftAA = new JPanel();
        leftAA.setLayout(new BoxLayout(leftAA, BoxLayout.Y_AXIS));
        leftAA.setBackground(Color.WHITE);
        JLabel leftAALabel = new JLabel("AA", SwingConstants.CENTER);
        leftAALabel.setFont(new Font("Arial", Font.BOLD, 12));
        leftAALabel.setForeground(new Color(33, 33, 33));
        leftAA.add(leftAALabel);
        for (int i = 20; i >= 6; i--) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            leftAA.add(seat);
            if (i > 6) leftAA.add(Box.createVerticalStrut(2));
        }
        JButton seat5 = new JButton("5");
        seat5.setPreferredSize(new Dimension(28, 22));
        seat5.setMinimumSize(new Dimension(28, 22));
        seat5.setMaximumSize(new Dimension(28, 22));
        seat5.setMargin(new Insets(0, 0, 0, 0));
        seat5.setFont(new Font("Arial", Font.PLAIN, 10));
        seat5.setBackground(new Color(200, 230, 201));
        seat5.setForeground(new Color(33, 33, 33));
        seat5.setFocusPainted(false);
        seat5.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        leftAA.add(seat5);
        leftAA.add(Box.createVerticalStrut(20));
        leftAA.add(Box.createVerticalStrut(20));
        leftAA.add(Box.createVerticalStrut(20));
        for (int i = 4; i >= 1; i--) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            leftAA.add(seat);
            if (i > 1) leftAA.add(Box.createVerticalStrut(2));
        }
        leftBalcony.add(leftAA);
        sidePanel.add(leftBalcony, BorderLayout.WEST);

        // Right Balcony - AA 34-53, BB 24-28
        JPanel rightBalcony = new JPanel();
        rightBalcony.setLayout(new BoxLayout(rightBalcony, BoxLayout.X_AXIS)); // Use BoxLayout to respect component sizes
        rightBalcony.setBackground(Color.WHITE);
        rightBalcony.setPreferredSize(new Dimension(160, 0)); // Increased width to fit seats

        JPanel rightAA = new JPanel();
        rightAA.setLayout(new BoxLayout(rightAA, BoxLayout.Y_AXIS));
        rightAA.setBackground(Color.WHITE);
        JLabel rightAALabel = new JLabel("AA", SwingConstants.CENTER);
        rightAALabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightAALabel.setForeground(new Color(33, 33, 33));
        rightAA.add(rightAALabel);
        for (int i = 34; i <= 49; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rightAA.add(seat);
            if (i < 49) rightAA.add(Box.createVerticalStrut(2));
        }
        rightAA.add(Box.createVerticalStrut(20));
        rightAA.add(Box.createVerticalStrut(20));
        rightAA.add(Box.createVerticalStrut(20));
        for (int i = 50; i <= 53; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rightAA.add(seat);
            if (i < 53) rightAA.add(Box.createVerticalStrut(2));
        }
        rightBalcony.add(rightAA);
        rightBalcony.add(Box.createHorizontalStrut(10)); // Small gap between AA and BB

        JPanel rightBB = new JPanel();
        rightBB.setLayout(new BoxLayout(rightBB, BoxLayout.Y_AXIS));
        rightBB.setBackground(Color.WHITE);
        JLabel rightBBLabel = new JLabel("BB", SwingConstants.CENTER);
        rightBBLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightBBLabel.setForeground(new Color(33, 33, 33));
        rightBB.add(rightBBLabel);
        for (int i = 24; i <= 28; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            rightBB.add(seat);
            if (i < 28) rightBB.add(Box.createVerticalStrut(2));
        }
        rightBalcony.add(rightBB);
        sidePanel.add(rightBalcony, BorderLayout.EAST);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(sidePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Stage
        JPanel stagePanel = new JPanel(new BorderLayout());
        stagePanel.setBackground(Color.WHITE);
        JLabel stageLabel = new JLabel("STAGE", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Arial", Font.BOLD, 22));
        stageLabel.setForeground(Color.WHITE);
        stageLabel.setBackground(new Color(33, 150, 243));
        stageLabel.setOpaque(true);
        stageLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        JPanel leftPadding = new JPanel();
        leftPadding.setPreferredSize(new Dimension(175, 30));
        leftPadding.setBackground(Color.WHITE);
        JPanel rightPadding = new JPanel();
        rightPadding.setPreferredSize(new Dimension(193, 30));
        rightPadding.setBackground(Color.WHITE);
        stagePanel.add(leftPadding, BorderLayout.WEST);
        stagePanel.add(stageLabel, BorderLayout.CENTER);
        stagePanel.add(rightPadding, BorderLayout.EAST);
        add(stagePanel, BorderLayout.SOUTH);

        // Set frame size and visibility
        pack();
        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createSpecialRow(JPanel panel, char rowLetter) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new GridLayout(2, 1, 0, 0));
        rowPanel.setBackground(Color.WHITE);

        JPanel upperRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        upperRow.setBackground(Color.WHITE);
        JLabel upperLabel = new JLabel("" + rowLetter);
        upperLabel.setFont(new Font("Arial", Font.BOLD, 12));
        upperLabel.setForeground(new Color(33, 33, 33));
        upperRow.add(upperLabel);
        for (int i = 4; i <= 16; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            upperRow.add(seat);
        }
        rowPanel.add(upperRow);

        JPanel lowerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        lowerRow.setBackground(Color.WHITE);
        lowerRow.add(Box.createHorizontalStrut(20));
        for (int i = 1; i <= 3; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            lowerRow.add(seat);
        }
        lowerRow.add(Box.createHorizontalStrut(355));
        int endSeat = 19;
        for (int i = 17; i <= endSeat; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            lowerRow.add(seat);
        }
        lowerRow.add(Box.createHorizontalStrut(25));
        rowPanel.add(lowerRow);
        panel.add(rowPanel);
    }

    private void createRowN(JPanel panel) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new GridLayout(2, 1, 0, 0));
        rowPanel.setBackground(Color.WHITE);

        JPanel upperRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        upperRow.setBackground(Color.WHITE);
        JLabel nLabel = new JLabel("N");
        nLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nLabel.setForeground(new Color(33, 33, 33));
        upperRow.add(nLabel);
        for (int i = 4; i <= 14; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            upperRow.add(seat);
        }
        rowPanel.add(upperRow);

        JPanel lowerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        lowerRow.setBackground(Color.WHITE);
        lowerRow.add(Box.createHorizontalStrut(20));
        for (int i = 1; i <= 3; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            lowerRow.add(seat);
        }
        lowerRow.add(Box.createHorizontalStrut(300));
        for (int i = 17; i <= 19; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            lowerRow.add(seat);
        }
        lowerRow.add(Box.createHorizontalStrut(25));
        rowPanel.add(lowerRow);
        panel.add(rowPanel);
    }

    private void createRowM(JPanel panel) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new GridLayout(2, 1, 0, 0));
        rowPanel.setBackground(Color.WHITE);

        JPanel upperRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        upperRow.setBackground(Color.WHITE);
        JLabel mLabel = new JLabel("M");
        mLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mLabel.setForeground(new Color(33, 33, 33));
        upperRow.add(mLabel);
        for (int i = 3; i <= 12; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            upperRow.add(seat);
        }
        rowPanel.add(upperRow);

        JPanel lowerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        lowerRow.setBackground(Color.WHITE);
        lowerRow.add(Box.createHorizontalStrut(50));
        for (int i = 1; i <= 2; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            lowerRow.add(seat);
        }
        lowerRow.add(Box.createHorizontalStrut(268));
        for (int i = 15; i <= 16; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            lowerRow.add(seat);
        }
        lowerRow.add(Box.createHorizontalStrut(66));
        rowPanel.add(lowerRow);
        panel.add(rowPanel);
    }

    private void createRowO(JPanel panel) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new GridLayout(2, 1, 0, 0));
        rowPanel.setBackground(Color.WHITE);

        JPanel upperRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        upperRow.setBackground(Color.WHITE);
        JLabel oLabel = new JLabel("O");
        oLabel.setFont(new Font("Arial", Font.BOLD, 12));
        oLabel.setForeground(new Color(33, 33, 33));
        upperRow.add(oLabel);
        for (int i = 1; i <= 16; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            upperRow.add(seat);
        }
        rowPanel.add(upperRow);

        JPanel lowerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        lowerRow.setBackground(Color.WHITE);
        lowerRow.add(Box.createHorizontalStrut(465));
        for (int i = 17; i <= 20; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setPreferredSize(new Dimension(28, 22));
            seat.setMinimumSize(new Dimension(28, 22));
            seat.setMaximumSize(new Dimension(28, 22));
            seat.setMargin(new Insets(0, 0, 0, 0));
            seat.setFont(new Font("Arial", Font.PLAIN, 10));
            seat.setBackground(new Color(200, 230, 201));
            seat.setForeground(new Color(33, 33, 33));
            seat.setFocusPainted(false);
            seat.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            lowerRow.add(seat);
        }
        lowerRow.add(Box.createHorizontalStrut(25));
        rowPanel.add(lowerRow);
        panel.add(rowPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainHall());
    }
}