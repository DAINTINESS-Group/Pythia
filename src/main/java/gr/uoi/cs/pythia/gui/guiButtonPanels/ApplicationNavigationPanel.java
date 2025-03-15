package gr.uoi.cs.pythia.gui.guiButtonPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.DatasetWriterGUI;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.ReportGeneratorGUI;
import gr.uoi.cs.pythia.gui.guiScores.WindowScores;
import gr.uoi.cs.pythia.gui.resultsGui.ResultsPanelManager;

import javax.swing.*;
import java.awt.*;

public class ApplicationNavigationPanel extends JPanel{

    public AnalysisSelectionPanel analysisPanel;
    private JButton showResults;
    private JButton editDataType;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    public ApplicationNavigationPanel(JPanel cardPanel, CardLayout cardLayout){

        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Use a more modern look with rounded borders and padding
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around buttons

        JButton registerDatasetButton = createButton("Register Dataset", new Color(40, 167, 69), ()->{
            DatasetInputPanel datasetFormPanel = new DatasetInputPanel();
            MainWindow.getMainWindow().showCard(datasetFormPanel, "datasetForm");
        });

        JButton writeDataButton = createButton("Write Data", new Color(255, 193, 7),
                ()->MainWindow.getMainWindow().showCard(new DatasetWriterGUI(cardPanel, cardLayout), "datasetWriterWindow"));
        JButton generateReportButton = createButton("Generate Report", new Color(153, 51, 255),
                ()->MainWindow.getMainWindow().showCard(new ReportGeneratorGUI(cardPanel, cardLayout), "reportGenerator"));

        JButton analysisProfileButton = createButton("Analysis Tasks Profile", new Color(0, 123, 255), ()->{ // Blue color
            analysisPanel = new AnalysisSelectionPanel(cardPanel, cardLayout);
            MainWindow.getMainWindow().showCard(analysisPanel, "analysisPanel");

        });
        add(registerDatasetButton);
        add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between buttons
        add(analysisProfileButton);
        add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between buttons
        add(generateReportButton);
        add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between buttons
        add(writeDataButton);
        add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between buttons
    }

    public JButton createButton(String text, Color color, Runnable action){
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40)); // Slightly smaller height
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2), // Original border
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding inside the button
        ));
        button.setOpaque(true); // Needed for background color to show
        button.addActionListener(e->action.run());

        // Rounded corners (Java 8+) - more modern look
        button.setUI(new RoundedButtonUI(10)); // Radius of 10 pixels

        return button;
    }


    // Custom UI class for rounded buttons
    private static class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI{
        private final int arcRadius;

        public RoundedButtonUI(int arcRadius){
            this.arcRadius = arcRadius;
        }

        @Override
        public void paint(Graphics g, JComponent c){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();

            // Paint background
            if(model.isArmed()){
                g2.setColor(b.getBackground().darker()); // Slightly darker on click
            } else {
                g2.setColor(b.getBackground());
            }
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arcRadius, arcRadius);

            // Paint border (optional, can be removed)
            g2.setColor(b.getBorder().getClass().getName().contains("LineBorder")?((javax.swing.border.LineBorder) b.getBorder()).getLineColor():b.getBackground()); // Match border color to background
            g2.drawRoundRect(0, 0, c.getWidth()-1, c.getHeight()-1, arcRadius, arcRadius);


            super.paint(g2, c); // Paint the text
            g2.dispose();
        }
    }

    public void setOnShowResultsButton(){
        if(analysisPanel!=null){
            if(showResults!=null){
                remove(showResults);
            }

            showResults = createButton("Show Results", new Color(0, 153, 233), ()->{
                ResultsPanelManager resultsPanel = new ResultsPanelManager(analysisPanel.getCheckBoxes(), cardPanel, cardLayout);
                MainWindow.getMainWindow().showCard(resultsPanel, "resultPanel");

            });

            add(showResults);
            add(Box.createRigidArea(new Dimension(0, 10)));
            revalidate();
            repaint();
        }
    }

    public void setOnEditDatatypesButton(){
        if(AppController.getInstance().getDatasetProfile()!=null){
            if(editDataType!=null){
                remove(editDataType);
            }
            if(AppController.getInstance().getDataset()!=null){
                editDataType = createButton("EditDataType", new Color(54, 75, 222), ()->
                        new WindowScores(AppController.getInstance().getDataset(), AppController.getInstance().getScoreCalculatorManager().getScoresPerColumnMap()));

                add(editDataType);
                add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between buttons

            }
        }
    }
}
