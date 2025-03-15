
package gr.uoi.cs.pythia.gui.guiButtonPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import org.apache.spark.sql.types.StructType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

public class DatasetInputPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 742659501582583104L;
	private final JTextField aliasField = new JTextField(20);
    private final JTextField pathField = new JTextField(20);
    private final JTextField pathSchemaField = new JTextField(20);

    private final JButton registerButton = new JButton("Register Dataset");
    private final JTextArea resultArea = new JTextArea(5, 20);
    public StructType currentStructType;
    private JDialog progressDialog;

    public DatasetInputPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        setupForm();
    }

    private void setupForm() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addComponent(inputPanel, "Dataset Alias:", aliasField, gbc, 0);
        addComponent(inputPanel, "Dataset Path:", createPathSelectionPanel(pathField), gbc, 1);
        addComponent(inputPanel, "Load Schema:", createPathSelectionPanel(pathSchemaField), gbc, 2);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        registerButton.addActionListener(e -> registerDataset());
        buttonPanel.add(registerButton);

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setBorder(BorderFactory.createTitledBorder("Results"));

        add(inputPanel, BorderLayout.NORTH);
        add(resultScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        DocumentListener updateListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateResultsArea(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateResultsArea(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateResultsArea(); }
        };
        aliasField.getDocument().addDocumentListener(updateListener);
        pathField.getDocument().addDocumentListener(updateListener);
        //pathSchemaField.getDocument().addDocumentListener(updateListener);
    }

    private JPanel createPathSelectionPanel(JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(textField);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(createFileChooserListener(textField));
        panel.add(browseButton);
        return panel;
    }

    private ActionListener createFileChooserListener(JTextField textField) {
        return e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        };
    }

    private void addComponent(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int gridy) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    public void registerDataset() {
        String alias = aliasField.getText().trim();
        String path = pathField.getText().trim();

        if (alias.isEmpty() || path.isEmpty()) {
            resultArea.setText("Error: Please fill all fields before registering.");
            return;
        }

        registerButton.setEnabled(false);
        SwingUtilities.invokeLater(this::showProgressDialog);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (pathSchemaField != null && !pathSchemaField.getText().trim().isEmpty()) {
                    AppController.getInstance().registerDataset(alias, path, pathSchemaField.getText().trim());
                } else {
                    AppController.getInstance().registerDataset(alias, path);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Dataset registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        hideProgressDialog();
                        MainWindow.getMainWindow().setOnEditButton();
                        MainWindow.getMainWindow().showNavigationPanel();
                    });
                } catch (InterruptedException | ExecutionException ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        hideProgressDialog();
                        registerButton.setEnabled(true);
                    });
                }
            }
        };

        worker.execute();
    }

    private void showProgressDialog() {
        progressDialog = new JDialog((Frame) null, "Registering Dataset", true);
        progressDialog.setLayout(new BorderLayout());
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressDialog.add(progressBar, BorderLayout.CENTER);
        progressDialog.setVisible(true);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isVisible()) {
            progressDialog.dispose();
        }
    }

    public void updateResultsArea() {
        String alias = aliasField.getText().trim();
        String path = pathField.getText().trim();

        //String schema = (currentStructType != null) ? currentStructType.prettyJson() : "No schema yet";

        resultArea.setText(String.format("Dataset Alias: %s\nDataset Path: %s\n", alias, path));
    }

    public JTextField getAliasField() { return aliasField; }
    public JTextField getPathField() { return pathField; }
    public JTextArea getResultArea() { return resultArea; }
    public JButton getRegisterButton() { return registerButton; }
}