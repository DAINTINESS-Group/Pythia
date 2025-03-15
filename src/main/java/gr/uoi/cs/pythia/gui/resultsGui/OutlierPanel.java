package gr.uoi.cs.pythia.gui.resultsGui;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.OutlierProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import org.apache.spark.sql.Row;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class OutlierPanel extends AnalysisPanel {


	public OutlierPanel( ) {
        super();
    }

    @Override
    public void createPanelContent() {
        List<Column> columnList = AppController.getInstance().getDatasetProfile().getColumns();

        if (columnList == null || columnList.isEmpty()) {
            add(new JLabel("No columns found.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        JPanel chartsContainer = new JPanel(new GridLayout(0, 1, 10, 10)); // Vertical layout with gaps

        for (Column column : columnList) {
            JPanel chartPanelWrapper = createOutlierChartPanel(column); // Create panel for each chart
            chartsContainer.add(chartPanelWrapper);
        }

        JScrollPane scrollPane = new JScrollPane(chartsContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        add(scrollPane, BorderLayout.CENTER); // Add scroll pane to the center
        this.revalidate();
        this.repaint();
    }

    private JPanel createOutlierChartPanel(Column column) {
        List<Row> listValues = AppController.getInstance().getDataset().select(column.getName()).collectAsList();

        if (column.getOutlierProfile() == null || listValues.isEmpty()) {
            JPanel noDataPanel = new JPanel();
            noDataPanel.add(new JLabel("No data to visualize for " + column.getName()));
            noDataPanel.setBorder(BorderFactory.createTitledBorder(column.getName() + " Outliers"));
            return noDataPanel;
        }

        JFreeChart chart = createScatterPlot(column, listValues);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        chartPanel.setMouseWheelEnabled(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(column.getName() + " Outliers"));
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }


    private JFreeChart createScatterPlot(Column column, List<Row> list) {
        OutlierProfile outlierProfile = column.getOutlierProfile();
        XYSeries realDataSeries = new XYSeries("Real Data - " + column.getName());
        XYSeries outlierSeries = new XYSeries("Outliers - " + column.getName());

        for (int i = 0; i < list.size(); i++) {
            Row row = list.get(i);
            if (row == null) {
                continue; // Skip null rows
            }
            String valueStr = row.getAs(column.getName()).toString();
            try {
                Double value = Double.parseDouble(valueStr);
                realDataSeries.add(i + 1, value);
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid number: " + valueStr);
            }
        }

        if (outlierProfile != null) {
            for (OutlierResult result : outlierProfile.getOutlierResultList()) {
                outlierSeries.add(result.getPosition(), result.getValue());
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(realDataSeries);
        dataset.addSeries(outlierSeries);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Data and Outlier Analysis for " + column.getName(),
                "Position", "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        styleScatterPlot(chart); // Style the chart
        return chart;
    }

    private void styleScatterPlot(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setSeriesPaint(0, Color.BLUE); // Real data in blue
        renderer.setSeriesPaint(1, Color.RED);  // Outliers in red
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6)); // Shape for real data
        renderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-5, -5, 10, 10)); // Larger shape for outliers
        plot.setRenderer(renderer);
    }
}