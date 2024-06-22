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
    private final String columnName;
    private final CardinalitiesProfile profile;


    public SparkCardinalitiesCalculator(Dataset<Row> dataset, String columnName) {
        this.dataset = dataset;
        this.columnName = columnName;
        this.numberOfDistinctValues = -1;
        this.numberOfNullValues = -1;
        this.profile = new CardinalitiesProfile(numberOfDistinctValues,numberOfNullValues);

    }

    @Override
    public  CardinalitiesProfile computeCardinalityProfile() {
        this.numberOfDistinctValues = this.getNumberOfDistinctValues();
        this.numberOfNullValues = this.getNumberOfNullValues();
        this.profile.setNumberOfDistinctValues(numberOfDistinctValues);
        this.profile.setNumberOfNullValues(numberOfNullValues);
        return this.profile;
    }


    public long getNumberOfNullValues() {
    	if(this.numberOfNullValues == -1)
    		calculateNumberOfNullValues();
        return numberOfNullValues;
    }


    public long getNumberOfDistinctValues() {
    	if(this.numberOfDistinctValues == -1)
    		calculateDistinctValues();
        return numberOfDistinctValues;
    }

    private void calculateNumberOfNullValues() {
        try{
            Column column = col(columnName);
            this.numberOfNullValues = dataset.select(column)
                    .filter(column.isNull().or(trim(column).equalTo("")))
                    .count();
        }catch (NullPointerException e) {
            handleException("NullPointerException");
        } catch (Exception e) {
            handleException("Exception");
        }
        /*
         List<Row> columnValues = dataset.select(this.column).collectAsList();
         System.out.println(columnValues);
         this.numberOfNullValues = 0;
         for(Row row : columnValues){
             if (row.get(0) == null || row.get(0).toString().trim().equals("")){
                 this.numberOfNullValues++;
             }
         }
*/

    }
    private static void handleException(String exceptionType) {
        System.err.println("An " + exceptionType + " occurred while calculating the number of null values");

    }

    
    private void calculateDistinctValues() {
        try {
            Column column = col(columnName);
            this.numberOfDistinctValues = dataset.select(column)
                    .filter(column.isNotNull().and(trim(column).notEqual("")))
                    .distinct()
                    .count();
        } catch (NullPointerException e) {
            handleException("NullPointerException");
        } catch (Exception e) {
            handleException("Exception");
        }
        /*
        //USE hashSet
        Column column = col(columnName);
        Set<Object> columnValues = new HashSet<>();
        for (Object value : dataset.select(column).collectAsList()) { // Danger collect asLIst -> out of memory !!
            if(value == null || value.toString().trim().equals("")){
                columnValues.add(value);
            }
        }*/

    }

}
