package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.client.Patterns;
import gr.uoi.cs.pythia.config.AnalysisParameters;
import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.outlier.IOutlierAlgo;
import gr.uoi.cs.pythia.patterns.outlier.OutlierAlgoFactory;
import gr.uoi.cs.pythia.patterns.outlier.OutlierResult;
import gr.uoi.cs.pythia.patterns.outlier.OutlierType;
import gr.uoi.cs.pythia.reader.IDatasetReaderFactory;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;

public class ZScoreOutlierAlgoTests {

	private IOutlierAlgo zScoreOutlierAlgo;
	private Dataset<Row> dataset;
	private  DatasetProfile datasetProfile;
	
	// TODO not sure if we need this - to be discussed
	private AnalysisParameters analysisParameters;
	
	@Before
	public void init() throws AnalysisException, IOException {
		// Load the cars dataset (100 records version)
		StructType schema = Patterns.createCarsDatasetSchema();
		String path = String.format("src%stest%sresources%sdatasets%scars_100.csv",
				File.separator, File.separator, File.separator, File.separator);
		SparkConfig sparkConfig = new SparkConfig();
		
		// Create dataset object
		dataset = new IDatasetReaderFactory(
			SparkSession.builder()
				.appName(sparkConfig.getAppName())
                .master(sparkConfig.getMaster())
                .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
                .getOrCreate())
			.createDataframeReader(path, schema).read();
		
		// Create datasetProfile object such that descriptive statistics are calculated
		// and they can be used by z score algo.
		IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
	    datasetProfiler.registerDataset("cars", path, schema);
	    
		// TODO identify highlight patterns (including outlier detection) will eventually be 
		// performed internally within computeProfileOfDataset 
	    // so we can't call this method here - how do we solve this?
	    datasetProfile = datasetProfiler.computeProfileOfDataset(
				TestsUtilities.getResultsDir("patterns"));
		
		// Initialize outlier algo
		zScoreOutlierAlgo = new OutlierAlgoFactory().createOutlierAlgo(OutlierType.Z_SCORE);
	}
	
	@Test
	public void testIdentifyOutliers() {
		List<OutlierResult> expected =  createExpectedZScoreOutlierResults();
		zScoreOutlierAlgo.identifyOutliers(dataset, datasetProfile, analysisParameters);
		List<OutlierResult> actual = zScoreOutlierAlgo.getResults();
		
		for (int i=0; i<actual.size(); i++) {
			assertEquals(expected.get(i).getColumnName(), actual.get(i).getColumnName());
			assertEquals(expected.get(i).getValue(), actual.get(i).getValue());
			assertEquals(expected.get(i).getScore(), actual.get(i).getScore());
			assertEquals(expected.get(i).getPosition(), actual.get(i).getPosition());
		}
	}

	private List<OutlierResult> createExpectedZScoreOutlierResults() {
		return Arrays.asList(
				new OutlierResult("mileage", 97440.0, 3.1075171338770025, 67),
				new OutlierResult("engineSize", 3.0, 3.0317626369169393, 23),
				new OutlierResult("engineSize", 3.0, 3.0317626369169393, 24),
				new OutlierResult("engineSize", 3.0, 3.0317626369169393, 50),
				new OutlierResult("engineSize", 3.0, 3.0317626369169393, 93)
				);
	}
}
