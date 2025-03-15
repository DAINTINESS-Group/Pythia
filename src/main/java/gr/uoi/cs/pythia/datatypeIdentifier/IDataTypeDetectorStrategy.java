package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

public interface IDataTypeDetectorStrategy {
     void identifyDataType(Row valueInColum);
     int getScore();
}
