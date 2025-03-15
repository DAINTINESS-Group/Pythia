package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.histogram.Bin;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HistogramPanel extends AnalysisPanel { // Inherit from AnalysisPanel

	public HistogramPanel() {
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
            Histogram histogram = column.getHistogram();
            if (histogram != null) {
                JPanel chartPanelWrapper = createHistogramPanel(histogram, column.getName());
                chartsContainer.add(chartPanelWrapper);
            }
            Histogram quartile = column.getQuartilesHistogram();
            if(quartile!=null){
                JPanel chartPanelWrapper = createHistogramPanel(quartile, column.getName());
                chartsContainer.add(chartPanelWrapper);
            }
        }

        JScrollPane scrollPane = new JScrollPane(chartsContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center of the panel
        this.revalidate();
        this.repaint();
    }

    private JPanel createHistogramPanel(Histogram histogram, String columnName) {
        JFreeChart chart = createHistogramChart(histogram);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        chartPanel.setMouseWheelEnabled(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(columnName)); // Use provided column name
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }


    private JFreeChart createHistogramChart(Histogram histogram) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        List<Bin> binList = histogram.getBins();

        double[] values = new double[binList.size()];
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (int i = 0; i < binList.size(); i++) {
            Bin bin = binList.get(i);
            values[i] = bin.getCount();
            minValue = Math.min(minValue, bin.getLowerBound());
            maxValue = Math.max(maxValue, bin.getUpperBound());
        }

        dataset.addSeries(histogram.getColumnName(), values, binList.size());

        JFreeChart chart = ChartFactory.createHistogram(
                histogram.getColumnName(),
                "Value",
                "Frequency",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Styling (extracted to a separate method for better organization)
        styleHistogramChart(chart, minValue, maxValue);

        return chart;
    }

    private void styleHistogramChart(JFreeChart chart, double minValue, double maxValue) {
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189));
        renderer.setMargin(0.05);
        plot.setRenderer(renderer);

        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);
        domainAxis.setRange(minValue - 1, maxValue + 1);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(true);

        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));
    }
}