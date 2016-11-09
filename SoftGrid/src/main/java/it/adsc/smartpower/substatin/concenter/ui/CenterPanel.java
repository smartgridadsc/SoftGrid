package it.adsc.smartpower.substatin.concenter.ui;

import com.l2fprod.common.propertysheet.AbstractProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import it.adsc.smartpower.substatin.concenter.Experiment;
import it.adsc.smartpower.substatin.concenter.MainPanel;
import it.adsc.smartpower.substatin.concenter.service.ISoftGridService;
import it.adsc.smartpower.substatin.concenter.service.ServiceFactory;
import it.illinois.adsc.ema.common.webservice.*;
import it.illinois.adsc.ema.pw.ConfigReader;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.ExceptionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by prageethmahendra on 30/8/2016.
 */
public class CenterPanel extends JPanel implements ExperimentListener, PropertyChangeListener, ActionListener {

    private static CenterPanel instance;
    private JScrollPane scrollPane = new JScrollPane();
    private JTextArea textArea = new JTextArea();
    private JPanel topPanel = new JPanel();
    private PropertySheetPanel propertySheetPanel = new PropertySheetPanel();
    private Properties expConfigProperties = null;
    private OperatorPanel operatorPanel;
    private JSplitPane splitPane = new JSplitPane();
    private JButton commandButton = new JButton();
    private JTextField commandFeild = new JTextField();
    private JLabel commandLabel = new JLabel("Command");
    private JPanel commandPanel = new JPanel();
    private Experiment experiment;
    private boolean serverStarted = false;

    public CenterPanel(Experiment experiment) {
        super();
        this.experiment = experiment;
        try {
            setupGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() throws Exception {
        this.setLayout(new GridBagLayout());
        topPanel.setLayout(new GridBagLayout());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LeftPanel.getInstance().addExperimentListener(CenterPanel.this);
            }
        });
        commandPanel.setLayout(new GridBagLayout());
        scrollPane.getViewport().add(textArea, null);
        commandButton.setPreferredSize(new Dimension(28, 28));
        commandButton.setMinimumSize(new Dimension(28, 28));
        commandButton.setMaximumSize(new Dimension(28, 28));

        commandFeild.setPreferredSize(new Dimension(100, 25));
        commandFeild.setMinimumSize(new Dimension(100, 25));
        commandFeild.setMaximumSize(new Dimension(100, 25));
        Image img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("execute-xxl.png"))).getImage();
//        Image img = new ImageIcon("execute-xxl.png").getImage();
        Image newimg = img.getScaledInstance(23, 23, java.awt.Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(newimg);
        commandButton.setIcon(icon);

        commandPanel.add(commandLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        commandPanel.add(commandFeild, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        commandPanel.add(commandButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 0, 3, 3), 0, 0));
        commandPanel.add(scrollPane, new GridBagConstraints(0, 1, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));


        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(commandPanel);
        splitPane.setDividerLocation(185);
        operatorPanel = new OperatorPanel(experiment);
        topPanel.add(operatorPanel, new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(propertySheetPanel, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(splitPane, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        propertySheetPanel.addPropertySheetChangeListener(this);
        commandButton.addActionListener(this);
        commandFeild.addActionListener(this);
        textArea.setLineWrap(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == commandButton || e.getSource() == commandFeild) {
            handleCommand();
        }
    }

    private void handleCommand() {
        final String command = commandFeild.getText().trim();
        boolean scriptCommand = false;
        if (command.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid command...!");
            return;
        }
        if (LeftPanel.getInstance().getSelectedExperiment() == null) {
            JOptionPane.showMessageDialog(this, "Please select an experiment project...!");
            return;
        }
        final Experiment experiment = LeftPanel.getInstance().getSelectedExperiment();
        if (command.equalsIgnoreCase("run script")) {
            scriptCommand = true;
        }


        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    int port = ConfigUtil.GATEWAY_CC_PORT;
                    String ip = ConfigUtil.IP;
                    if (command.contains(">") && command.split(">").length >= 2) {
                        try {
                            ip = command.split(">")[1].trim().split(":")[0].trim();
                            port = Integer.parseInt(command.split(">")[1].trim().split(":")[1].trim());
                        } catch (Exception e) {
                            System.out.println("Invalid Command...!");
                            e.printStackTrace();
                            return null;
                        }
                    }
                    final ISoftGridService softGridService = ServiceFactory.getServiceConnection();
                    final ExperimentRequest experimentRequest = new ExperimentRequest();
                    experimentRequest.setEntity("CC");
                    MainPanel.getInstance().getCenterPanel().logMessage("Checking Control Center Status...!");
                    experimentRequest.setGatewayPort(port);
                    experimentRequest.setGatewayIP(ip);
                    ExperimentStatus experimentStatus = getCCStatus(experimentRequest, true);
                    if (experimentStatus != ExperimentStatus.STARTED) {
                        MainPanel.getInstance().getCenterPanel().logMessage("Control Center Status = " + experimentStatus.name());
                        MainPanel.getInstance().getCenterPanel().logMessage("Starting Control Center...!");
                        experimentRequest.setExperimentType(ExperimentType.SETUP);
                        experimentRequest.setServerName("My Service");
                        ExperimentResponse experimentResponse = softGridService.changeExperimentState(experimentRequest);
                        MainPanel.getInstance().getCenterPanel().logMessage("experimentResponse = " + experimentResponse);
                        MainPanel.getInstance().getCenterPanel().logMessage("Starting", false);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            runStatusWorker(experimentRequest, true);
                        }
                    });
                    int count = 0;
                    while (!serverStarted) {
                        count++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        if (count == 50) {
                            break;
                        }
                    }

                    if (experiment != null && command.equalsIgnoreCase("run script")) {
                        MainPanel.getInstance().getCenterPanel().logMessage("Transfering Script File : " + experiment.getScriptFilePath());
                        TransferResults transferResults = softGridService.transferFile(experiment.getScriptFilePath(), FileType.COMMAND_FILE);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    experimentRequest.setExperimentType(ExperimentType.RUN);
                    experimentRequest.setCommand(command);
                    experimentRequest.setGatewayIP(ip);
                    experimentRequest.setGatewayPort(port);
                    ExperimentResponse experimentResponse = softGridService.changeExperimentState(experimentRequest);
                    MainPanel.getInstance().getCenterPanel().logMessage("Command :" + commandFeild.getText());
                    MainPanel.getInstance().getCenterPanel().logMessage("Command Executed...!");

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return null;
            }
        };
        swingWorker.execute();
    }

    private void runStatusWorker(final ExperimentRequest experimentRequest,
                                 final boolean started) {
        SwingWorker statusThread = new SwingWorker() {
            @Override
            public Object doInBackground() {

                int attempt = 1;
                while (true) {
                    ExperimentStatus experimentStatus = getCCStatus(experimentRequest, started);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (experimentStatus == null) {
                        if (attempt == 5) {
                            MainPanel.getInstance().getCenterPanel().logMessage("ERROR : in status check.");
                        }
                        attempt++;
                    } else {
                        if (experimentStatus == (started ? ExperimentStatus.STARTED : ExperimentStatus.STOPED)) {
                            MainPanel.getInstance().getCenterPanel().logMessage("\nControl Center " + (started ? "Started" : "Stoped"));
                            serverStarted = true;
                            break;
                        }
                    }
                }
                return null;
            }
        };
        statusThread.execute();
    }

    private ExperimentStatus getCCStatus(ExperimentRequest experimentRequest, final boolean started) {
        MainPanel.getInstance().getCenterPanel().logMessage(".", false);
        experimentRequest.setExperimentType(ExperimentType.CHECK);
        ISoftGridService softGridService = ServiceFactory.getServiceConnection();
        ExperimentResponse experimentResponse = softGridService.changeExperimentState(experimentRequest);
        return experimentResponse == null ? null : experimentResponse.getExperimentStatus();
    }

    public void setupConfigPanel(File file) {
        clearConfig();
        expConfigProperties = ConfigReader.getAllProperties(file);
        if (expConfigProperties != null) {
            Property[] property = new Property[expConfigProperties.size()];
            int count = 0;
            for (Object key : expConfigProperties.keySet()) {
                String keyString = key.toString();
                String value = expConfigProperties.getProperty(keyString);
                property[count] = new ExperimentProperty(keyString, value);
                count++;
            }
            propertySheetPanel.setProperties(property);
        }
        experiment.setProperties(expConfigProperties);
    }

    private void clearConfig() {
        expConfigProperties = new Properties();
        propertySheetPanel.setProperties(new Property[0]);
    }

    @Override
    public void experimentSelected(Experiment experiment) {
        if (experiment.getConfigPath() != null) {
            setupConfigPanel(new File(experiment.getConfigPath()));
        } else {
            clearConfig();
        }
    }

    @Override
    public void experimentDeleted(Experiment experiment) {
        clearConfig();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        saveProperties((AbstractProperty) evt.getSource());
    }

    private void saveProperties(AbstractProperty source) {
        FileOutputStream fo = null;
        try {
            expConfigProperties.put(source.getName(), source.getValue());
            fo = new FileOutputStream(LeftPanel.getInstance().getSelectedExperiment().getConfigFilePath());
            expConfigProperties.store(fo, "");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fo != null) {
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void logMessage(String str) {
        logMessage(str, true);
    }

    public void logMessage(String str, boolean newLine) {
        textArea.append(str + (newLine ? "\n" : ""));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        });

    }
}


class ExperimentProperty extends AbstractProperty {
    private String name = "";
    private String category = "";

    public ExperimentProperty(String name, String value) {
        this.setValue(value);
        this.setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getShortDescription() {
        return name;
    }

    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void readFromObject(Object o) {
    }

    @Override
    public void writeToObject(Object o) {
    }
}
