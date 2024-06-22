package gr.uoi.cs.pythia.cardinalities;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class CardinalitiesCalculatorFactory {


    private ICardinalitiesCalculator cardinalitiesCalculator;

    public ICardinalitiesCalculator createCardinalitiesCalculator(Dataset<Row> dataset,String columnName){
        cardinalitiesCalculator= new SparkCardinalitiesCalculator(dataset,columnName);
        return cardinalitiesCalculator;
    }

}
