package gr.uoi.cs.pythia.valueDistributionStatistics;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DistributionsValues.Mode;
import gr.uoi.cs.pythia.model.DistributionsValuesProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public class ManagerValueDistributionsTasks{

    private final Dataset<Row> dataset;
    private final ValueDistributionFactory valueDistributionFactory;
    private final List<Column> columnList;
    private static final int MAX_NUMBER_MODE_VALUES = 30;


    public ManagerValueDistributionsTasks(Dataset<Row> dataset, List<Column> columnList){
        this.dataset = dataset;
        this.columnList = columnList;
        this.valueDistributionFactory = new ValueDistributionFactory();
    }

    public void calculateAllDistributions(){

        for(Column column : columnList){
            IValueDistributionsTasks valueDistributionsTasks = valueDistributionFactory.createDistribution(dataset, column);
            valueDistributionsTasks.calculateMode();
            valueDistributionsTasks.createQuartilesProfile();
            valueDistributionsTasks.createHistogramForQuartiles();
        }
        optimizeModeValues();
    }


    private void optimizeModeValues(){
        for(Column column : columnList){
            DistributionsValuesProfile distributionsValuesProfile = column.getDistributionsValuesProfile();
            List<Mode> modeList = distributionsValuesProfile.getMode();
            if(modeList.size() > MAX_NUMBER_MODE_VALUES){
                modeList = modeList.subList(0, MAX_NUMBER_MODE_VALUES);
                distributionsValuesProfile.setMode(modeList);
            }
        }

    }

}
