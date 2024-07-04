package gr.uoi.cs.pythia.model;

import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import java.util.ArrayList;
import java.util.List;

public class OutlierProfile {

    private final List<OutlierResult> outlierResultList;
    private String outlierType;
    //TODO add threshold info ???

    public OutlierProfile(List<OutlierResult> outlierResults, String outlierType){
        this.outlierResultList = outlierResults;
        this.outlierType = outlierType;
    }

    public OutlierProfile(){
        this.outlierResultList = new ArrayList<>();
        this.outlierType = "No init Outlier Type";
    }

    public String getOutlierType() {
        return outlierType;
    }
    public List<OutlierResult> getOutlierResultList() {
        return outlierResultList;
    }

    public void setOutlierType(String type) {
        this.outlierType = type;
    }

    @Override
    public String toString() {
        if(outlierResultList.isEmpty()){
            return "Not found Outliers,\n" +
                    "TypeOutlier is "+outlierType;
        }
        StringBuilder sb = new StringBuilder();
        String header = String.format("%-24s%-24s%-24s%-24s\n" ,"OutlierType", "Value", "Score", "Position");
        sb.append(header);
        for (OutlierResult outlierResult : outlierResultList) {
            sb.append(String.format("%-24s", outlierType)).append(outlierResult.toString());
        }
        return sb.toString();
    }
}
