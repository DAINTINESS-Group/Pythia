package gr.uoi.cs.pythia.valueDistributionStatistics;

import gr.uoi.cs.pythia.model.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import java.util.ArrayList;
import java.util.List;

public class Sorting{

    private final Dataset<Row> dataset;
    private final Column column;
    private Dataset<Row> sortedData;

    public Sorting(Dataset<Row> dataset, Column column){
        this.dataset = dataset;
        this.column = column;

    }

    public Dataset<Row> getDataset(){
        return dataset;
    }

    public void sort(){
        if(column==null || column.getName()==null){
            System.out.println("NULL COLUMN");
            return;
        }
        this.sortedData = dataset.orderBy(functions.col(column.getName()).asc());

    }

    public List<Number> getsortedList(){
        List<Number> sortedList = new ArrayList<>();

        if(sortedData==null){
            System.err.println("Error: Sorting has not been performed. Call sort() first.");
            return sortedList;
        }

        List<Row> rows = sortedData.collectAsList();

        for(Row row : rows){
            Object value = row.get(row.fieldIndex(column.getName()));

            // Skip null values
            if(value==null) continue;

            String valueAsString = value.toString().trim();

            // Skip empty values
            if(valueAsString.isEmpty()) continue;

            try {
                Number numericValue = Double.parseDouble(valueAsString);

                if(!Double.isNaN(numericValue.doubleValue())){
                    sortedList.add(numericValue);
                }
            } catch (NumberFormatException e) {
                // Log the error without stopping execution
                System.err.println("Warning: Could not parse '"+valueAsString+"' in column '"+column.getName()+"'");
            }
        }

        return sortedList;
    }


}
