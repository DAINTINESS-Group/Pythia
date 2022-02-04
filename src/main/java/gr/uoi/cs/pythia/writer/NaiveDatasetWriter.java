package gr.uoi.cs.pythia.writer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class NaiveDatasetWriter implements IDatasetWriter {

  @Override
  public void write(Dataset<Row> dataset, String path) throws IOException {
    PrintWriter printWriter = new PrintWriter(new FileWriter(path), true);
    printWriter.println(String.join(",", dataset.columns()));
    for (Row row : dataset.collectAsList()) {
      List<String> rowValues = getRowValues(row);
      printWriter.println(String.join(",", rowValues));
    }
    printWriter.close();
  }

  private List<String> getRowValues(Row row) {
    List<String> rowValues = new ArrayList<>();
    for (int i = 0; i < row.length(); i++) {
      Object rowValue = row.get(i);
      rowValues.add(rowValue != null ? rowValue.toString() : "");
    }
    return rowValues;
  }
}
