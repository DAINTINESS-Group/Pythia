package gr.uoi.cs.pythia.gui.resultsGui;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public abstract class AnalysisPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -670864156692313912L;
	public AnalysisPanel() {
		setLayout(new BorderLayout());
		runSwingWorker();
	}
	public abstract void createPanelContent();
	public void runSwingWorker() {

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				createPanelContent();
				return null;
			}
			@Override
			protected void done() {
				try {
					revalidate();
					repaint();
					get(); // Check for exceptions
				} catch (InterruptedException ex) {
					// Handle interruption
					JOptionPane.showMessageDialog(AnalysisPanel.this,
							"The operation was interrupted: " + ex.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				} catch (ExecutionException ex) {
					// Handle execution exception
					JOptionPane.showMessageDialog(AnalysisPanel.this,
							"An error occurred: " + ex.getCause().getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		worker.execute(); // Now execute the worker
	}

}