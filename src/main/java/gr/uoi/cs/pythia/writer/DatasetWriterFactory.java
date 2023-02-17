package gr.uoi.cs.pythia.writer;

public class DatasetWriterFactory {

  public IDatasetWriter createDatasetWriter(String type) {
    switch (type) {
      case DatasetWriterConstants.HADOOP:
        return new HadoopDatasetWriter();
      case DatasetWriterConstants.NAIVE:
        return new NaiveDatasetWriter();
    }
    throw new IllegalArgumentException(
        String.format("Writer %s is not a valid writer type.", type));
  }
}
