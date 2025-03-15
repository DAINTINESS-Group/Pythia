package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.regression.RegressionType;
import gr.uoi.cs.pythia.regression.RegressionParameters;
import gr.uoi.cs.pythia.regression.RegressionRequest;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class RegressionGUI extends AnalysisParametersGUI<RegressionRequest>{

    /**
	 * 
	 */

	private final JTextField dependentVariableField;
    private final JTextField independentVariablesField;
    private final JTextField precisionField;
    private final JComboBox<RegressionType> regressionTypeComboBox; // Make it a field
    //private final JTextArea regressionsArea; // For displaying added regressions
    private final RegressionRequest regressionRequest;

    public RegressionGUI(AnalysisTabsPanel tabsGUI,JPanel cardPanel,CardLayout cardLayout){
        super("Regression", tabsGUI, new RegressionParameterValidator(), AppController.getInstance()::declareRegressionRequest,cardPanel,cardLayout);

        dependentVariableField = new JTextField();
        addInputField("Dependent Variable", dependentVariableField);

        independentVariablesField = new JTextField();
        addInputField("Independent Variables (comma-separated)", independentVariablesField);

        regressionTypeComboBox = new JComboBox<>(RegressionType.values()); // Initialize here
        addInputField("Regression Type", regressionTypeComboBox);

        precisionField = new JTextField();
        addInputField("Precision", precisionField);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Added Regressions"));
        add(scrollPane, BorderLayout.CENTER); // Add to the center of the panel

        JButton addRegressionButton = new JButton("Add Regression");
        addRegressionButton.addActionListener(e->addRegression());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel for the button
        buttonPanel.add(addRegressionButton);
        add(buttonPanel, BorderLayout.EAST); // Add button panel to the south

        regressionRequest = new RegressionRequest();
        updateResultArea(); // Initial update
    }

    public void addRegression(){
        String dependentVar = dependentVariableField.getText();
        String independentVarsStr = independentVariablesField.getText();
        RegressionType type = (RegressionType) regressionTypeComboBox.getSelectedItem();

        if(AppController.getInstance().getDatasetProfile()==null){
            JOptionPane.showMessageDialog(null, "You must register dataset first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate inputs
        if(dependentVar.isEmpty() || independentVarsStr.isEmpty() && type!=RegressionType.AUTOMATED ){
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> independentVars = Arrays.asList(independentVarsStr.split(","));

        Dataset<Row> dataset = AppController.getInstance().getDataset();
        String[] datasetColumns = dataset.columns();

        for(String column : independentVars){

            if(!Arrays.asList(datasetColumns).contains(column) && type!=RegressionType.AUTOMATED){
                JOptionPane.showMessageDialog(null, "column '"+column+"' does not exist in the dataset.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if(!Arrays.asList(datasetColumns).contains(dependentVar)){
            JOptionPane.showMessageDialog(null, "column '"+dependentVar+"' does not exist in the dataset.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        Double precision = null;
        String precisionStr = precisionField.getText();
        if(type!=RegressionType.LINEAR && type!=RegressionType.MULTIPLE_LINEAR){ // Only check precision for these types
            if(precisionStr.isEmpty()){
                JOptionPane.showMessageDialog(this, "Precision is required for this regression type.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                precision = Double.parseDouble(precisionStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid precision value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            // For LINEAR and MULTIPLE_LINEAR, precision is optional. If empty, use a default value.
            if(!precisionStr.isEmpty()){
                try {
                    precision = Double.parseDouble(precisionStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid precision value.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }


        RegressionParameters params = new RegressionParameters(independentVars, dependentVar, type, precision);
        regressionRequest.addRegression(params);  // Add the regression

        // Clear input fields
        dependentVariableField.setText("");
        independentVariablesField.setText("");
        //precisionField.setText("0.05"); // Reset precision field

        updateResultArea();
    }

    @Override
    protected RegressionRequest getParameters(){
        if(regressionRequest.getRegressionParameters().isEmpty()){
            JOptionPane.showMessageDialog(this, "Create at least one Regression Parameter.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return regressionRequest;
    }

    @Override
    protected void updateResultArea(){
        StringBuilder sb = new StringBuilder("Added Regressions:\n");

        if(regressionRequest.getRegressionParameters().isEmpty()){
            sb.append("No regressions added yet.");
        } else {
            for(RegressionParameters params : regressionRequest.getRegressionParameters()){
                sb.append("Dependent Variable: ").append(params.getDependentVariable()).append("\n");
                sb.append("Independent Variables: ").append(params.getIndependentVariables()).append("\n");
                sb.append("Regression Type: ").append(params.getType()).append("\n");
                sb.append("Precision: ").append(params.getPrecision()).append("\n");
                sb.append("----------------------------\n");
            }
        }

        resultArea.setText(sb.toString());
    }
}