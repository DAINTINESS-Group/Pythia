package gr.uoi.cs.pythia.generalinfo;

/**
 * This interface defines methods for calculating basic information
 * about a dataset.
 */
public interface IBasicInfoCalculator {
    /**
     * Calculates the number of lines in the entire dataset.
     */
    void calculateNumberOfLinesInDataset();
    /**
     * Calculates the size of the dataset file.
     */
    void calculateFileSize();
    /**
     * Returns the number of lines in the dataset.
     *
     * @return The number of lines
     */
    long getNumberOfLines();
    /**
     * Returns the size of the dataset file.
     *
     * @return The size of the file (in Megabytes)
     */
    Double getFileSize();

}
