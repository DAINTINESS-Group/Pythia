package gr.uoi.cs.pythia.gui.guiScores;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;

import javax.swing.*;
import java.util.Map;

public class WindowScores extends JPanel{

    /**
	 * 
	 */
	private static final long serialVersionUID = 2837848122778560934L;

	public WindowScores(Dataset<Row> dataset, Map<String, Map<DataType, Integer>> scoresPerColumnMap ) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Data Type Detector Results");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(1920,1080 );
        dialog.setLocationRelativeTo(null);
        new DataTypeDetectorUI(dialog,dataset, scoresPerColumnMap);
        dialog.setVisible(true);
    }

}
