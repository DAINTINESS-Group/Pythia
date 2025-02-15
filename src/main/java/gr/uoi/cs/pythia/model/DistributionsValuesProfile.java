package gr.uoi.cs.pythia.model;

import gr.uoi.cs.pythia.model.DistributionsValues.Mode;
import gr.uoi.cs.pythia.model.DistributionsValues.QuartilesProfile;

import java.util.List;

public class DistributionsValuesProfile {


    private  List<Mode> mode;
    private final QuartilesProfile quartileProfile;

    public DistributionsValuesProfile(QuartilesProfile diagram, List<Mode> mode) {
        this.quartileProfile= diagram;
        this.mode = mode;
    }
    public QuartilesProfile getQuartileProfile() {
        return quartileProfile;
    }

    public void setMode(List<Mode> mode){
        this.mode=mode;
    }
    public List<Mode> getMode() {
        return mode;
    }
    @Override
    public String toString() {
        return "DistributionsValuesProfile{"+"mode="+mode+
                ", diagram="+quartileProfile+
                '}';
    }


}
