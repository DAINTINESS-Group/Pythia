package gr.uoi.cs.pythia.generalinfo;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class SparkBasicInfoCalculator implements IBasicInfoCalculator {

    private final SparkSession session;
    private final Dataset<Row> dataset;
    private long numberOfLines;
    private final String pathFile;
    private Double fileSize;

    public static final Double ERROR_VALUE_FILE_SIZE = -1.0;
    public static final long ERROR_VALUE_NUMBER_OF_LINES = -1;


    public SparkBasicInfoCalculator(Dataset<Row> dataset, SparkSession session, String pathFile){
        this.dataset = dataset;
        this.session = session;
        this.pathFile = pathFile;

    }
    @Override
    public void calculateNumberOfLinesInDataset() {
        try {
            //1h
            //int defaultPartitions = 0; // with zero automatic spark create partitions
           // RDD<String> linesRDD = session.sparkContext().textFile(pathFile,defaultPartitions);
           // this.numberOfLines = linesRDD.count();

            //2h
            this.numberOfLines = dataset.count();
        } catch (NullPointerException e) {
            //e.printStackTrace();
            System.out.println("Null-Empty Dataset In Calculations NumberOfLines");
            this.numberOfLines = ERROR_VALUE_NUMBER_OF_LINES;
        }

    }
    @Override
    public void calculateFileSize() {
        try {
            FileSystem fs = FileSystem.get(Objects.requireNonNull(session)
                    .sparkContext()
                    .hadoopConfiguration());

            Path filePath = new Path(Objects.requireNonNull(pathFile));
            FileStatus fileStatus = fs.getFileStatus(filePath);

            long fileSizeInBytes = fileStatus.getLen();
            fileSize = convertBytesInMegaBytes(fileSizeInBytes);
        } catch (IOException e) {

            fileSize = ERROR_VALUE_FILE_SIZE;
        } catch (NullPointerException e) {
            System.out.println("Null Session!!");
            fileSize = ERROR_VALUE_FILE_SIZE;
        }
    }

    private double convertBytesInMegaBytes(long fileSizeInBytes){
        BigDecimal fileSizeInMB = BigDecimal.valueOf(fileSizeInBytes).divide(BigDecimal.valueOf(1024 * 1024), 2, RoundingMode.HALF_UP);
        return fileSizeInMB.doubleValue();
    }

    @Override
    public long getNumberOfLines() {
        return numberOfLines;
    }
    @Override
    public Double getFileSize() {
        return fileSize;
    }
}
