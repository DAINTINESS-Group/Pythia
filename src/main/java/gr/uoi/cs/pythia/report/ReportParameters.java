package gr.uoi.cs.pythia.report;

public class ReportParameters{
    public final String type;
    public final String path;

    public ReportParameters(String type, String path) {
        this.type = type;
        this.path = path;
    }

    @Override
    public String toString() {
        return "Type: " + type + "\nPath: " + path;
    }
}