package gr.uoi.cs.pythia.gui.resultsGui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ResultsPanelManager extends JPanel {

	private final List<AnalysisType> selectedAnalysis;
    private final JTabbedPane tabbedPane;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    public ResultsPanelManager(JCheckBox[] checkBoxes, JPanel cardPanel, CardLayout cardLayout) {
        this.cardPanel =cardPanel;
        this.cardLayout = cardLayout;
        this.selectedAnalysis = new ArrayList<>();

        if (checkBoxes != null) {
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedAnalysis.add(AnalysisType.fromString(checkBox.getText()));
                }
            }
        }

        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        createAnalysisTabs();
        add(createBackButton(), BorderLayout.SOUTH);
    }

    private void createAnalysisTabs() {
        for (AnalysisType analysis : selectedAnalysis) {
            AnalysisPanel panel = AnalysisPanelFactory.createPanel(analysis); // Get AnalysisPanel
            tabbedPane.addTab(analysis.getLabel(), panel); // Add the AnalysisPanel
        }
    }

    private JButton createBackButton() {
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            cardLayout.first(cardPanel);
            cardLayout.next(cardPanel);
            cardLayout.next(cardPanel);
             });
        return backButton;
    }
}