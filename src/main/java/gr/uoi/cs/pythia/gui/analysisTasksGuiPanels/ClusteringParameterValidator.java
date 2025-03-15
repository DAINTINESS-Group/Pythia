package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.clustering.ClusteringParameters;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusteringParameterValidator implements ParameterValidator<ClusteringParameters> {

    @Override
    public ClusteringParameters validateAndCreate(Map<String, JComponent> inputFields) {
        String type = (String) ((JComboBox<?>) inputFields.get("Clustering Type")).getSelectedItem();
        String numClustersStr = ((JTextField) inputFields.get("Number of Clusters")).getText();
        String featuresStr = ((JTextField) inputFields.get("Selected Features (comma-separated)")).getText();

        if(AppController.getInstance().getDatasetProfile() == null){
            JOptionPane.showMessageDialog(null, ("You must register dataset first"), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }


        try {
            int numClusters = Integer.parseInt(numClustersStr);
            if (numClusters <= 0) {
                JOptionPane.showMessageDialog(null, "Number of clusters must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            List<String> features = Arrays.asList(featuresStr.split(","));
            if (featuresStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select at least one feature.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            // Validate that all selected features (column names) exist in the dataset
            Dataset<Row> dataset = AppController.getInstance().getDataset();
            String[] datasetColumns = dataset.columns();
            for(String feature : featuresStr.split(",")){
                if(!Arrays.asList(datasetColumns).contains(feature)){
                    JOptionPane.showMessageDialog(null, ("Selected feature '"+feature+"' does not exist in the dataset."), "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }

            Map<String, ClusteringType> map = new HashMap<>();
            map.put("KMEANS", ClusteringType.KMEANS);
            map.put("DIVISIVE", ClusteringType.DIVISIVE);
            map.put("GRAPH_BASED", ClusteringType.GRAPH_BASED);
            ClusteringType clusteringType;
            if(map.containsKey(type)) {
                clusteringType = map.get(type);
            }else{
                return null;
            }
            return new ClusteringParameters(clusteringType, numClusters, features);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid number of clusters.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}