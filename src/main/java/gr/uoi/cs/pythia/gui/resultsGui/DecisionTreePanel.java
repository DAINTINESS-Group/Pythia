package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DecisionTreePanel extends AnalysisPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2029255171895761525L;
	private final String pathImages = AppController.getInstance().getAuxiliaryPath();
    private final List<File> listImages;

    public DecisionTreePanel() {
        super();
        listImages = new ArrayList<>();
    }

    public void findImages() {
        String alias = AppController.getInstance().getDatasetProfile().getAlias();
        String folderPattern = alias + "_results_";

        File folder = new File(pathImages);
        File[] files = folder.listFiles();

        if (files == null) {
            System.out.println("The folder does not exist or is empty.");
            return;
        }

        List<File> validFolders = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory() && file.getName().startsWith(folderPattern)) {
                validFolders.add(file);
            }
        }

        if (validFolders.isEmpty()) {
            System.out.println("No folders matching the pattern were found.");
            return;
        }


        File latestFolder = validFolders.stream()
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);

        System.out.println("The most recent folder is: " + latestFolder.getName());


        File decisionTreesFolder = new File(latestFolder, "decision_trees");

        if (!decisionTreesFolder.exists() || !decisionTreesFolder.isDirectory()) {
            System.out.println("The 'decision_trees' folder was not found.");
            return;
        }

        File[] imageFiles = decisionTreesFolder.listFiles();

        listImages.clear();

        if (imageFiles != null) {
            for (File file : imageFiles) {
                if (file.isFile() && isImage(file)) {
                    listImages.add(file);
                }
            }
        }

        if (listImages.isEmpty()) {
            System.out.println("No images were found in the 'decision_trees' folder.");
        } else {
            System.out.println("The following images were found:");
            for (File imageFile : listImages) {
                System.out.println(imageFile.getName());
            }
        }
    }

    private boolean isImage(File file) {
        String[] imageExtensions = {".jpg",".png"};
        for (String ext : imageExtensions) {
            if (file.getName().toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void createPanelContent() {
        findImages();
        this.setLayout(new BorderLayout());
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(0, 3));
        if (!listImages.isEmpty()) {
            for (File imageFile : listImages) {
                try {
                    ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());
                    Image image = imageIcon.getImage().getScaledInstance(1920, 920, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(image);

                    JLabel imageLabel = new JLabel(imageIcon);
                    imagePanel.add(imageLabel);
                } catch (Exception e) {

                    System.out.println("Error loading image: " + imageFile.getName());
                }
            }
        } else {
            imagePanel.add(new JLabel("No images found."));
        }
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
}
