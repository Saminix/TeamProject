package Swing;

import javax.swing.*;
import java.awt.*;

/**
 * DashboardUI class represents the user interface for the dashboard.
 * It displays a set of cards and a table with user data.
 */

public class DashboardUI extends JPanel {

    private Card card1, card2, card3;
    private Table table;
    private JScrollPane jScrollPane1;
    private RPanel roundPanel1;

    public DashboardUI() {
        initComponents();
        init();
    }

    private void init() {
        table.fixTable(jScrollPane1);
        table.addRow(new Object[]{"1", "Mike Bhand", "mikebhand@gmail.com", "Admin", "25 Apr,2018"});
        table.addRow(new Object[]{"2", "Andrew Strauss", "andrewstrauss@gmail.com", "Editor", "25 Apr,2018"});
        table.addRow(new Object[]{"3", "Ross Kopelman", "rosskopelman@gmail.com", "Subscriber", "25 Apr,2018"});
        table.addRow(new Object[]{"4", "Mike Hussy", "mikehussy@gmail.com", "Admin", "25 Apr,2018"});
        table.addRow(new Object[]{"5", "Kevin Pietersen", "kevinpietersen@gmail.com", "Admin", "25 Apr,2018"});
        table.addRow(new Object[]{"6", "Andrew Strauss", "andrewstrauss@gmail.com", "Editor", "25 Apr,2018"});
        table.addRow(new Object[]{"7", "Ross Kopelman", "rosskopelman@gmail.com", "Subscriber", "25 Apr,2018"});
        table.addRow(new Object[]{"8", "Mike Hussy", "mikehussy@gmail.com", "Admin", "25 Apr,2018"});
        table.addRow(new Object[]{"9", "Kevin Pietersen", "kevinpietersen@gmail.com", "Admin", "25 Apr,2018"});
        table.addRow(new Object[]{"10", "Kevin Pietersen", "kevinpietersen@gmail.com", "Admin", "25 Apr,2018"});
        table.addRow(new Object[]{"11", "Andrew Strauss", "andrewstrauss@gmail.com", "Editor", "25 Apr,2018"});
        table.addRow(new Object[]{"12", "Ross Kopelman", "rosskopelman@gmail.com", "Subscriber", "25 Apr,2018"});
        table.addRow(new Object[]{"13", "Mike Hussy", "mikehussy@gmail.com", "Admin", "25 Apr,2018"});
        table.addRow(new Object[]{"14", "Kevin Pietersen", "kevinpietersen@gmail.com", "Admin", "25 Apr,2018"});

        // Initialize card data
        /*
        card1.setData(new Icon(null, null, null, "$ 500.00", "Report Income Monthly"));
        card2.setData(new Icon(null, null, null, "$ 800.00", "Report Expense Monthly"));
        card3.setData(new Icon(null, null, null, "$ 300.00", "Report Profit Monthly"));

         */
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel cardPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        card1 = new Card();
        card2 = new Card();
        card3 = new Card();

        cardPanel.add(card1);
        cardPanel.add(card2);
        cardPanel.add(card3);


        roundPanel1 = new RPanel();
        roundPanel1.setLayout(new BorderLayout());
        roundPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table = new Table();
        jScrollPane1 = new JScrollPane(table);
        roundPanel1.add(jScrollPane1, BorderLayout.CENTER);

        add(cardPanel, BorderLayout.NORTH);
        add(roundPanel1, BorderLayout.CENTER);
    }
}