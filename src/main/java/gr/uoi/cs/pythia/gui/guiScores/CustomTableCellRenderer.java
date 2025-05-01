package gr.uoi.cs.pythia.gui.guiScores;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4870661113959179664L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            if (row % 2 == 0) {
                c.setBackground(new Color(230, 230, 255)); // Light blue for even rows
            } else {
                c.setBackground(Color.WHITE); // White for odd rows
            }
        }
        return c;
    }
}
