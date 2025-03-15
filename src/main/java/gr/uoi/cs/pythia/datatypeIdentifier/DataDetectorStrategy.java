package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

public abstract class DataDetectorStrategy implements IDataTypeDetectorStrategy{

    protected int score;

    public DataDetectorStrategy(){
        this.score = 0;
    }

    @Override
    public void identifyDataType(Row valueInColum){
        try {
            findDataType(valueInColum);
        } catch (Exception e) {
            System.err.println("Value: "+valueInColum+" skiked about findDataType because is: "+ e.getMessage() );
        }
    }
        public abstract void findDataType (Row valueInColum);
        @Override
        public int getScore () {
            return score;
        }

    }
