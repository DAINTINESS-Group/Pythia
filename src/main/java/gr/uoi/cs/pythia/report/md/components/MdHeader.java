package gr.uoi.cs.pythia.report.md.components;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.report.md.structures.MdBasicStructures;


public class MdHeader {

    private final String datasetAlias;
    private final DatasetProfile datasetProfile;

    public MdHeader(String datasetAlias,DatasetProfile datasetProfile) {
        this.datasetAlias = datasetAlias;
        this.datasetProfile = datasetProfile;
    }
    // TODO: add more info of which dataset, author etc, after the title
    @Override
    public String toString() {
        return getLogoImage() + "\n" +
               getTitle() + "\n"+
                getDescription()+"\n";
    }

    private String getLogoImage() {
        String logoUrl = "https://drive.google.com/uc?export=view&id=1IrWn72NN5KQ5CIQn9YVKwxGompI0RFPl";
        return MdBasicStructures.center(MdBasicStructures.image(logoUrl, "Pythia logo"));
    }

    private String getTitle() {
        String text = String.format("Statistical Profile of \"%s\"",  datasetAlias);
        return MdBasicStructures.center(MdBasicStructures.heading1(text));
    }

    private String getDescription() {
        String pathText = String.format("**Path:** %s<br>", datasetProfile.getPath());
        String linesText = String.format("**Number of Lines:** %d<br>", datasetProfile.getNumberOfLines());
        String sizeText = String.format("**File Size:** %s MB<br>", datasetProfile.getFileSize());
        String timestampText = String.format("**Timestamp:** %s<br>", datasetProfile.getTimestamp() +" "+datasetProfile.getZoneId());

        String description = pathText + "\n" +
                linesText + "\n" +
                sizeText + "\n" +
                timestampText + "\n";

        return MdBasicStructures.center(description);
    }



}
