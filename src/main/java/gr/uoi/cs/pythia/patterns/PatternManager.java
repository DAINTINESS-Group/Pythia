package gr.uoi.cs.pythia.patterns;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import gr.uoi.cs.pythia.patterns.dominance.*;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.dominance.DominanceResult;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.patterns.outlier.IOutlierAlgo;
import gr.uoi.cs.pythia.patterns.outlier.OutlierAlgoFactory;

import static gr.uoi.cs.pythia.patterns.dominance.DominanceAlgoFactory.DominanceAlgoVersion.OPTIMIZED_HIGH;
import static gr.uoi.cs.pythia.patterns.dominance.DominanceAlgoFactory.DominanceAlgoVersion.OPTIMIZED_LOW;

public class PatternManager implements IPatternManager {

  private final Logger logger = Logger.getLogger(PatternManager.class);
  private final Dataset<Row> dataset;
  private final DatasetProfile datasetProfile;
  private final DominanceParameters dominanceParameters;
  private final OutlierType outlierType;

  private IDominanceAlgo highDominanceAlgo;
  private IDominanceAlgo lowDominanceAlgo;
  private IOutlierAlgo outlierAlgo;

  public PatternManager(
          Dataset<Row> dataset,
          DatasetProfile datasetProfile,
          DominanceParameters dominanceParameters,
          OutlierType outlierType,
          double outlierThreshold) {
    this.dataset = dataset;
    this.datasetProfile = datasetProfile;
    this.dominanceParameters = dominanceParameters;
    this.outlierType = outlierType;
    outlierAlgo = new OutlierAlgoFactory()
            .createOutlierAlgo(outlierType, outlierThreshold);
    initializePatternAlgos();
  }

  private void initializePatternAlgos() {
      // Default to the optimized dominance algo versions
      DominanceAlgoFactory factory = new DominanceAlgoFactory();
      highDominanceAlgo = factory.generateDominanceAlgo(OPTIMIZED_HIGH, dataset);
      lowDominanceAlgo = factory.generateDominanceAlgo(OPTIMIZED_LOW, dataset);
  }

 
  @Override
  public void identifyOutliers() {
	  List<OutlierResult> outlierResults = outlierAlgo.identifyOutliers(dataset,datasetProfile);
	  
	  datasetProfile.getPatternsProfile().setOutlierResults(outlierResults);
	  
	  datasetProfile.getPatternsProfile().setOutlierType(outlierAlgo.getOutlierType().replace("_", " "));
	  
	  logger.info(String.format(
			  "Identified outliers using the \"%s\" outlier type for dataset: '%s'",
			  outlierType, datasetProfile.getAlias()));
  }
  
  @Override
  public void identifyDominancePatterns() {
	Instant start = Instant.now();
	
    DominanceColumnSelector columnSelector = new DominanceColumnSelector(dominanceParameters);

    // Select the measurement & coordinate columns for dominance highlight identification
    List<String> measurementColumns = columnSelector.selectMeasurementColumns(datasetProfile);
    List<String> coordinateColumns = columnSelector.selectCoordinateColumns(datasetProfile, dataset);

    // Debug prints
    logger.info(String.format("Selected measurement columns: %s", measurementColumns));
    logger.info(String.format("Selected coordinate columns: %s", coordinateColumns));
    
    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    logger.info(String.format("Duration of dominance column selection: %s / %sms", 
    		duration, duration.toMillis()));
	  
    // Highlight identification can not proceed with no measurement/coordinate
    if (measurementColumns.isEmpty()) return;
    if (coordinateColumns.isEmpty()) return;

    List<DominanceResult> highDominanceResults = new ArrayList<>();
    List<DominanceResult> lowDominanceResults = new ArrayList<>();

    // Pass all the measurement & coordinate column combinations
    // through low & high dominance identification algorithms
    // for one & two coordinates respectively.
    identifyDominanceWithOneCoordinate(measurementColumns, coordinateColumns,
            highDominanceResults, lowDominanceResults);
    identifyDominanceWithTwoCoordinates(measurementColumns, coordinateColumns,
            highDominanceResults, lowDominanceResults);

    // Add results to patterns profile
    datasetProfile.getPatternsProfile().setHighDominanceResults(highDominanceResults);
    datasetProfile.getPatternsProfile().setLowDominanceResults(lowDominanceResults);

    logger.info(String.format(
            "Identified dominance highlight patterns for dataset: '%s'", datasetProfile.getAlias()));
  }

  private void identifyDominanceWithOneCoordinate(
          List<String> measurementColumns, List<String> coordinateColumns,
          List<DominanceResult> highDominanceResults,
          List<DominanceResult> lowDominanceResults) {
	  Instant start = Instant.now();
	  
	  for (String measurement : measurementColumns) {
		  for (String xCoordinate : coordinateColumns) {
			  highDominanceResults.add(highDominanceAlgo
					  .identifyDominanceWithOneCoordinate(measurement, xCoordinate));
			  lowDominanceResults.add(lowDominanceAlgo
					  .identifyDominanceWithOneCoordinate(measurement, xCoordinate));
		  }
	  }
	  
	  Instant end = Instant.now();
	  Duration duration = Duration.between(start, end);
	  logger.info(String.format("Duration of identifyDominanceWithOneCoordinate: %s / %sms", 
			  duration, duration.toMillis()));
  }

  private void identifyDominanceWithTwoCoordinates(
          List<String> measurementColumns, List<String> coordinateColumns,
          List<DominanceResult> highDominanceResults,
          List<DominanceResult> lowDominanceResults) {
	  	Instant start = Instant.now();
	  	
        for (String measurement : measurementColumns) {
          for (String xCoordinate : coordinateColumns) {
            for (String yCoordinate : coordinateColumns) {
              if (xCoordinate.equals(yCoordinate)) continue;
              highDominanceResults.add(highDominanceAlgo
                      .identifyDominanceWithTwoCoordinates(
                              measurement, xCoordinate, yCoordinate));
              lowDominanceResults.add(lowDominanceAlgo
                      .identifyDominanceWithTwoCoordinates(
                              measurement, xCoordinate, yCoordinate));
            }
          }
        }
        
        Instant end = Instant.now();
  	  	Duration duration = Duration.between(start, end);
  	  	logger.info(String.format("Duration of identifyDominanceWithTwoCoordinates: %s / %sms", 
  	  			duration, duration.toMillis()));
  }
}
