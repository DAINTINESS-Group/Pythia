package gr.uoi.cs.pythia.gui.guiScores;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DataTypeDetectorUI{
    private final JFrame frame = new JFrame("Data Type Detector Results");
    private final JDialog dialog;

    public DataTypeDetectorUI(JDialog dialog, Dataset<Row> dataset, Map<String, Map<DataType, Integer>> scoresPerColumnMap){

        this.dialog = dialog;
        frame.setLayout(new BorderLayout());
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        StructField[] structFields = dataset.schema().fields();
        String[] columnNames = new String[structFields.length];
        int i = 0;
        for(StructField field : structFields){
            columnNames[i] = field.name();
            i++;
        }
        DataPanel dataPanel = new DataPanel(dataset, columnNames);
        ScorePanel scorePanel = new ScorePanel(scoresPerColumnMap, columnNames);
        JTable dataTable = dataPanel.getTable();
        JTable scoreTable = scorePanel.getTable();
        DataTypeSelectionDialog selectionDialog = new DataTypeSelectionDialog();
        scoreTable.addMouseListener(new EditClickListener(scoreTable, frame, selectionDialog));
        dataTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        scoreTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        mainPanel.add(dataPanel);
        mainPanel.add(scorePanel);
        JButton saveButton = getjButton(dialog);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel);
        dialog.add(mainPanel, BorderLayout.CENTER);

    }

    private @NotNull JButton getjButton(JDialog dialog){
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveButton.setBackground(new Color(50, 150, 250));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e->{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select catalog for save scema"); // Set dialog title
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int userSelection = fileChooser.showSaveDialog(null);

            if(userSelection==JFileChooser.APPROVE_OPTION){
                File selectedDirectory = fileChooser.getSelectedFile();
                String directoryPath = selectedDirectory.getAbsolutePath();

                try {

                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
                        @Override
                        protected Void doInBackground() throws IOException{
                            AppController.getInstance().writeSchemaFile(directoryPath);
                            AppController.getInstance().updateSchema();
                            return null;
                        }

                        @Override
                        protected void done(){
                            SwingUtilities.invokeLater(dialog::dispose);
                        }
                    };

                    worker.execute();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "The file with schema not saved: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    //ex.printStackTrace();
                }
            }

        });
        return saveButton;
    }

    //Testing
    public static void showResults(JDialog dialog, Dataset<Row> dataset, Map<String, Map<DataType, Integer>> scoresPerColumnMap){
        SwingUtilities.invokeLater(()->new DataTypeDetectorUI(dialog, dataset, scoresPerColumnMap));
    }

    //Testing
    public JFrame getFrame(){
        return frame;
    }

    //Testing
    public JDialog getDialog(){
        return dialog;
    }

}

