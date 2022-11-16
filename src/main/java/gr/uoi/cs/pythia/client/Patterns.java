package gr.uoi.cs.pythia.client;

import java.io.File;
import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.Metadata;

import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;

public class Patterns {

	// This is a dummy main method
	// Its purpose is to assist with the development of highlight pattern identification
	public static void main(String[] args) throws AnalysisException, IOException {
		IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
		
		StructType schema =
                new StructType(
                        new StructField[]{
                                new StructField("name", DataTypes.StringType, true, Metadata.empty()),
                                new StructField("age", DataTypes.IntegerType, true, Metadata.empty()),
                                new StructField("money", DataTypes.IntegerType, true, Metadata.empty()),
                        });
		
        datasetProfiler.registerDataset("people", 
        		String.format(
                "src%stest%sresources%speople.json", File.separator, File.separator, File.separator), 
        		schema);
        
        datasetProfiler.identifyPatternHighlights();
        
	}

}
