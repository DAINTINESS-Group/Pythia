package gr.uoi.cs.pythia.client;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.correlations.CorrelationsMethod;
import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.histogram.generator.HistogramGeneratorType;
import gr.uoi.cs.pythia.histogram.generator.HistogramParameters;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;
import gr.uoi.cs.pythia.util.HighlightParameters;
import org.apache.log4j.Logger;
import org.apache.spark.sql.AnalysisException;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class AdultMainGui{

    private static final Logger logger = Logger.getLogger(AdultMainGui.class);

    public static void main(String[] args) throws AnalysisException, IOException{
        Instant start = Instant.now();

        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();

        String alias = "adultGui";
        String path = String.format(
                "src%stest%sresources%sdatasets%sAdult100K.csv",
                File.separator, File.separator, File.separator, File.separator);


        AppController.getInstance().setProfiler(datasetProfiler);
        datasetProfiler.registerDataset(alias, path);

        datasetProfiler.declareDominanceParameters(
                DominanceColumnSelectionMode.USER_SPECIFIED_ONLY,
                new String[] {"hours_per_week"},
                new String[] {"native_country", "occupation", "gender"}
        );

        datasetProfiler.declareHistogramParameters(new HistogramParameters(HistogramGeneratorType.KEEP_NANS,10));
        datasetProfiler.declareCorrelationsParameters(CorrelationsMethod.PEARSON);

        boolean shouldRunDescriptiveStats = true;
        boolean shouldRunHistograms = true;
        boolean shouldRunAllPairsCorrelations = true;
        boolean shouldRunDecisionTrees = false;
        boolean shouldRunDominancePatterns = false;
        boolean shouldRunOutlierDetection = false;
        boolean shouldRunRegression = false;
        boolean shouldRunClustering = false;
        HighlightParameters highlightParameters = new HighlightParameters(HighlightParameters.HighlightExtractionMode.NONE, Double.MAX_VALUE);

        datasetProfiler.computeProfileOfDataset(
                new DatasetProfilerParameters(
                        "results",
                        shouldRunDescriptiveStats,
                        shouldRunHistograms,
                        shouldRunAllPairsCorrelations,
                        shouldRunDecisionTrees,
                        shouldRunDominancePatterns,
                        shouldRunOutlierDetection,
                        shouldRunRegression,
                        shouldRunClustering,
                        highlightParameters));

        datasetProfiler.generateReport(ReportGeneratorConstants.TXT_REPORT, "");
        datasetProfiler.generateReport(ReportGeneratorConstants.MD_REPORT, "");

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        logger.info(String.format("Total execution time: %s / %sms", duration, duration.toMillis()));
        System.exit(0);
    }


}

