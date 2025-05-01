package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;



import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.report.ReportParameters;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ReportGeneratorGUI extends AnalysisParametersGUI<ReportParameters> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3577433927087270739L;

	public ReportGeneratorGUI(JPanel cardPanel, CardLayout cardLayout) {
        super("Report Generator", null, new ReportParameterValidator(), AppController.getInstance()::generateReport,cardPanel,cardLayout);   // No tabsGUI needed

        JComboBox<String> reportTypeComboBox=new JComboBox<>(new String[]{"TXT", "JSON", "MD"});
        addInputField("Report Type", reportTypeComboBox);

        JTextField savePathField = new JTextField();
        savePathField.setEditable(false);
        addInputField("Save Path", savePathField);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> choosePath(savePathField));
        ((JPanel) getComponent(0)).add(browseButton); // Add to input panel

    }

    public void choosePath(JTextField pathField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            pathField.setText(selectedFile.getAbsolutePath());
            updateResultArea();
        }
    }

    @Override
    public void updateResultArea() {
        // Retrieve components from the inputFields map
        Object reportTypeComponent = inputFields.get("Report Type");
        Object savePathComponent = inputFields.get("Save Path");

        // Initialize a StringBuilder to construct the result string
        StringBuilder sb = new StringBuilder("Current Report Parameters:\n");

        // Safely retrieve and append the report type
        if (reportTypeComponent instanceof JComboBox<?>) {
            JComboBox<?> reportTypeComboBox = (JComboBox<?>) reportTypeComponent;
            String reportType = reportTypeComboBox.getSelectedItem() != null ?
                    reportTypeComboBox.getSelectedItem().toString() : "";
            sb.append("Report Type: ").append(reportType).append("\n");
        }

        // Safely retrieve and append the save path
        if (savePathComponent instanceof JTextField) {
            JTextField savePathField = (JTextField) savePathComponent;
            String savePath = savePathField.getText();
            sb.append("Save Path: ").append(savePath).append("\n");
        }

        // Set the constructed string to the result area
        resultArea.setText(sb.toString());
    }
}
