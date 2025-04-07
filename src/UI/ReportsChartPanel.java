package UI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


/**
 * A support class panel for displaying the data in chart visual display form
 * or pie chart and a var chart.
 *  Utilises JFreeChart with the .jar library
 */

public class ReportsChartPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JPanel chartContainer;
    private JComboBox<String> chartTypeCombo;
    public JFreeChart currentChart;

    // Implement the reports chanel
    public ReportsChartPanel(DefaultTableModel model) {
        this.tableModel = model;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel controls = createControlPanel();
        add(controls, BorderLayout.NORTH);
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        add(chartContainer, BorderLayout.CENTER);
    }

    /**
     * Creates chart  selection and the export
     */
    private JPanel createControlPanel() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controls.setBackground(Color.WHITE);
        controls.setBorder(BorderFactory.createTitledBorder("Chart Controls"));
        chartTypeCombo = new JComboBox<>(new String[]{"Bar Chart", "Pie Chart"});
        // new fonts
        chartTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chartTypeCombo.addActionListener(e -> updateChart());
        JButton exportButton = createStyledButton("Export PNG", new Color(255, 87, 34));
        controls.add(new JLabel("Chart Type:"));
        controls.add(chartTypeCombo);
        controls.add(exportButton);
        return controls;
    }

    /**
     * a styled button with hover effects.
     *
     * @param text  the text
     * @param color button colour
     * @return the button
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });return button;
    }

    /**
     * chart can be changed by bar or pie.
     */
    public void updateChart() {
        if (tableModel.getRowCount() == 0) {
            clearChart();
            return;
        }
        String chartType = (String) chartTypeCombo.getSelectedItem();
        if ("Bar Chart".equals(chartType)) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String date = tableModel.getValueAt(i, 0).toString();
                double revenue = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
                dataset.addValue(revenue, "Revenue", date);
            }
            currentChart = ChartFactory.createBarChart(
                    "Revenue by Date", "Date", "Revenue (£)", dataset, PlotOrientation.VERTICAL, true, true, false
            );
        } else if ("Pie Chart".equals(chartType)) {
            DefaultPieDataset dataset = new DefaultPieDataset();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String date = tableModel.getValueAt(i, 0).toString();
                double revenue = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
                dataset.setValue(date, revenue);
            }
            currentChart = ChartFactory.createPieChart("Revenue Distribution by Date", dataset, true, true, false
            );
        }
        if (currentChart != null) {
            currentChart.setBackgroundPaint(Color.WHITE);
            currentChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
            chartContainer.removeAll();
            ChartPanel chartPanel = new ChartPanel(currentChart);
            chartPanel.setPreferredSize(new Dimension(600, 400));
            chartContainer.add(chartPanel, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();
        }
    }
    // change chart based on data changes on filter
    public void clearChart() {
        chartContainer.removeAll();
        chartContainer.revalidate();
        chartContainer.repaint();
        currentChart = null;
    }
}

