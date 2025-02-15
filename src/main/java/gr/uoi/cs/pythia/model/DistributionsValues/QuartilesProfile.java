package gr.uoi.cs.pythia.model.DistributionsValues;

public class QuartilesProfile {

    private final Number Q1;
    private final Number Q2;
    private final Number Q3;

    public QuartilesProfile(Number Q1, Number Q2, Number Q3) {
        this.Q1 = Q1;
        this.Q2 = Q2;
        this.Q3 = Q3;

    }

    public Number getQ1() {
        return Q1;

    }
    public Number getQ2() {
        return Q2;

    }
    public Number getQ3() {
        return Q3;

    }


    @Override
    public String toString() {
        String sb = "QuartilesProfile{"+"Q1="+Q1+
                ", Q2="+Q2+
                ", Q3="+Q3+
                '}';
        return sb;
    }
}
