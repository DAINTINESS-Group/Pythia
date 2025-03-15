package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.clustering.Cluster;
import gr.uoi.cs.pythia.model.ClusteringProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
public class ClusteringPanelTest {

    private ClusteringPanel clusteringPanel;

    /**
     * Setup method before each test.
     * Initializes the ClusteringPanel and sets up fake dataset and clustering profile
     * for testing purposes.
     */
    @Before
    public void setUp() throws Exception {
        clusteringPanel = new ClusteringPanel();
        // Initializing the AppController with fake dataset and profile
        setDataset(createFakeDataset());
        setDatasetProfile(createFakeClusteringProfile());
    }

    /**
     * Tear down method after each test.
     * Resets the dataset and dataset profile to null after each test.
     */
    @After
    public void tearDown() throws Exception {
        setDataset(null); // Reset the dataset
        setDatasetProfile(null); // Reset the dataset profile
    }

    /**
     * Helper method to set the dataset in AppController using reflection.
     * This injects the dataset into the AppController for testing purposes.
     *
     * @param dataset the dataset to be set in AppController
     * @throws Exception if there is an issue accessing the field
     */
    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    /**
     * Helper method to set the dataset profile in AppController using reflection.
     * This injects the dataset profile into the AppController for testing purposes.
     *
     * @param datasetProfile the dataset profile to be set in AppController
     * @throws Exception if there is an issue accessing the field
     */
    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Creates a fake dataset for testing purposes.
     * This dataset contains three rows with three features each.
     *
     * @return a fake dataset
     */
    private Dataset<Row> createFakeDataset() {
        SparkSession spark = SparkSession.builder().master("local").appName("FakeDataset").getOrCreate();
        List<Row> data = Arrays.asList(
                RowFactory.create(1.0, 2.0, 3.0),
                RowFactory.create(4.0, 5.0, 6.0),
                RowFactory.create(7.0, 8.0, 9.0)
        );
        StructType schema = new StructType(new StructField[] {
                new StructField("feature1", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature2", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature3", DataTypes.DoubleType, false, Metadata.empty())
        });
        return spark.createDataFrame(data, schema);
    }

    /**
     * Creates a fake ClusteringProfile for testing purposes.
     * The profile contains two clusters with various parameters for testing.
     *
     * @return a fake clustering profile
     */
    private DatasetProfile createFakeClusteringProfile() {
        List<Cluster> clusters = new ArrayList<>();

        // Create fake clusters
        clusters.add(new Cluster(1, 10,
                Arrays.asList(1.0, 2.0, 3.0),
                Arrays.asList(0.1, 0.2, 0.3),
                Arrays.asList(1.0, 2.0, 3.0),
                Arrays.asList(0.5, 1.5, 2.5),
                Arrays.asList(1.5, 2.5, 3.5),
                0.05
        ));

        clusters.add(new Cluster(2, 20,
                Arrays.asList(2.0, 3.0, 4.0),
                Arrays.asList(0.2, 0.3, 0.4),
                Arrays.asList(2.0, 3.0, 4.0),
                Arrays.asList(1.0, 2.0, 3.0),
                Arrays.asList(3.0, 4.0, 5.0),
                0.08
        ));

        ClusteringProfile clusteringProfile = new ClusteringProfile();
        clusteringProfile.setError(0.1);
        clusteringProfile.setAvgSilhouetteScore(0.85);
        clusteringProfile.setClusters(clusters);
        clusteringProfile.setResult(createFakeDataset());

        DatasetProfile datasetProfile = new DatasetProfile();
        datasetProfile.setClusteringProfile(clusteringProfile);
        datasetProfile.setAlias("Test Dataset");
        return datasetProfile;
    }

    /**
     * Test to verify that the clustering panel displays the charts correctly.
     * It checks if the charts are rendered in the CENTER part of the panel.
     */
    @Test
    public void testChartsExist() {
        clusteringPanel.createPanelContent();

        // Get the JScrollPane from the ClusteringPanel
        JScrollPane scrollPane = (JScrollPane) clusteringPanel.getComponent(0);
        JViewport viewport = scrollPane.getViewport();
        JPanel mainPanel = (JPanel) viewport.getView();

        // The main panel should have 3 components: profile, charts, and clusters table
        assertEquals(3, mainPanel.getComponentCount());

        // The second component is the charts panel
        JPanel chartsPanel = (JPanel) mainPanel.getComponent(1);
        assertEquals(2, chartsPanel.getComponentCount()); // It should contain 2 charts
    }

    /**
     * Test to verify that the cluster table is displayed correctly.
     * It checks if the table in the SOUTH section contains data.
     */
    @Test
    public void testClusterTableExists() {
        clusteringPanel.createPanelContent();

        // Get the JScrollPane from the ClusteringPanel
        JScrollPane scrollPane = (JScrollPane) clusteringPanel.getComponent(0);
        JViewport viewport = scrollPane.getViewport();
        JPanel mainPanel = (JPanel) viewport.getView();

        // The main panel should have 3 components: profile, charts, and clusters table
        assertEquals(3, mainPanel.getComponentCount());

        // The third component is the clusters panel
        JPanel clustersPanel = (JPanel) mainPanel.getComponent(2);

        // The clusters panel contains multiple cluster panels (one for each cluster)
        // Each cluster panel contains a JScrollPane with the table inside
        for (Component component : clustersPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel clusterPanel = (JPanel) component;
                JScrollPane clustersScrollPane = (JScrollPane) clusterPanel.getComponent(0);
                JTable clusterTable = (JTable) clustersScrollPane.getViewport().getView();
                assertNotNull(clusterTable);

                // The table should have rows based on the number of columns in the dataset
                assertEquals(3, clusterTable.getRowCount()); // 3 columns in the fake dataset
            }
        }
    }

    /**
     * Test to verify that the correct message is displayed when no ClusteringProfile is found.
     * This test ensures that if the dataset profile doesn't have a clustering profile,
     * an appropriate message is shown.
     */
    @Test
    public void testNoClusteringProfileMessage() throws Exception {
        // Set a dataset profile with no clustering profile
        DatasetProfile emptyProfile = new DatasetProfile();
        emptyProfile.setClusteringProfile(null);
        setDatasetProfile(emptyProfile);

        // Invoke and wait to ensure the panel content is updated
        SwingUtilities.invokeAndWait(() -> {
            clusteringPanel.createPanelContent();
            Component[] components = clusteringPanel.getComponents();
            assertEquals(1, components.length); // Only one component (message label)

            // Verify the message text
            JLabel label = (JLabel) components[0];
            assertEquals("No clustering profile found.", label.getText());
        });
    }
}