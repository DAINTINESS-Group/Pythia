package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.clustering.ClusteringParameters;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.ClusteringParameterValidator;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ClusteringParameterValidatorTest {

    private ClusteringParameterValidator validator;
    private Map<String, JComponent> inputFields;
    private DatasetProfile originalDatasetProfile;

    /**
     * Sets up the test environment before each test case.
     * Initializes the validator, input fields, and a fake dataset for testing.
     *
     * @throws Exception if there is an issue setting up the test environment.
     */
    @Before
    public void setUp() throws Exception {
        validator = new ClusteringParameterValidator();
        inputFields = new HashMap<>();
        originalDatasetProfile = AppController.getInstance().getDatasetProfile();
        setDatasetProfile(new DatasetProfile());
        setDataset(createFakeDataset());
    }

    /**
     * Helper method to set the dataset in the AppController for testing.
     *
     * @param dataset the dataset to set.
     * @throws Exception if there is an issue accessing or setting the dataset field.
     */
    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    /**
     * Creates a fake dataset for testing purposes.
     *
     * @return a Dataset<Row> containing fake data.
     */
    private Dataset<Row> createFakeDataset() {
        SparkSession spark = SparkSession.builder().master("local").appName("FakeDataset").getOrCreate();
        List<Row> data = Arrays.asList(
                RowFactory.create("feature1", 1.0),
                RowFactory.create("feature2", 2.0),
                RowFactory.create("feature3", 3.0)
        );
        StructType schema = new StructType(new StructField[]{
                new StructField("feature1", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature2", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature3", DataTypes.DoubleType, false, Metadata.empty())
        });
        return spark.createDataFrame(data, schema);
    }

    /**
     * Helper method to set the dataset profile in the AppController for testing.
     *
     * @param datasetProfile the dataset profile to set.
     * @throws Exception if there is an issue accessing or setting the dataset profile field.
     */
    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Cleans up the test environment after each test case.
     * Restores the original dataset profile in the AppController.
     *
     * @throws Exception if there is an issue restoring the dataset profile.
     */
    @After
    public void tearDown() throws Exception {
        setDatasetProfile(originalDatasetProfile);
    }

    /**
     * Tests the validation and creation of clustering parameters with valid inputs.
     * Ensures that the validator correctly creates a ClusteringParameters object.
     */
    @Test
    public void testValidateAndCreate_ValidInputs() {
        JComboBox<String> clusteringTypeComboBox = new JComboBox<>(new String[]{"KMEANS", "DIVISIVE", "GRAPH_BASED"});
        clusteringTypeComboBox.setSelectedItem("KMEANS");
        JTextField numberOfClustersField = new JTextField("3");
        JTextField selectedFeaturesField = new JTextField("feature1,feature2,feature3");
        inputFields.put("Clustering Type", clusteringTypeComboBox);
        inputFields.put("Number of Clusters", numberOfClustersField);
        inputFields.put("Selected Features (comma-separated)", selectedFeaturesField);
        ClusteringParameters parameters = validator.validateAndCreate(inputFields);
        assertNotNull(parameters);
        assertEquals(ClusteringType.KMEANS, parameters.getType());
        assertEquals(3, parameters.getNumOfClusters());
        assertEquals(Arrays.asList("feature1", "feature2", "feature3"), parameters.getSelectedFeatures());
    }

    /**
     * Tests the validation of clustering parameters with an invalid number of clusters.
     * Ensures that the validator returns null and handles the error appropriately.
     */
    @Test
    public void testValidateAndCreate_InvalidNumberOfClusters() throws InterruptedException, InvocationTargetException {
        JComboBox<String> clusteringTypeComboBox = new JComboBox<>(new String[]{"KMEANS", "DIVISIVE", "GRAPH_BASED"});
        clusteringTypeComboBox.setSelectedItem("DIVISIVE");
        JTextField numberOfClustersField = new JTextField("-1"); // Invalid number of clusters
        JTextField selectedFeaturesField = new JTextField("feature1,feature2");
        inputFields.put("Clustering Type", clusteringTypeComboBox);
        inputFields.put("Number of Clusters", numberOfClustersField);
        inputFields.put("Selected Features (comma-separated)", selectedFeaturesField);
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            ClusteringParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Tests the validation of clustering parameters with an empty features string.
     * Ensures that the validator returns null and handles the error appropriately.
     */
    @Test
    public void testValidateAndCreate_EmptyFeatures() throws InterruptedException, InvocationTargetException {
        JComboBox<String> clusteringTypeComboBox = new JComboBox<>(new String[]{"KMEANS", "DIVISIVE", "GRAPH_BASED"});
        clusteringTypeComboBox.setSelectedItem("GRAPH_BASED");
        JTextField numberOfClustersField = new JTextField("2");
        JTextField selectedFeaturesField = new JTextField(""); // Empty features string
        inputFields.put("Clustering Type", clusteringTypeComboBox);
        inputFields.put("Number of Clusters", numberOfClustersField);
        inputFields.put("Selected Features (comma-separated)", selectedFeaturesField);
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            ClusteringParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Tests the validation of clustering parameters with an invalid clustering type.
     * Ensures that the validator returns null and handles the error appropriately.
     */
    @Test
    public void testValidateAndCreate_InvalidClusteringType() throws InvocationTargetException, InterruptedException {
        JComboBox<String> clusteringTypeComboBox = new JComboBox<>();
        clusteringTypeComboBox.setEditable(true); // Allow any input
        clusteringTypeComboBox.setSelectedItem("INVALID_TYPE"); // Invalid type
        JTextField numberOfClustersField = new JTextField("4");
        JTextField selectedFeaturesField = new JTextField("feature1,feature2");
        inputFields.put("Clustering Type", clusteringTypeComboBox);
        inputFields.put("Number of Clusters", numberOfClustersField);
        inputFields.put("Selected Features (comma-separated)", selectedFeaturesField);
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            ClusteringParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Tests the validation of clustering parameters with a non-numeric number of clusters.
     * Ensures that the validator returns null and handles the error appropriately.
     */
    @Test
    public void testValidateAndCreate_NonNumericNumberOfClusters() throws InterruptedException, InvocationTargetException {
        JComboBox<String> clusteringTypeComboBox = new JComboBox<>(new String[]{"KMEANS", "DIVISIVE", "GRAPH_BASED"});
        clusteringTypeComboBox.setSelectedItem("KMEANS");
        JTextField numberOfClustersField = new JTextField("abc"); // Non-numeric input
        JTextField selectedFeaturesField = new JTextField("feature1,feature2");
        inputFields.put("Clustering Type", clusteringTypeComboBox);
        inputFields.put("Number of Clusters", numberOfClustersField);
        inputFields.put("Selected Features (comma-separated)", selectedFeaturesField);
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            ClusteringParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Tests the validation of clustering parameters with invalid feature names.
     * Ensures that the validator returns null and handles the error appropriately.
     */
    @Test
    public void testValidateAndCreate_InvalidFeatureNames() throws InterruptedException, InvocationTargetException {
        JComboBox<String> clusteringTypeComboBox = new JComboBox<>(new String[]{"KMEANS", "DIVISIVE", "GRAPH_BASED"});
        clusteringTypeComboBox.setSelectedItem("KMEANS");
        JTextField numberOfClustersField = new JTextField("3");
        JTextField selectedFeaturesField = new JTextField("invalid_feature,feature2"); // Invalid feature name
        inputFields.put("Clustering Type", clusteringTypeComboBox);
        inputFields.put("Number of Clusters", numberOfClustersField);
        inputFields.put("Selected Features (comma-separated)", selectedFeaturesField);
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            ClusteringParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Tests the validation of clustering parameters when no dataset profile is available.
     * Ensures that the validator returns null and handles the error appropriately.
     */
    @Test
    public void testValidateAndCreate_NoDatasetProfile() throws Exception {
        setDatasetProfile(null);
        JComboBox<String> clusteringTypeComboBox = new JComboBox<>(new String[]{"KMEANS", "DIVISIVE", "GRAPH_BASED"});
        clusteringTypeComboBox.setSelectedItem("KMEANS");
        JTextField numberOfClustersField = new JTextField("3");
        JTextField selectedFeaturesField = new JTextField("feature1,feature2,feature3");
        inputFields.put("Clustering Type", clusteringTypeComboBox);
        inputFields.put("Number of Clusters", numberOfClustersField);
        inputFields.put("Selected Features (comma-separated)", selectedFeaturesField);
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            ClusteringParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }
}