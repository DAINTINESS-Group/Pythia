package gr.uoi.cs.pythia.valueDistributionStatistics;

import gr.uoi.cs.pythia.model.DistributionsValues.Mode;
import gr.uoi.cs.pythia.model.DistributionsValues.QuartilesProfile;
import gr.uoi.cs.pythia.model.histogram.Histogram;

import java.util.List;

public interface IValueDistributionsTasks{

    void calculateMode();

    void createQuartilesProfile();

    void createHistogramForQuartiles();

    List<Mode> getListModes();

    Histogram getHistogram();

}