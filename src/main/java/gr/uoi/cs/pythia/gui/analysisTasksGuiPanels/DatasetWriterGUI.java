package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.writer.DatasetWriterConstants;
import gr.uoi.cs.pythia.writer.DatasetWriterParameters;

import javax.swing.*;
import java.awt.*;

public class DatasetWriterGUI extends AnalysisParametersGUI<DatasetWriterParameters> {



    /**
	 * 
	 */

	public DatasetWriterGUI(JPanel cardPanel, CardLayout cardLayout) {
        super("Dataset Writer",null, new DatasetWriterParameterValidator(), AppController.getInstance()::writeDataset,cardPanel,cardLayout); // No tabsGUI needed


        addInputField("Dataset Alias", new JTextField(20));
        addInputField("Writer Type", new JComboBox<>(new String[]{DatasetWriterConstants.HADOOP, DatasetWriterConstants.NAIVE}));
        JTextField pathField = new JTextField(20);
        addInputField("Output Path", pathField);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseForPath(pathField));
        ((JPanel) getComponent(0)).add(browseButton); // Add to input panel
    }

    private void browseForPath(JTextField pathField){
        JFileChooser fileChooser=new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            updateResultArea();
        }
    }

    @Override
    public void updateResultArea() {
        // Retrieve components from the inputFields map
        Object datasetAliasComponent = inputFields.get("Dataset Alias");
        Object writerTypeComponent = inputFields.get("Writer Type");
        Object outputPathComponent = inputFields.get("Output Path");

        // Initialize a StringBuilder to construct the result string
        StringBuilder sb = new StringBuilder("Current Dataset Writer Parameters:\n");

        // Safely retrieve and append the dataset alias
        if (datasetAliasComponent instanceof JTextField) {
            JTextField datasetAliasField = (JTextField) datasetAliasComponent;
            String datasetAlias = datasetAliasField.getText();
            sb.append("Dataset Alias: ").append(datasetAlias).append("\n");
        }

        // Safely retrieve and append the writer type
        if (writerTypeComponent instanceof JComboBox<?>) {
            JComboBox<?> writerTypeComboBox = (JComboBox<?>) writerTypeComponent;
            String writerType = writerTypeComboBox.getSelectedItem() != null ?
                    writerTypeComboBox.getSelectedItem().toString() : "";
            sb.append("Writer Type: ").append(writerType).append("\n");
        }

        // Safely retrieve and append the output path
        if (outputPathComponent instanceof JTextField) {
            JTextField outputPathField = (JTextField) outputPathComponent;
            String outputPath = outputPathField.getText();
            sb.append("Output Path: ").append(outputPath).append("\n");
        }

        // Set the constructed string to the result area
        resultArea.setText(sb.toString());
    }
}