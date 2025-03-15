package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.clustering.ClusteringParameters;

import javax.swing.*;
import java.awt.*;

public class ClusteringGUI extends AnalysisParametersGUI<ClusteringParameters> {
    /**
	 * 
	 */

	public ClusteringGUI(AnalysisTabsPanel tabsGUI,JPanel cardPanel,CardLayout cardLayout) {
        super("Clustering", tabsGUI, new ClusteringParameterValidator(), AppController.getInstance()::declareClusteringParameters, cardPanel, cardLayout);

        JComboBox<String> clusteringTypeComboBox=new JComboBox<>(new String[]{"KMEANS", "DIVISIVE", "GRAPH_BASED"});
        addInputField("Clustering Type", clusteringTypeComboBox);

        JTextField numberOfClustersField=new JTextField(10);
        addInputField("Number of Clusters", numberOfClustersField);

        JTextField selectedFeaturesField=new JTextField(20);
        addInputField("Selected Features (comma-separated)", selectedFeaturesField);
    }

    @Override
    public void updateResultArea() {
        // Retrieve components from the inputFields map
        Object clusteringTypeComponent = inputFields.get("Clustering Type");
        Object numberOfClustersComponent = inputFields.get("Number of Clusters");
        Object selectedFeaturesComponent = inputFields.get("Selected Features (comma-separated)");

        // Initialize a StringBuilder to construct the result string
        StringBuilder sb = new StringBuilder("Current Clustering Parameters:\n");

        // Safely retrieve and append the clustering type
        if (clusteringTypeComponent instanceof JComboBox<?>) {
            JComboBox<?> clusteringTypeComboBox = (JComboBox<?>) clusteringTypeComponent;
            String clusteringType = clusteringTypeComboBox.getSelectedItem() != null ? clusteringTypeComboBox.getSelectedItem().toString() : "";
            sb.append("Clustering Type: ").append(clusteringType).append("\n");
        }

        // Safely retrieve and append the number of clusters
        if (numberOfClustersComponent instanceof JTextField) {
            JTextField numberOfClustersField = (JTextField) numberOfClustersComponent;
            String numberOfClustersStr = numberOfClustersField.getText();
            sb.append("Number of Clusters: ").append(numberOfClustersStr).append("\n");
        }

        // Safely retrieve and append the selected features
        if (selectedFeaturesComponent instanceof JTextField) {
            JTextField selectedFeaturesField = (JTextField) selectedFeaturesComponent;
            String selectedFeaturesStr = selectedFeaturesField.getText();
            sb.append("Selected Features (comma-separated): ").append(selectedFeaturesStr).append("\n");
        }

        // Set the constructed string to the result area
        resultArea.setText(sb.toString());
    }
}