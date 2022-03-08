package gr.uoi.cs.pythia.engine;

public class IDatasetProfilerFactory {

  public IDatasetProfiler createDatasetProfiler() {
    return new DatasetProfiler();
  }
}
