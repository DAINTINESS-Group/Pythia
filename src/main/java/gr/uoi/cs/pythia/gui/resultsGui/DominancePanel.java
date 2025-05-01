package gr.uoi.cs.pythia.gui.resultsGui;
import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.dominance.DominanceResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DominancePanel extends AnalysisPanel{

    /**
	 * 
	 */
	private static final long serialVersionUID = -7082887602070781555L;

	public DominancePanel(){
        super();
    }
    @Override
    public void createPanelContent(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Get dominance results
        List<DominanceResult> highDominanceResults = AppController.getInstance().getDatasetProfile().getPatternsProfile().getHighDominanceResults();
        List<DominanceResult> lowDominanceResults = AppController.getInstance().getDatasetProfile().getPatternsProfile().getLowDominanceResults();

        // Create panels for high and low dominance results
        JPanel highPanel = createDominancePanel("High Dominance Results:", highDominanceResults);
        JPanel lowPanel = createDominancePanel("Low Dominance Results:", lowDominanceResults);

        // Add panels to the main panel
        panel.add(highPanel);
        panel.add(lowPanel);

        // Add the main panel to the frame
        this.add(panel);
        this.revalidate();
        this.repaint();
    }

    private JPanel createDominancePanel(String title, List<DominanceResult> dominanceResults){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create a non-editable JTextArea to display results
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Append each dominance result to the text area
        for(DominanceResult result : dominanceResults){
            textArea.append(buildDominanceResultString(result)+"\n");
        }

        // Add the text area to a scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Add the title and scroll pane to the panel
        panel.add(new JLabel(title), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private String buildDominanceResultString(DominanceResult dominanceResult){
        StringBuilder resultString = new StringBuilder();

        resultString.append("--------------------------------------------------\n");

        resultString.append(dominanceResult.titleToString()).append("\n");

        resultString.append("Metadata:\n");
        resultString.append(dominanceResult.metadataToString()).append("\n");

        resultString.append("Detailed Results:\n");
        resultString.append(dominanceResult.identificationResultsToString(true)).append("\n");

        resultString.append("Identified Dominance Features:\n");
        resultString.append(dominanceResult.dominanceToString(true)).append("\n");

        if(dominanceResult.hasTwoCoordinates()){
            resultString.append("Query Results:\n");
            resultString.append(dominanceResult.queryResultToString()).append("\n");
        }

        resultString.append("--------------------------------------------------\n");

        return resultString.toString();
    }
}