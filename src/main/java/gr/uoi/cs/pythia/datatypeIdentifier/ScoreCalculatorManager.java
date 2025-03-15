package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreCalculatorManager{

    private final Map<DataType, DataDetectorStrategy> typeDataDetectorStrategyMap;
    private final Dataset<Row> dataset;
    private final Map<DataType, Integer> dataTypeScoreMap;
    private final Map<String, Map<DataType, Integer>> scoresPerColumnMap;
    private static final int NUMBER_OF_ROWS_SAMPLING = 50;


    public ScoreCalculatorManager(Dataset<Row> dataset){
        this.dataset = dataset;
        this.typeDataDetectorStrategyMap = new HashMap<>();
        this.dataTypeScoreMap = new HashMap<>();
        this.scoresPerColumnMap = new HashMap<>();
    }

    private void initializeMapTypeStrategies(){
        typeDataDetectorStrategyMap.put(DataTypes.StringType, new StringTypeStrategy());
        typeDataDetectorStrategyMap.put(DataTypes.BooleanType, new BooleanTypeStrategy());
        typeDataDetectorStrategyMap.put(DataTypes.DateType, new DateTypeStrategy());
        typeDataDetectorStrategyMap.put(DataTypes.TimestampType, new TimestampTypeStrategy());
        typeDataDetectorStrategyMap.put(DataTypes.ShortType, new ShortTypeStrategy());
        typeDataDetectorStrategyMap.put(DataTypes.IntegerType, new IntegerTypeStrategy());
        typeDataDetectorStrategyMap.put(DataTypes.LongType, new LongTypeStrategy());
        typeDataDetectorStrategyMap.put(DataTypes.FloatType, new FloatTypeStrategy());
        typeDataDetectorStrategyMap.put(DataTypes.DoubleType, new DoubleTypeStrategy());
        typeDataDetectorStrategyMap.put(DataTypes.createDecimalType(), new DecimalTypeStrategy());
    }


    public List<Row> createDatasetToCalculateScore(String columnName){
        Dataset<Row> sampleData = dataset.select(columnName).limit(NUMBER_OF_ROWS_SAMPLING);
        return sampleData.collectAsList();
    }

    private void calculateScoresPerColumnValue(Row valueInColumn){
        typeDataDetectorStrategyMap.forEach((key, value)->{
            value.identifyDataType(valueInColumn);
            int score = value.getScore();
            setScoresInMap(key, score);
        });
    }

    public void calculateScores(){
        for(String columnName : dataset.columns()){
            calculateScorePerColumn(columnName);
        }
    }

    private void calculateScorePerColumn(String columnName){
        initializeMapTypeStrategies();
        List<Row> listColumnValues = createDatasetToCalculateScore(columnName);
        for(Row valueInColumn : listColumnValues){
            if(!valueInColumn.isNullAt(0)){
                calculateScoresPerColumnValue(valueInColumn);
            }
        }
        scoresPerColumnMap.put(columnName, new HashMap<>(dataTypeScoreMap));
    }

    private void setScoresInMap(DataType type, Integer score){
        if(typeDataDetectorStrategyMap.containsKey(type)){
            dataTypeScoreMap.put(type, score);
        }
    }

    public Map<String, Map<DataType, Integer>> getScoresPerColumnMap(){
        return scoresPerColumnMap;
    }

    @Override
    public String toString(){
        return "DataTypeDetectorManager{"+"scoresPerColumnMap="+scoresPerColumnMap+'}';
    }

}
