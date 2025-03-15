package gr.uoi.cs.pythia.writer;

public class DatasetWriterParameters{
    public final String alias;
    public final String writerType;
    public final String path;

    public DatasetWriterParameters(String alias, String writerType, String path) {
        this.alias = alias;
        this.writerType = writerType;
        this.path = path;
    }

    @Override
    public String toString() {
        return "Alias: " + alias + "\nWriter Type: " + writerType + "\nPath: " + path;
    }
}