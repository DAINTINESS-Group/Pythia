package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LabelingSystemGUI extends AnalysisParametersGUI<RuleSet>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8219141034340646306L;
	private final List<Rule> rulesList = new ArrayList<>();
    private final JTextArea rulesArea;

    public LabelingSystemGUI(AnalysisTabsPanel tabsGUI,JPanel cardPanel, CardLayout cardLayout){
        super("Labeling Parameters", tabsGUI, new LabelingParameterValidator(), AppController.getInstance()::computeLabeledColumn,cardPanel,cardLayout);

        JTextField targetColumnField = new JTextField();
        addInputField("Target Column", targetColumnField);

        JComboBox<String> operatorComboBox = new JComboBox<>(new String[]{"<", "<=", ">", ">=", "="});
        addInputField("Operator", operatorComboBox);

        JTextField limitField = new JTextField();
        addInputField("Limit", limitField);

        JTextField labelField = new JTextField();
        addInputField("Label", labelField);

        JTextField newColumnNameField = new JTextField();
        addInputField("New Column Name", newColumnNameField);

        rulesArea = new JTextArea();
        rulesArea.setEditable(false);
        rulesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(rulesArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Rules List"));
        add(scrollPane, BorderLayout.CENTER);

        JButton addRuleButton = new JButton("Add Rule");
        addRuleButton.addActionListener(e->addRule());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addRuleButton);
        add(buttonPanel, BorderLayout.EAST);

        updateResultArea();
    }

    public void addRule(){
        String targetColumn = ((JTextField) inputFields.get("Target Column")).getText();
        String operator = (String) ((JComboBox<?>) inputFields.get("Operator")).getSelectedItem();
        String limitText = ((JTextField) inputFields.get("Limit")).getText();
        String label = ((JTextField) inputFields.get("Label")).getText();

        if(targetColumn.isEmpty() || limitText.isEmpty() || label.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validate that all selected features (column names) exist in the dataset
        Dataset<Row> dataset = AppController.getInstance().getDataset();
        if(dataset == null){
            JOptionPane.showMessageDialog(null, "You must register dataset first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] datasetColumns = dataset.columns();
        if(!Arrays.asList(datasetColumns).contains(targetColumn)){
            JOptionPane.showMessageDialog(null, ("Selected feature '"+targetColumn+"' does not exist in the dataset."), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        try {
            double limit = Double.parseDouble(limitText);
            Rule rule = new Rule(targetColumn, operator, limit, label);
            rulesList.add(rule);
            rulesArea.append(rule+"\n");

            ((JTextField) inputFields.get("Target Column")).setText("");
            ((JTextField) inputFields.get("Limit")).setText("");
            ((JTextField) inputFields.get("Label")).setText("");

            updateResultArea();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Limit must be a number!", "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

@Override
protected void updateResultArea() {
    // Retrieve components from the inputFields map
    Object targetColumnComponent = inputFields.get("Target Column");
    Object operatorComponent = inputFields.get("Operator");
    Object limitComponent = inputFields.get("Limit");
    Object labelComponent = inputFields.get("Label");
    Object newColumnNameComponent = inputFields.get("New Column Name");

    // Initialize a StringBuilder to construct the result string
    StringBuilder sb = new StringBuilder("Current Labeling Parameters:\n");

    // Safely retrieve and append the target column
    if (targetColumnComponent instanceof JTextField) {
        JTextField targetColumnField = (JTextField) targetColumnComponent;
        String targetColumn = targetColumnField.getText();
        sb.append("Target Column: ").append(targetColumn).append("\n");
    }

    // Safely retrieve and append the operator
    if (operatorComponent instanceof JComboBox<?>) {
        JComboBox<?> operatorComboBox = (JComboBox<?>) operatorComponent;
        String operator = operatorComboBox.getSelectedItem() != null ?
                operatorComboBox.getSelectedItem().toString() : "";
        sb.append("Operator: ").append(operator).append("\n");
    }

    // Safely retrieve and append the limit
    if (limitComponent instanceof JTextField) {
        JTextField limitField = (JTextField) limitComponent;
        String limit = limitField.getText();
        sb.append("Limit: ").append(limit).append("\n");
    }

    // Safely retrieve and append the label
    if (labelComponent instanceof JTextField) {
        JTextField labelField = (JTextField) labelComponent;
        String label = labelField.getText();
        sb.append("Label: ").append(label).append("\n");
    }

    // Safely retrieve and append the new column name
    if (newColumnNameComponent instanceof JTextField) {
        JTextField newColumnNameField = (JTextField) newColumnNameComponent;
        String newColumnName = newColumnNameField.getText();
        sb.append("New Column Name: ").append(newColumnName).append("\n");
    }

    // Append the rules
    sb.append("\nRules:\n");
    for (Rule rule : rulesList) {
        sb.append(rule.toString()).append("\n");
    }

    // Set the constructed string to the result area
    resultArea.setText(sb.toString());
}


    @Override
    public RuleSet getParameters(){
        String newColumnName = ((JTextField) inputFields.get("New Column Name")).getText();

        if(newColumnName.isEmpty()){
            JOptionPane.showMessageDialog(this, "New Column Name is required", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new RuleSet(newColumnName, new ArrayList<>(rulesList));
    }
}

