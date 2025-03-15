package gr.uoi.cs.pythia.gui.guiScores;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;

import javax.swing.*;


public class DataTypeSelectionDialog {

    public void showDialog(JFrame frame, String columnName) {
        String[] dataTypes = {"STRING", "BOOLEAN", "DATE", "TIMESTAMP", "SHORT", "INTEGER", "LONG", "FLOAT", "DOUBLE", "DECIMAL"};
        JComboBox<String> comboBox = new JComboBox<>(dataTypes);
        int option = JOptionPane.showConfirmDialog(frame, comboBox, "Select Data Type for Column: " + columnName,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String selectedTypeAsString = (String) comboBox.getSelectedItem();
            DataType selectedType = null;
            if(selectedTypeAsString!=null){
                selectedType = mapStringToDataType(selectedTypeAsString);
            }
            AppController.getInstance().onDataTypeSelected(selectedType,columnName);
        }
    }

    public DataType mapStringToDataType(String selectedType) {
        switch (selectedType) {
            case "STRING": return DataTypes.StringType;
            case "BOOLEAN": return DataTypes.BooleanType;
            case "DATE": return DataTypes.DateType;
            case "TIMESTAMP": return DataTypes.TimestampType;
            case "SHORT": return DataTypes.ShortType;
            case "INTEGER": return DataTypes.IntegerType;
            case "LONG": return DataTypes.LongType;
            case "FLOAT": return DataTypes.FloatType;
            case "DOUBLE": return DataTypes.DoubleType;
            case "DECIMAL": return DataTypes.createDecimalType();
            default: return null;
        }
    }

}

