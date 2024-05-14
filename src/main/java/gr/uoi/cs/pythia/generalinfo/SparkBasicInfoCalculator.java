package gr.uoi.cs.pythia.generalinfo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class SparkBasicInfoCalculator implements IBasicInfoCalculator {


    private final Dataset<Row> dataset;
    private long numberOfLines;
    private final String pathFile;
    private Double fileSize;
    private long fileSizeInBytes;

    public static final Double ERROR_VALUE_FILE_SIZE = -1.0;
    public static final long ERROR_VALUE_NUMBER_OF_LINES = -1;


    public SparkBasicInfoCalculator(Dataset<Row> dataset, String pathFile){
        this.dataset = dataset;
        this.pathFile = pathFile;

    }
    @Override
    public void calculateNumberOfLinesInDataset() {
        try {
            this.numberOfLines = dataset.count();
        } catch (NullPointerException e) {
            System.out.println("Null-Empty Dataset In Calculations NumberOfLines");
            this.numberOfLines = ERROR_VALUE_NUMBER_OF_LINES;
        }

    }
    @Override
    public void calculateFileSize() {
        try {
            Configuration configuration = new Configuration();
            FileSystem fs = FileSystem.get(configuration);
            Path path = new Path(pathFile);
            FileStatus fileStatus = fs.getFileStatus(path);
            fileSizeInBytes = fileStatus.getLen();
            fileSize = convertBytesInMegaBytes(fileSizeInBytes);
        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            fileSize = ERROR_VALUE_FILE_SIZE;
        }
    }

    private double convertBytesInMegaBytes(long fileSizeInBytes){
        BigDecimal fileSizeInMB = BigDecimal.valueOf(fileSizeInBytes).divide(BigDecimal.valueOf(1024 * 1024),2, RoundingMode.HALF_EVEN);
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
    public long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

}
