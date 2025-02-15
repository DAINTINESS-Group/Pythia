package gr.uoi.cs.pythia.model.DistributionsValues;

public class Mode {


    private final Object value;
    private final long occurrences;

    public Mode(long occurrences, Object value) {
        this.occurrences = occurrences;
        this.value = value;
    }

    public long getOccurrences() {
        return occurrences;
    }

    public Object getValue() {
        return value;
    }
    @Override
    public String toString() {
        String sb = "Mode{"+"occurrences="+occurrences+
                ", value="+value+
                '}';
        return sb;
    }

}
