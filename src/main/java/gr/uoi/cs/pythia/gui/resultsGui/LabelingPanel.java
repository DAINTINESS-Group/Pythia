package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import javax.swing.*;
import java.awt.*;

public class LabelingPanel extends AnalysisPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4974978945258218129L;

	public LabelingPanel( ) {
        super();
    }

    @Override
    public void createPanelContent() {
        Dataset<Row> dataset = AppController.getInstance().getDataset();

        if (dataset == null) {
            add(new JLabel("No data available.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }
        JTable table = createDatasetTable(dataset);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private JTable createDatasetTable(Dataset<Row> dataset) {
        String[] columnNames = dataset.columns();
        Object[][] data = new Object[(int) dataset.count()][columnNames.length];

        int rowIndex = 0;
        for (Row row : dataset.collectAsList()) {
            for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
                data[rowIndex][colIndex] = row.get(colIndex);
            }
            rowIndex++;
        }
        return new JTable(data, columnNames);
    }
}
