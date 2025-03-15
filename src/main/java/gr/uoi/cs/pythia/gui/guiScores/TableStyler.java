package gr.uoi.cs.pythia.gui.guiScores;


import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class TableStyler {
    public static void styleTable(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Απενεργοποίηση αυτόματης προσαρμογής μεγέθους
        table.setFillsViewportHeight(true); // Γέμισμα του viewport
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(220, 220, 220));
        header.setForeground(Color.BLACK);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(150);
        }
    }
}