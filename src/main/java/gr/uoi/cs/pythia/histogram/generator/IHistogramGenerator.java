package gr.uoi.cs.pythia.histogram.generator;

import gr.uoi.cs.pythia.model.histogram.Histogram;

public interface IHistogramGenerator {

    Histogram generateHistogram(int bins);
}
