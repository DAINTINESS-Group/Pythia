package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.CorrelationsProfile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;


public class CorrelationsPanel extends AnalysisPanel { // Inherit from AnalysisPanel

    /**
	 * 
	 */
	private static final long serialVersionUID = -2911930085195678658L;


	public CorrelationsPanel( ) {
        super();
        //createPanelContent();
    }

    @Override
    public void createPanelContent() {
        List<Column> columnList = AppController.getInstance().getDatasetProfile().getColumns();
        JPanel mainPanel = new JPanel(new BorderLayout()); // Main panel with BorderLayout

        if (columnList != null && !columnList.isEmpty()) {
            JPanel correlationsPanelContainer = new JPanel(new GridLayout(0, 1)); // Vertical container for correlation panels

            for (Column column : columnList) {
                JPanel correlationPanel = createCorrelationPanel(column);
                if (correlationPanel != null) { // Check if panel creation was successful
                    correlationsPanelContainer.add(correlationPanel);
                }
            }
            mainPanel.add(new JScrollPane(correlationsPanelContainer), BorderLayout.CENTER); // Scrolling for all correlation panels
        } else {
            mainPanel.add(new JLabel("No columns or correlations profile found.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        add(mainPanel, BorderLayout.CENTER); // Add the main panel to the CorrelationsPanel
        revalidate();
        repaint();
    }


    private JPanel createCorrelationPanel(Column column) {
        CorrelationsProfile correlationsProfile = column.getCorrelationsProfile();

        if (correlationsProfile != null) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder("Correlations for " + column.getName()));

            String[] columnNames = {"Target Column", "Correlation"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);

            for (Map.Entry<String, Double> entry : correlationsProfile.getAllCorrelations().entrySet()) {
                model.addRow(new Object[]{entry.getKey(), entry.getValue()});
            }
            return panel;
        } else {
            System.out.println("No correlations profile found for column: " + column.getName());
            return null; // Return null if no correlations
        }
    }
}

