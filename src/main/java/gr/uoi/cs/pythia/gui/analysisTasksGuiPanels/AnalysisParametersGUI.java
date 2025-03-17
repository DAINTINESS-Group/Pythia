package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AnalysisParametersGUI<T> extends JPanel {

	protected final Map<String, JComponent> inputFields = new LinkedHashMap<>(); // Preserve order
    protected final JTextArea resultArea;
    protected final AnalysisTabsPanel tabsGUI;
    private final ParameterValidator<T> validator;
    private final JPanel mainCardPanel;

    public AnalysisParametersGUI(String analysisName, AnalysisTabsPanel tabsGUI, ParameterValidator<T> validator, Consumer<T> parameterSetter, JPanel cardPanel,CardLayout cardLayout) {
        this.tabsGUI = tabsGUI;
        // Callback to set parameters in AppController
        this.validator = validator;
        this.mainCardPanel = cardPanel;


        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder(analysisName + " Parameters"));
        /*GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;*/
        //int row = 0;

        // Input fields will be added here dynamically

        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Results"));
        add(scrollPane, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            T parameters = getParameters(); // Validation happens here
            if (parameters != null) { // Only proceed if validation passes
                parameterSetter.accept(parameters);

                if(this instanceof ReportGeneratorGUI || this instanceof DatasetWriterGUI){
                    cardLayout.show(mainCardPanel, "navigationPanel");

                }
                if (tabsGUI != null) {
                    tabsGUI.removeTab(analysisName);
                }
            }
        });
    }

    public void addInputField(String label, JComponent component) {
        JPanel inputPanel = (JPanel) getComponent(0); // Get the input panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = inputFields.size();
        gbc.gridx = 0;
        gbc.gridy = row;
        inputPanel.add(new JLabel(label + ":"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputFields.put(label, component);
        inputPanel.add(component, gbc);

        // Add listener for live updates
        if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { updateResultArea(); }
                @Override public void removeUpdate(DocumentEvent e) { updateResultArea(); }
                @Override public void changedUpdate(DocumentEvent e) { updateResultArea(); }
            });
        } else if (component instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            comboBox.addActionListener(e -> updateResultArea());
        }
    }

    protected T getParameters() {
        return validator.validateAndCreate(inputFields);
    }


    protected abstract void updateResultArea();

    public Map<String, JComponent> getInputFields(){
        return inputFields;
    }

    public JTextArea getResultArea(){
        return resultArea;
    }


}