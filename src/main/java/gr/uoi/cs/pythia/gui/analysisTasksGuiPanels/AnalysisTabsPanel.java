package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnalysisTabsPanel extends JPanel {

    /**
	 * 
	 */

	private final JList<String> tabList;
    public final DefaultListModel<String> listModel;
    private final CardLayout cardLayout;
    private final JPanel cardsPanel;
    private final List<JPanel> tabPanels = new ArrayList<>();
    public final List<String> selectedAnalyses;
    private final Map<String, Boolean> analysisFlags = new HashMap<>();
    public final JButton computeAllButton;
    private JDialog progressDialog;
    private final JPanel mainCardPanel;
    private final CardLayout mainCardLayout;
    public AnalysisTabsPanel(List<String> initialSelectedAnalyses, String path,JPanel mainCardPanel, CardLayout mainCardLayout) {
        this.mainCardLayout =mainCardLayout;
        this.mainCardPanel = mainCardPanel;

        this.selectedAnalyses = new ArrayList<>(initialSelectedAnalyses);
        initAnalysisFlags();
        setLayout(new BorderLayout());
        // Tab List
        listModel = new DefaultListModel<>();
        tabList = new JList<>(listModel);
        tabList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabList.setBackground(new Color(45, 45, 45));
        tabList.setForeground(Color.WHITE);
        tabList.setFont(new Font("Arial", Font.PLAIN, 16));
        tabList.setSelectionBackground(new Color(85, 85, 85));
        tabList.setSelectionForeground(Color.WHITE);
        tabList.setFixedCellHeight(40);
        tabList.setFixedCellWidth(200);

        tabList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = tabList.getSelectedIndex();
                if (index >= 0) {
                    showTab(index);
                }
            }
        });

        // Content Panel
        cardsPanel = new JPanel();
        cardLayout = new CardLayout();
        cardsPanel.setLayout(cardLayout);
        cardsPanel.setBackground(new Color(240, 240, 240));

        // Main Panel Layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(tabList), BorderLayout.WEST);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        // Compute All Button
        computeAllButton = new JButton("Compute All Calculations");
        computeAllButton.setVisible(false);
        computeAllButton.addActionListener(e -> computeAllCalculations());
        mainPanel.add(computeAllButton, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);


        // Add Tabs Dynamically
        for (String analysis : selectedAnalyses) {
            addAnalysisTab(analysis);
        }
        addTab("Highlight", createTabContent("Highlight Parameters", new HighlightParametersGUI(this, path,cardsPanel,cardLayout)));
    }

    private void computeAllCalculations() {
        SwingUtilities.invokeLater(this::showProgressDialog);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    AppController.getInstance().computeProfileOfDataset();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    //ex.printStackTrace();
                } finally {
                    SwingUtilities.invokeLater(AnalysisTabsPanel.this::hideProgressDialog);
                    //MainWindow.getMainWindow().setOnShowResultsButton();
                    //MainWindow.getMainWindow().showNavigationPanel();
                    mainCardLayout.first(mainCardPanel);
                    mainCardLayout.next(mainCardPanel);
                    mainCardLayout.next(mainCardPanel);
                }
            }
        };
        worker.execute();
        System.out.println("Compute All button clicked!");
    }

    private void showProgressDialog() {
        progressDialog = new JDialog((Frame) null, "Compute all calculations", true);
        progressDialog.setLayout(new BorderLayout());
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressDialog.add(progressBar, BorderLayout.CENTER);

        progressDialog.setVisible(true);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isVisible()) {
            progressDialog.dispose();
        }
    }

    private void initAnalysisFlags() {
        analysisFlags.put("Descriptive Stats", false);
        analysisFlags.put("Histograms", false);
        analysisFlags.put("All Pairs Correlations", false);
        analysisFlags.put("Decision Trees", false);
        analysisFlags.put("Dominance Patterns", false);
        analysisFlags.put("Outlier Detection", false);
        analysisFlags.put("Regression", false);
        analysisFlags.put("Clustering", false);
        analysisFlags.put("Labeling Parameters", false);

        if (selectedAnalyses.contains("Dominance Patterns") || selectedAnalyses.contains("Outlier Detection")) {
            if (!analysisFlags.get("Descriptive Stats") && !selectedAnalyses.contains("Descriptive Stats")) {
                selectedAnalyses.add("Descriptive Stats");
                analysisFlags.put("Descriptive Stats", true);
            }
            if (!analysisFlags.get("All Pairs Correlations") && !selectedAnalyses.contains("All Pairs Correlations")) {
                selectedAnalyses.add("All Pairs Correlations");
                analysisFlags.put("All Pairs Correlations", true);
            }
        }
        if (selectedAnalyses.contains("Regression")) {
            if (!analysisFlags.get("All Pairs Correlations") && !selectedAnalyses.contains("All Pairs Correlations")) {
                selectedAnalyses.add("All Pairs Correlations");
                analysisFlags.put("All Pairs Correlations", true);
            }
        }
        if (selectedAnalyses.contains("Histograms")) {
            if (!analysisFlags.get("Descriptive Stats") && !selectedAnalyses.contains("Descriptive Stats")) {
                selectedAnalyses.add("Descriptive Stats");
                analysisFlags.put("Descriptive Stats", true);
            }
        }
        if (selectedAnalyses.contains("Decision Trees")) {
            if (!analysisFlags.get("Labeling Parameters")) {
                selectedAnalyses.add("Labeling Parameters");
                analysisFlags.put("Labeling Parameters", true);
            }
        }

    }

    private void addAnalysisTab(String analysis) {
        switch (analysis) {
            case "Clustering":
                analysisFlags.put("Clustering", true);
                addTab("Clustering", createTabContent("Clustering",
                        new ClusteringGUI(this,cardsPanel,cardLayout)));
                break;
            case "All Pairs Correlations":
                analysisFlags.put("All Pairs Correlations", true);
                addTab("Correlations", createTabContent("Correlations",
                        new CorrelationsGUI(this,cardsPanel,cardLayout)));
                break;
            case "Dominance Patterns":
                analysisFlags.put("Dominance Patterns", true);
                addTab("Dominance Patterns", createTabContent("Dominance Patterns",
                        new DominanceParametersGUI(this,cardsPanel,cardLayout)));
                break;
            case "Histograms":
                analysisFlags.put("Histograms", true);
                addTab("Histograms", createTabContent("Histograms",
                        new HistogramGUI(this,cardsPanel,cardLayout)));
                break;
            case "Outlier Detection":
                analysisFlags.put("Outlier Detection", true);
                addTab("Outlier", createTabContent("Outlier Parameters",
                        new OutlierAnalysisGUI(this,cardsPanel,cardLayout)));
                break;
            case "Regression":
                analysisFlags.put("Regression", true);
                addTab("Regression", createTabContent("Regressions",
                        new RegressionGUI(this,cardsPanel,cardLayout)));
                break;
            case "Labeling Parameters":
                analysisFlags.put("Labeling Parameters", true);
                addTab("Labeling Parameters", createTabContent("Labeling",
                        new LabelingSystemGUI(this,cardsPanel,cardLayout)));
                break;
            case "Decision Trees":
                analysisFlags.put("Decision Trees", true);
                // Add Decision Tree GUI creation here when you have it.
                break;
            case "Descriptive Stats":
                analysisFlags.put("Descriptive Stats", true);
                break;
            default:
                break;
        }


    }

    private JPanel createTabContent(String title, JPanel contentPanel) {
        JPanel tabContentPanel = new JPanel(new BorderLayout());
        tabContentPanel.setBackground(new Color(240, 240, 240));
        tabContentPanel.add(new JLabel(title), BorderLayout.NORTH);
        tabContentPanel.add(contentPanel, BorderLayout.CENTER);
        return tabContentPanel;
    }

    public void addTab(String tabName, JPanel tabContent) {
        listModel.addElement(tabName);
        tabPanels.add(tabContent);
        cardsPanel.add(tabContent, String.valueOf(tabPanels.size() - 1));

        // Show/Hide Compute All Button
        //computeAllButton.setVisible(listModel.isEmpty()); // Show if there are tabs
    }

    public void removeTab(String tabName) {
        int index = listModel.indexOf(tabName);
        if (index >= 0) {
            cardsPanel.remove(tabPanels.get(index));
            tabPanels.remove(index);
            listModel.remove(index);

            cardsPanel.revalidate();
            cardsPanel.repaint();


            if (listModel.isEmpty()) {
                computeAllButton.setVisible(true);
            }

        }
    }


    private void showTab(int index) {
        if (index >= 0 && index < tabPanels.size()) { // Check bounds
            cardLayout.show(cardsPanel, String.valueOf(index));
        }
    }

    public Map<String, Boolean> getAnalysisFlags() {
        return analysisFlags;
    }

    public JPanel getCardsPanel() {
        return cardsPanel;
    }
}