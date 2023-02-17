package gr.uoi.cs.pythia.report.md.components;

import gr.uoi.cs.pythia.report.md.structures.MdBasicStructures;

public class MdHeader {

    private final String datasetAlias;

    public MdHeader(String datasetAlias) {
        this.datasetAlias = datasetAlias;
    }

    // TODO: add more info of which dataset, author etc, after the title
    @Override
    public String toString() {
        return getLogoImage() + "\n" +
               getTitle() + "\n";
    }

    private String getLogoImage() {
        String logoUrl = "https://drive.google.com/uc?export=view&id=1IrWn72NN5KQ5CIQn9YVKwxGompI0RFPl";
        return MdBasicStructures.center(MdBasicStructures.image(logoUrl, "Pythia logo"));
    }

    private String getTitle() {
        String text = String.format("Statistical Profile of \"%s\"",  datasetAlias);
        return MdBasicStructures.center(MdBasicStructures.heading1(text));
    }
}
