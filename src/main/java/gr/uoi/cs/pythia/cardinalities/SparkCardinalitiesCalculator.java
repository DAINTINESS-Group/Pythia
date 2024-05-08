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

    public SparkCardinalitiesCalculator(Dataset<Row> dataset, String columnName) {
        this.dataset = dataset;
        this.columnName = columnName;
        this.numberOfDistinctValues = 0;
        this.numberOfNullValues = 0;
    }

    @Override
    public CardinalitiesProfile createCardinalitiesProfile() {
        CardinalitiesProfile profile = new CardinalitiesProfile(numberOfDistinctValues,numberOfNullValues);
        return profile;
    }

    @Override
    public void calculateNumberOfNullValues() {
        //numberOfNullValues = dataset.filter(dataset.col(columnName).isNull().or(dataset.col(columnName).equalTo(""))).count();
        try {
            Column column = col(columnName);
            Column nullOrEmptyCondition = column.isNull().or(expr("trim(" + columnName + ")").equalTo(""));
            Column conditionResult = when(nullOrEmptyCondition, true);
            Column countResult = count(conditionResult);
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

    @Override
    public void calculateDistincValues() {
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
            Dataset<Row> selectedColumn = dataset.select(countDistinctResult);
            Row firstRow = selectedColumn.first();
            numberOfDistinctValues = firstRow.getLong(0);
        } catch (NullPointerException e) {
            handleException("NullPointerException", e);
        } catch (Exception e) {
            handleException("Exception", e);
        }

    }
    @Override
    public long getNumberOfNullValues() {
        return numberOfNullValues;
    }
    @Override
    public long getNumberOfDistinctValues() {
        return numberOfDistinctValues;
    }


}
