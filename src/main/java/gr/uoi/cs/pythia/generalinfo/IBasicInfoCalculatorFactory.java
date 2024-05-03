package gr.uoi.cs.pythia.generalinfo;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class IBasicInfoCalculatorFactory {

    private IBasicInfoCalculator calculator;

    public IBasicInfoCalculator createBasicInfoCalculator(Dataset<Row> dataset, SparkSession session, String pathFile){
        calculator = new SparkBasicInfoCalculator(dataset,session,pathFile);
        executeCalculations();
        return calculator;
    }

    private void executeCalculations(){
        calculator.calculateFileSize();
        calculator.calculateNumberOfLinesInDataset();
    }
}
