package gr.uoi.cs.pythia.gui.guiButtonPanels;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame implements NavigationListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -338386314255299400L;
	private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final ApplicationNavigationPanel navigationPanel;
    private static MainWindow mainWindow ;

    public MainWindow() {
        mainWindow =this;
        setTitle("Pythia Data Profiling");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLayout(new BorderLayout());

        //navigationPanel = new ApplicationNavigationPanel();
        cardPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cardPanel.getLayout();
        cardPanel.add(new PlaceholderPanel(), "placeholder");
        navigationPanel = new ApplicationNavigationPanel(cardPanel,cardLayout);

        add(navigationPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public void showCard(Component component, String cardName) {
        cardPanel.add(component, cardName);
        cardLayout.show(cardPanel, cardName);
    }
    public static MainWindow getMainWindow() {
        if(mainWindow == null) {
            mainWindow = new MainWindow();
        }
        return mainWindow;

    }
    public /*Container*/JPanel getCardPanel(){
        return cardPanel;
    }
    public CardLayout getCardLayout(){
        return cardLayout;
    }



    @Override
    public void showNavigationPanel(){
        showCard(navigationPanel, "navigationPanel");
    }

    @Override
    public void setOnShowResultsButton(){
        navigationPanel.setOnShowResultsButton();
    }
    @Override
    public void setOnEditButton(){
        navigationPanel.setOnEditDatatypesButton();
    }

    public ApplicationNavigationPanel getNavigationPanel() {
        return navigationPanel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

}

