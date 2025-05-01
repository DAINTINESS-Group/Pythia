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

    /**
	 * 
	 */
	private static final long serialVersionUID = 2042428924134747569L;
	ClusteringProfile clusteringProfile;

    public ClusteringPanel() {
        super();
    }

    @Override
    public void createPanelContent() {
        clusteringProfile = AppController.getInstance().getDatasetProfile().getClusteringProfile();
        if (clusteringProfile == null || clusteringProfile.getClusters().isEmpty()) {
            add(new JLabel("No clustering profile found.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        // Create a main panel to hold all components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Add profile information panel
        mainPanel.add(createProfilePanel());

        // Add charts panel
        mainPanel.add(createChartsPanel());

        // Add clusters table
        mainPanel.add(createClustersTable());

        // Add the main panel to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Add the scroll pane to the ClusteringPanel
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
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
    private JPanel createClustersTable() {
        // Create a panel to hold all cluster tables
        JPanel clustersPanel = new JPanel();
        clustersPanel.setLayout(new BoxLayout(clustersPanel, BoxLayout.Y_AXIS));

        // Iterate through each cluster and create a table for it
        for (Cluster cluster : clusteringProfile.getClusters()) {
            JPanel clusterPanel = createClusterPanel(cluster);
            clustersPanel.add(clusterPanel);
            clustersPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between clusters
        }

        return clustersPanel;
    }

    // Method to create a panel for a single cluster
    private JPanel createClusterPanel(Cluster cluster) {
        JPanel clusterPanel = new JPanel(new BorderLayout());
        clusterPanel.setBorder(BorderFactory.createTitledBorder("Cluster " + cluster.getId() + " (with " + cluster.getNumOfPoints() + " points)"));

        // Create a table for the cluster statistics
        String[] tableColumnNames = {"Column", "Mean", "Standard Deviation", "Median", "Min", "Max"};
        DefaultTableModel model = new DefaultTableModel(tableColumnNames, 0);
        String[] list = AppController.getInstance().getDatasetProfile().getClusteringProfile().getResult().columns();

        // Add rows for each column in the cluster
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals("cluster")) continue;
            String columnName = list[i];
            Object[] rowData = {
                    columnName,
                    cluster.getMean().get(i), // Get mean for this column
                    cluster.getStandardDeviations().get(i), // Get standard deviation for this column
                    cluster.getMedian().get(i), // Get median for this column
                    cluster.getMin().get(i), // Get min for this column
                    cluster.getMax().get(i)  // Get max for this column
            };
            model.addRow(rowData);
        }

        JTable clusterTable = new JTable(model);
        clusterTable.setFillsViewportHeight(true);

        // Add the table to the cluster panel
        clusterPanel.add(new JScrollPane(clusterTable), BorderLayout.CENTER);

        return clusterPanel;
    }
}