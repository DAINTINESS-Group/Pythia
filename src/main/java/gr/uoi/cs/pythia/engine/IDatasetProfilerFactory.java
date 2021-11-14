package gr.uoi.cs.pythia.engine;

public class IDatasetProfilerFactory {

  public static IDatasetProfiler createDatasetProfiler() {
    return new DatasetProfiler();
  }
}
