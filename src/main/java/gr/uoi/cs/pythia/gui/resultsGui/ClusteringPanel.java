package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.clustering.Cluster;
import gr.uoi.cs.pythia.model.ClusteringProfile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClusteringPanel extends AnalysisPanel {


    ClusteringProfile clusteringProfile;
    public ClusteringPanel() {
        super();
    }

    @Override
    public void createPanelContent() {
        setLayout(new BorderLayout());
        clusteringProfile = AppController.getInstance().getDatasetProfile().getClusteringProfile();
        if(clusteringProfile == null || clusteringProfile.getClusters().isEmpty()){
          add(new JLabel("No clustering profile found.", SwingConstants.CENTER), BorderLayout.CENTER);
          return;

        }
        // Add profile information panel
        add(createProfilePanel(), BorderLayout.NORTH);

        // Add charts panel
        add(createChartsPanel(), BorderLayout.CENTER);

        // Add clusters table
        add(createClustersTable(), BorderLayout.SOUTH);
    }

    // Method to create the profile information panel
    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new GridLayout(4, 1));
        profilePanel.setBorder(BorderFactory.createTitledBorder("Clustering Profile"));

        profilePanel.add(new JLabel("Type: " + clusteringProfile.getType()));
        profilePanel.add(new JLabel("Error: " + clusteringProfile.getError()));
        profilePanel.add(new JLabel("Average Silhouette Score: " + clusteringProfile.getAvgSilhouetteScore()));
        profilePanel.add(new JLabel("Number of Clusters: " + clusteringProfile.getClusters().size()));

        return profilePanel;
    }

    // Method to create the charts panel
    private JPanel createChartsPanel() {
        JPanel chartPanel = new JPanel(new GridLayout(1, 2));

        // Create and customize the silhouette score chart
        JFreeChart silhouetteChart = createHistogram(
                "Average Silhouette Score",
                "Score",
                new double[]{clusteringProfile.getAvgSilhouetteScore()},
                "Silhouette Score"
        );
        ChartPanel silhouetteChartPanel = new ChartPanel(silhouetteChart);
        silhouetteChartPanel.setPreferredSize(new Dimension(500, 400));
        chartPanel.add(silhouetteChartPanel);

        // Create and customize the error chart
        JFreeChart errorChart = createHistogram(
                "Error",
                "Error Value",
                new double[]{clusteringProfile.getError()},
                "Error"
        );
        ChartPanel errorChartPanel = new ChartPanel(errorChart);
        errorChartPanel.setPreferredSize(new Dimension(500, 400));
        chartPanel.add(errorChartPanel);

        return chartPanel;
    }

    // Method to create a histogram chart
    private JFreeChart createHistogram(String title, String xAxisLabel, double[] data, String seriesName) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        dataset.addSeries(seriesName, data, 1); // 1 bin

        JFreeChart chart = ChartFactory.createHistogram(
                title,
                xAxisLabel,
                "Frequency",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        customizeChart(chart);
        return chart;
    }

    // Method to customize the chart appearance
    private void customizeChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();

        renderer.setMargin(0.1); // 10% margin between bars
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setDrawBarOutline(true);
        renderer.setSeriesOutlinePaint(0, Color.BLACK);
        renderer.setSeriesPaint(0, new Color(11, 14, 14));
    }

    // Method to create the clusters table
    private JScrollPane createClustersTable() {
        String[] columnNames = {"ID", "Num of Points", "Mean,EXIST_index", "Standard Deviations,EXIST_index", "Median,EXIST_index", "Min,EXIST_index", "Max,EXIST_index", "Error"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Cluster cluster : clusteringProfile.getClusters()) {
            Object[] rowData = {
                    cluster.getId(),
                    cluster.getNumOfPoints(),
                    cluster.getMean().toString(),
                    cluster.getStandardDeviations().toString(),
                    cluster.getMedian().toString(),
                    cluster.getMin().toString(),
                    cluster.getMax().toString(),
                    cluster.getError()
            };
            model.addRow(rowData);
        }

        JTable clusterTable = new JTable(model);
        clusterTable.setFillsViewportHeight(true);

        return new JScrollPane(clusterTable);
    }
}