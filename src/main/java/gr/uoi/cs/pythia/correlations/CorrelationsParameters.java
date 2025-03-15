package gr.uoi.cs.pythia.correlations;

public class CorrelationsParameters{
    public final CorrelationsMethod method;

    public CorrelationsParameters(CorrelationsMethod method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "Method: " + method;
    }
}