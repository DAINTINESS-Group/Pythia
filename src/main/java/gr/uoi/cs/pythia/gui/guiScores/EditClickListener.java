package gr.uoi.cs.pythia.gui.guiScores;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditClickListener extends MouseAdapter {

    private final JTable table;
    private final JFrame frame;
    private final DataTypeSelectionDialog selectionDialog;

    public EditClickListener(JTable table, JFrame frame, DataTypeSelectionDialog selectionDialog){
        this.table = table;
        this.frame = frame;
        this.selectionDialog = selectionDialog;
    }

    @Override
    public void mouseClicked(MouseEvent e){
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if(column==table.getColumnModel().getColumnCount()-1){
            String columnName = table.getValueAt(row, 0).toString();
            selectionDialog.showDialog(frame, columnName);
        }
    }
}