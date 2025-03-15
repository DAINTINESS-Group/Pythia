package gr.uoi.cs.pythia.gui.resultsGui;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.RegressionProfile;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class RegressionPanel extends AnalysisPanel {
	public RegressionPanel() {
        super();
    }

    @Override
    public void createPanelContent() {
        List<RegressionProfile> regressionProfiles = AppController.getInstance().getDatasetProfile().getRegressionProfiles();
        if (regressionProfiles == null || regressionProfiles.isEmpty()) {
            add(new JLabel("No regression profiles found.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        JPanel regressionsPanelContainer = new JPanel(new GridLayout(0, 1)); // Vertical container

        for (RegressionProfile profile : regressionProfiles) {
            if (profile != null) {
                JPanel regressionPanel = createRegressionPanel(profile);
                regressionsPanelContainer.add(regressionPanel);
            }
        }
        JScrollPane scrollPane = new JScrollPane(regressionsPanelContainer);
        scrollPane.setBorder(new TitledBorder("Regression Results for " + AppController.getInstance().getDatasetProfile().getAlias())); // Set border here
        add(scrollPane, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private JPanel createRegressionPanel(RegressionProfile profile) {
        JPanel panel = new JPanel(new BorderLayout());

        String title = String.format("Regression Profile: %s (Intercept: %.4f, Error: %.4f)",
                profile.getType(), profile.getIntercept(), profile.getError());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JTable table = createRegressionStatisticsTable(profile);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JTable createRegressionStatisticsTable(RegressionProfile profile) {
        String[] columnNames = {"Variable", "Slope", "Correlation", "P-Value"};
        List<Column> independentVariables = profile.getIndependentVariables();
        List<Double> slopes = profile.getSlopes();
        List<Double> correlations = profile.getCorrelations();
        List<Double> pValues = profile.getpValues();

        if (independentVariables == null || slopes == null || correlations == null || pValues == null) {
            return new JTable(new Object[][]{{"Invalid data", "N/A", "N/A", "N/A"}}, columnNames);
        }

        int rowCount = independentVariables.size() + 2; // +2 Intercept,Error
        Object[][] data = new Object[rowCount][columnNames.length];
        for (int i = 0; i < independentVariables.size(); i++) {
            data[i][0] = independentVariables.get(i) != null ? independentVariables.get(i).getName() : "N/A";
            data[i][1] = slopes.get(i) != null ? slopes.get(i) : "N/A";
            data[i][2] = correlations.get(i) != null ? correlations.get(i) : "N/A";
            data[i][3] = pValues.get(i) != null ? pValues.get(i) : "N/A";
        }


        data[rowCount - 2][0] = "Intercept";
        data[rowCount - 2][1] = profile.getIntercept();
        data[rowCount - 2][2] = "N/A";
        data[rowCount - 2][3] = "N/A";


        data[rowCount - 1][0] = "Error";
        data[rowCount - 1][1] = profile.getError();
        data[rowCount - 1][2] = "N/A";
        data[rowCount - 1][3] = "N/A";

        return new JTable(data, columnNames);
    }
}