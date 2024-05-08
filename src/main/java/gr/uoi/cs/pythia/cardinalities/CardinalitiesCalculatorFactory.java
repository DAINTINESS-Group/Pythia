package gr.uoi.cs.pythia.cardinalities;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class CardinalitiesCalculatorFactory {
    
    public ICardinalitiesCalculator createCardinalitiesCalculator(Dataset<Row> dataset,String columnName){
        return new SparkCardinalitiesCalculator(dataset,columnName);
    }
}
