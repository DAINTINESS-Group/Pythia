package gr.uoi.cs.pythia.generalinfo;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class IBasicInfoCalculatorFactory {

    private IBasicInfoCalculator calculator;

    public IBasicInfoCalculator createBasicInfoCalculator(Dataset<Row> dataset, String pathFile){
        calculator = new SparkBasicInfoCalculator(dataset,pathFile);
        executeCalculations();
        return calculator;
    }

    private void executeCalculations(){
        calculator.calculateFileSize();
        calculator.calculateNumberOfLinesInDataset();
    }
}
