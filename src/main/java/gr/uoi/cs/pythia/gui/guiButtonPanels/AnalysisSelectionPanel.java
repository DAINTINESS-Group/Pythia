package gr.uoi.cs.pythia.gui.guiButtonPanels;

import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.AnalysisTabsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class AnalysisSelectionPanel extends JPanel {

	private final JTextArea resultsArea;
    private final JCheckBox[] checkBoxes;
    private String path;

    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    public AnalysisSelectionPanel(JPanel cardPanel,CardLayout cardLayout) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;

        setLayout(new GridBagLayout());
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create checkboxes
        checkBoxes = new JCheckBox[]{
                createCheckBox("Descriptive Stats"),
                createCheckBox("Histograms"),
                createCheckBox("All Pairs Correlations"),
                createCheckBox("Decision Trees"),
                createCheckBox("Dominance Patterns"),
                createCheckBox("Outlier Detection"),
                createCheckBox("Regression"),
                createCheckBox("Clustering"),
                createCheckBox("Labeling Parameters")
        };

        // Add checkboxes to the panel
        for (JCheckBox checkBox : checkBoxes) {
            add(checkBox, gbc);
            gbc.gridy++; // Move to the next row
            checkBox.addActionListener(e -> updateResultsArea());
        }


        JPanel savePathPanel = getjPanel();
        add(savePathPanel, gbc); // Add the panel to the AnalysisSelectionPanel
        gbc.gridy++; // Move to the next row

        // Add the results area
        resultsArea = new JTextArea(10, 40);
        resultsArea.setEditable(false);
        JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
        gbc.fill = GridBagConstraints.BOTH;
        add(resultsScrollPane, gbc);
        gbc.gridy++; // Move to the next row

        // Add the OK button
        JButton okButton = createButton();
        add(okButton, gbc);

        // OK button action listener
        okButton.addActionListener(e -> {
            ArrayList<String> selectedAnalyses = new ArrayList<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedAnalyses.add(checkBox.getText());
                }
            }
            if (path == null) {
                JOptionPane.showMessageDialog(this, "Give path ", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!selectedAnalyses.isEmpty()) {
                showAnalysisTabs(selectedAnalyses);
            } else {
                JOptionPane.showMessageDialog(this, "Please select at least one analysis.");

            }
        });

        updateResultsArea();
    }

    private @NotNull JPanel getjPanel(){
        JPanel savePathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Panel for better layout
        JTextField filePathField = new JTextField(20);
        filePathField.setEditable(false); // Make the text field read-only

        JButton filePathButton = new JButton("Select File Path");
        filePathButton.addActionListener(e -> choosePath(filePathField));

        savePathPanel.add(filePathButton);
        savePathPanel.add(filePathField);
        return savePathPanel;
    }

    private JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(new Font("Arial", Font.PLAIN, 14));
        checkBox.setForeground(new Color(50, 50, 50));
        return checkBox;
    }

    private JButton createButton() {
        JButton button = new JButton("OK");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    public void choosePath(JTextField pathField){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if(parentWindow==null){
            JOptionPane.showMessageDialog(null, "Unable to find parent window.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int result = fileChooser.showOpenDialog(parentWindow);
        if(result==JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            if(selectedFile!=null){
                Path selectedPath = selectedFile.toPath();
                pathField.setText(selectedPath.toString());
                path = selectedPath.toString();
            }
        }
    }

    public void updateResultsArea() {
        StringBuilder results = new StringBuilder();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                results.append(checkBox.getText()).append(" selected\n");
            }
        }
        resultsArea.setText(results.toString());
    }

    private void showAnalysisTabs(ArrayList<String> selectedAnalyses) {
        AnalysisTabsPanel tabsGUI = new AnalysisTabsPanel(selectedAnalyses, path,cardPanel,cardLayout);
        MainWindow.getMainWindow().setOnShowResultsButton();
        MainWindow.getMainWindow().showCard(tabsGUI, "analysisTabs");
    }

    public JCheckBox[] getCheckBoxes() {
        return checkBoxes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String testPath) {
        this.path = testPath;
    }
}