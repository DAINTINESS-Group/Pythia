package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class StatisticsPanel extends AnalysisPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2635233518244620015L;

	public StatisticsPanel() {
        super();
    }

    @Override
    public void createPanelContent() {
        List<Column> columnList = AppController.getInstance().getDatasetProfile().getColumns();

        if (columnList == null || columnList.isEmpty()) {
            add(new JLabel("No columns found.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }
        JTable table = createStatisticsTable(columnList);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private JTable createStatisticsTable(List<Column> columnList) {
        String[] columnNames = {"Column", "Count", "Mean", "Standard Deviation", "Q1", "Median", "Q3", "Min", "Max", "Modes"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (Column column : columnList) {
            DescriptiveStatisticsProfile profile = column.getDescriptiveStatisticsProfile();
            if (profile != null) {
                Object[] rowData = {
                        column.getName(),
                        profile.getCount(),
                        profile.getMean(),
                        profile.getStandardDeviation(),
                        profile.getQ1(),
                        profile.getMedian(),
                        profile.getQ3(),
                        profile.getMin(),
                        profile.getMax(),
                        profile.modeValuetoString()
                };

                model.addRow(rowData);
                System.out.println("Added row: " + Arrays.toString(rowData));
            } else {
                System.out.println("No DescriptiveStatisticsProfile found for column: " + column.getName());
                model.addRow(new Object[]{column.getName(), "", "", "", "", "", "", "", "", ""});
            }
        }

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(150);
        }
        return table;
    }
}