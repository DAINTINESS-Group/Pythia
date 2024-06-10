package gr.uoi.cs.pythia.cardinalities;

import gr.uoi.cs.pythia.model.CardinalitiesProfile;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import static org.apache.spark.sql.functions.*;

public class SparkCardinalitiesCalculator implements ICardinalitiesCalculator{

    private final Dataset<Row> dataset;
    private long numberOfDistinctValues;
    private long numberOfNullValues;
    private String columnName;
    private CardinalitiesProfile profile;

    public SparkCardinalitiesCalculator(Dataset<Row> dataset, String columnName) {
        this.dataset = dataset;
        this.columnName = columnName;
        this.numberOfDistinctValues = -1;
        this.numberOfNullValues = -1;
        this.profile = new CardinalitiesProfile(numberOfDistinctValues,numberOfNullValues);
    }

    @Override
    public CardinalitiesProfile createCardinalitiesProfile() {
        this.profile = new CardinalitiesProfile(numberOfDistinctValues,numberOfNullValues);
        return this.profile;
    }

    @Override
    public long getNumberOfNullValues() {
    	if(this.numberOfNullValues == -1)
    		calculateNumberOfNullValues();
        return numberOfNullValues;
    }
    
    @Override
    public long getNumberOfDistinctValues() {
    	if(this.numberOfDistinctValues == -1)
    		calculateDistinctValues();
        return numberOfDistinctValues;
    }
    
    private void calculateNumberOfNullValues() {
        //numberOfNullValues = dataset.filter(dataset.col(columnName).isNull().or(dataset.col(columnName).equalTo(""))).count();
        try {
            Column column = col(columnName);
            Column nullOrEmptyCondition = column.isNull().or(expr("trim(" + columnName + ")").equalTo(""));
            Column conditionResult = when(nullOrEmptyCondition, true);
            Column countResult = count(conditionResult);

            
            //TODO CHECK WHY THIS!!!        
            Dataset<Row> result = dataset.agg(countResult);
            Row firstRow = result.first();
            numberOfNullValues = firstRow.getLong(0);

        } catch (NullPointerException e) {
            handleException("NullPointerException", e);
        } catch (Exception e) {
            handleException("Exception", e);
        }


    }
    private static void handleException(String exceptionType, Exception e) {
        System.err.println("An " + exceptionType + " occurred while calculating the number of null values");
        //e.printStackTrace();
    }

    
    private void calculateDistinctValues() {
        //numberOfDistinctValues = dataset.select(columnName).distinct().count();
       /* try {
            Dataset<Row> selectedColumn = dataset.select(functions.approx_count_distinct(columnName));
            Row firstRow = selectedColumn.first();
            numberOfDistinctValues = firstRow.getLong(0);
        } catch (NullPointerException e) {
            handleException("NullPointerException", e);
        } catch (Exception e) {
            handleException("Exception", e);
        }*/
        try {
            Column column = col(columnName);
            Column nullOrEmptyCondition = column.isNotNull().and(expr("trim(" + columnName + ")").notEqual(""));
            Column countDistinctResult = countDistinct(when(nullOrEmptyCondition, column));
            
            //TODO CHECK & FIX
            Dataset<Row> selectedColumn = dataset.select(countDistinctResult);
            Row firstRow = selectedColumn.first();
            numberOfDistinctValues = firstRow.getLong(0);
            
        } catch (NullPointerException e) {
            handleException("NullPointerException", e);
        } catch (Exception e) {
            handleException("Exception", e);
        }

    }


    //TODO maybe add sth like this to the Interface
    private CardinalitiesProfile computeCardinalityProfile() {
    	this.numberOfDistinctValues = this.getNumberOfDistinctValues();
    	this.numberOfNullValues = this.getNumberOfNullValues();
    	this.profile.setNumberOfDistinctValues(numberOfDistinctValues);
    	this.profile.setNumberOfNullValues(numberOfNullValues);
    	return this.profile;
    }

}
