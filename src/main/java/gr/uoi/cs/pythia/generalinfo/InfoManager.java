package gr.uoi.cs.pythia.generalinfo;

import gr.uoi.cs.pythia.model.DatasetProfile;

public class InfoManager {
    private final IBasicInfoCalculator infoCalculator;
    private final DatasetProfile datasetProfile;
    public InfoManager(DatasetProfile datasetProfile, IBasicInfoCalculator infoCalculator) {
        this.datasetProfile = datasetProfile;
        this.infoCalculator = infoCalculator;
    }

    public int runAllCalculations() {
        try {
            calculateNumberOfLines();
            calculateFileSize();
            return 1;
        }catch (NullPointerException e ){
            System.out.println("DatasetProfile is Null!!\nI cant update number of lines\nOR"+"DatasetProfile is Null!!\nI cant update filesize");
            return 0;
        }
    }

    private void calculateNumberOfLines() {
       // try{
            infoCalculator.calculateNumberOfLinesInDataset();
            datasetProfile.setNumberOfLines(infoCalculator.getNumberOfLines());
      //  }catch (NullPointerException e){
           // System.out.println("DatasetProfile is Null!!\nI cant update number of lines");
     //   }
    }
    private void calculateFileSize() {
     //   try{
            infoCalculator.calculateFileSize();
            datasetProfile.setFileSize(infoCalculator.getFileSize());
      //  }catch (NullPointerException e){
       //     System.out.println("DatasetProfile is Null!!\nI cant update filesize");
        //}
    }
}
