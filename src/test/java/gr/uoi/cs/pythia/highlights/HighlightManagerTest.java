package gr.uoi.cs.pythia.highlights;

import java.util.List;
import static org.junit.Assert.assertEquals;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Test;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.highlights.HighlightsProfile;
import gr.uoi.cs.pythia.model.highlights.HolisticHighlight;
import gr.uoi.cs.pythia.patterns.AllPatternTests;


public class HighlightManagerTest {

	@Test
    public void fullHHProductionHappyPath() {
		Dataset<Row> dataset = AllPatternTests.patternsResource.getDataset();
		DatasetProfile datasetProfile = AllHighlightsTests.highlightsResource.getDatasetProfile();
		HighlightsProfile highlightProfile = datasetProfile.getHighlightsProfile();
		
		List<HolisticHighlight> actualDescriptiveStatsHHs = highlightProfile.getDescriptiveStatsHHs();
		int expectedDescrStatsSize = 60; int actualDescrStatsSize = actualDescriptiveStatsHHs.size(); 
		
		List<HolisticHighlight> actualHistogramsHHs = highlightProfile.getHistogramsHHs();
		int expectedHistoNum = 5; int actualHistoNum = actualHistogramsHHs.size();
		
		List<HolisticHighlight> actualCorrelationsHHs = highlightProfile.getCorrelationsHHs();
		int expectedCorrelNum = 20; int actualCorrelNum = actualCorrelationsHHs.size();	//computes both corr(x,y) and corr(y,x)

		List<HolisticHighlight> actualDecisionTreesHHs = highlightProfile.getDecisionTreesHHs();
		int expectedDecTreeNum = 0; int actualDecTreeNum = actualDecisionTreesHHs.size();	
		
		
		List<HolisticHighlight> actualOutliersHHs = highlightProfile.getOutliersHHs();
		int expectedZscoreNum = 131; int actualZScoreNum = actualOutliersHHs.size();	
		
		
		//List<HolisticHighlight> actualSelectedHHs = highlightProfile.getSelectedHHs();
		
		assertEquals(expectedDescrStatsSize, actualDescrStatsSize);
		assertEquals(expectedHistoNum, actualHistoNum);
		assertEquals(expectedCorrelNum, actualCorrelNum);
		assertEquals(expectedDecTreeNum, actualDecTreeNum);
		assertEquals(expectedZscoreNum, actualZScoreNum);
		
		//System.out.println("\n\n");
		//report(actualDescriptiveStatsHHs, "DESCR STATS");
		//report(actualHistogramsHHs, "HISTOGRAMS");
		//report(actualCorrelationsHHs, "CORRELATIONS");
		//report(actualDecisionTreesHHs, "DEC TREES");
		//report(actualOutliersHHs, "OUTLIERS");			
		////report(actualSelectedHHs, "CORRELATIONS");
	}
	
	
	private void report(List<HolisticHighlight> list, String title) {
		System.out.println("\n ####################\t" + title + "\t#################### \n");
		for(HolisticHighlight hh: list) {
			System.out.println(hh.toString());
		}
	}
}//end class
