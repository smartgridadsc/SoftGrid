package it.adsc.smartpower.substatin.concenter.ui;

import it.adsc.smartpower.substatin.concenter.ControlCenterWindow;
import it.adsc.smartpower.substatin.concenter.Experiment;
import it.adsc.smartpower.substatin.concenter.MainPanel;
import it.adsc.smartpower.substatin.concenter.service.ISoftGridService;
import it.adsc.smartpower.substatin.concenter.service.ServiceFactory;
import it.illinois.adsc.ema.common.file.FileProcessor;
import it.illinois.adsc.ema.common.webservice.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by prageethmahendra on 31/8/2016.
 */
public class OperatorPanel extends JPanel implements ActionListener {
    private JButton monitorButton = new JButton("");
    private JButton stopButton = new JButton("");
    private JButton clearButton = new JButton("");
    private JButton runButton = new JButton("");
    private boolean serverStarted = true;
    private Experiment experiment;

    public OperatorPanel(Experiment experiment) {
        this.experiment = experiment;
        try {
            setupGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() throws Exception {
        monitorButton.setPreferredSize(new Dimension(30, 30));
        monitorButton.setMinimumSize(new Dimension(30, 30));
        monitorButton.setMaximumSize(new Dimension(30, 30));
        monitorButton.setToolTipText("Execute and Monitor");

        stopButton.setPreferredSize(new Dimension(30, 30));
        stopButton.setMinimumSize(new Dimension(30, 30));
        stopButton.setMaximumSize(new Dimension(30, 30));
        stopButton.setToolTipText("Rest Testbed");

        clearButton.setPreferredSize(new Dimension(30, 30));
        clearButton.setMinimumSize(new Dimension(30, 30));
        clearButton.setMaximumSize(new Dimension(30, 30));
        clearButton.setToolTipText("Reset");

        runButton.setPreferredSize(new Dimension(30, 30));
        runButton.setMinimumSize(new Dimension(30, 30));
        runButton.setMaximumSize(new Dimension(30, 30));
        runButton.setToolTipText("Initialize the server...!");

        Image img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("download-xxl.png"))).getImage();
//        Image img = new ImageIcon(validatedPath("download-xxl.png")).getImage();
        Image newimg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(newimg);
        monitorButton.setIcon(icon);

        img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("stop-xxl.png"))).getImage();
//        img = new ImageIcon(validatedPath("stop-xxl.png")).getImage();
        newimg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        stopButton.setIcon(icon);

        img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("reset-xxl.png"))).getImage();
//        img = new ImageIcon("..\\ControlCenter\\images\\reset-xxl.png").getImage();
        newimg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        clearButton.setIcon(icon);

        img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("start-xxl.png"))).getImage();
//        img = new ImageIcon(validatedPath("start-xxl.png")).getImage();
        newimg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        runButton.setIcon(icon);

        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEtchedBorder());
        this.setOpaque(true);
        this.setBackground(Color.gray);
        this.add(runButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 12, 3, 12), 0, 0));
        this.add(monitorButton, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 12, 3, 12), 0, 0));
//        this.add(clearButton, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 12, 3, 12), 0, 0));
        this.add(stopButton, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 12, 3, 12), 0, 0));
        this.add(new JLabel(), new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(3, 12, 3, 12), 0, 0));

        stopButton.addActionListener(this);
        monitorButton.addActionListener(this);
        clearButton.addActionListener(this);
        clearButton.setEnabled(false);
        monitorButton.setEnabled(true);
        runButton.addActionListener(this);
        stopButton.setEnabled(false);
    }

    public String validatedPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file = new File("..\\ControlCenter\\images\\" + path);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }
        return path;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stopButton) {
            SwingWorker swingWorker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    ExperimentResponse experimentResponse = null;
                    try {
                        MainPanel.getInstance().getCenterPanel().logMessage("Sending \"Stop Experiment\" Command...!");
                        ISoftGridService softGridService = ServiceFactory.getServiceConnection();
                        ExperimentRequest experimentRequest = new ExperimentRequest();
                        experimentRequest.setExperimentType(ExperimentType.RESET);
                        experimentRequest.setServerName("My Service");
                        experimentResponse = softGridService.changeExperimentState(experimentRequest);
                        if (experimentResponse != null) {
                            MainPanel.getInstance().getCenterPanel().logMessage("Valid Service Connection...!");
                        } else {
                            MainPanel.getInstance().getCenterPanel().logMessage("No Response Received...!\n" +
                                    "[ERROR]Possible cause [Server Not Started...!]");
                        }
                        serverStarted = experimentResponse == null;
                        runStatusWorker(experimentRequest, serverStarted);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    runButton.setEnabled(experimentResponse != null);
                    stopButton.setEnabled(experimentResponse == null);
                    return null;
                }
            };
            swingWorker.execute();


        } else if (e.getSource() == monitorButton) {
            downloadAllFiles();
//            executeMonitorQuery();
        } else if (e.getSource() == clearButton) {
//            SmartPowerControler.killAll();
//            if (ConfigUtil.SERVER_TYPE.equals("IED")) {
//                stopMonitor();
//            }
//            logAreaScrollPane.setVisible(true);
//            chartPanel.setVisible(false);
            runButton.setEnabled(true);
            clearButton.setEnabled(false);
        } else if (e.getSource() == runButton) {
            startIEDServer();


            // connect to the web service
            // send case file
            // download all the icd files
            // start experiment
            // check and wait till it fully starts
            // when it starts instruct the user to connect the device
            // detect the device and indicate its detection
            // start the control center client
            // provide the user to enter manual commands

            runButton.setEnabled(false);

        }
    }

    private void startIEDServer() {
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    MainPanel.getInstance().getCenterPanel().logMessage("Initializing...!");
                    MainPanel.getInstance().getCenterPanel().logMessage("Starting IEDs...!");
                    ISoftGridService softGridService = ServiceFactory.getServiceConnection();
                    ExperimentRequest experimentRequest = new ExperimentRequest();
                    experimentRequest.setExperimentType(ExperimentType.SETUP);
                    experimentRequest.setServerName("My Service");
                    TransferResults transferResults = softGridService.transferFile(experiment.getCaseFilePath(), FileType.CASE_FILE);
                    if (transferResults != null && transferResults.isSuccess()) {
                        ExperimentResponse experimentResponse = softGridService.changeExperimentState(experimentRequest);
                        MainPanel.getInstance().getCenterPanel().logMessage("experimentResponse = " + experimentResponse);
                        MainPanel.getInstance().getCenterPanel().logMessage("Starting", false);
                        serverStarted = true;
                        runStatusWorker(experimentRequest, serverStarted);
                    } else {
                        MainPanel.getInstance().getCenterPanel().logMessage("Error in uploading the case file : " + experiment.getCaseFilePath());
                        serverStarted = false;

                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        stopButton.setEnabled(serverStarted);
                        runButton.setEnabled(!serverStarted);
                    }
                });

                return null;
            }
        };
        swingWorker.execute();
    }

    private void downloadAllFiles() {
        SwingWorker statusThread = new SwingWorker() {
            @Override
            public Object doInBackground() {
                int count = 1;
                String fileName = "";
                while (true) {

                    if (experiment != null) {
                        MainPanel.getInstance().getCenterPanel().logMessage("Request for Next File. File Number : " + count + " ...!", false);
                        ISoftGridService softGridService = ServiceFactory.getServiceConnection();
                        Response response = softGridService.requestFile(fileName, FileType.ANY);
                        if (response == null ||
                                response.getStatus() != Response.Status.OK.getStatusCode()
                                || response.getHeaders().get("filename") == null) {
                            JOptionPane.showMessageDialog(ControlCenterWindow.getInstance(), "Error in downloading the log files.");
                            break;
                        }
                        fileName = response.getHeaders().get("filename").get(0).toString().trim();

                        if (fileName.isEmpty()) {
                            MainPanel.getInstance().getCenterPanel().logMessage("\nAll the log files downloaded to the below folder...!", true);
                            MainPanel.getInstance().getCenterPanel().logMessage(experiment.getDownloadPath() + "\n", true);
                            break;
                        } else {
                            MainPanel.getInstance().getCenterPanel().logMessage("File Name : " + fileName, true);
                        }
                        InputStream inputStream = (InputStream) response.getEntity();
                        if (inputStream == null) {
                            break;
                        } else {
                            File logPath = new File(experiment.getDownloadPath());
                            if (!logPath.exists()) {
                                logPath.mkdir();
                            }
                            if (logPath.isDirectory() && fileName.trim().length() > 0) {
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(new File(logPath.getAbsolutePath() + "\\" + fileName));
                                    FileProcessor.downloadFile(inputStream, fileOutputStream);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            count++;
                        }
                    }

                }
                return null;
            }

        };
        statusThread.execute();
    }

    private void runStatusWorker(final ExperimentRequest experimentRequest,
                                 final boolean started) {
        SwingWorker statusThread = new SwingWorker() {
            @Override
            public Object doInBackground() {
                int attempt = 1;
                while (true) {
                    if (!(serverStarted && started)) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    MainPanel.getInstance().getCenterPanel().logMessage(".", false);
                    experimentRequest.setExperimentType(ExperimentType.CHECK);
                    ISoftGridService softGridService = ServiceFactory.getServiceConnection();
                    ExperimentResponse experimentResponse = softGridService.changeExperimentState(experimentRequest);
                    if (experimentResponse == null) {
                        if (attempt == 5) {
                            MainPanel.getInstance().getCenterPanel().logMessage("ERROR : in status check.");
                            break;
                        }
                        attempt++;
                    } else {
                        if (experimentResponse.getExperimentStatus() == (started ? ExperimentStatus.STARTED : ExperimentStatus.STOPED)) {
                            MainPanel.getInstance().getCenterPanel().logMessage("\nAll IEDs " + (started ? "Started" : "Stoped"));
                            break;
                        }
                    }

                }
                return null;
            }

        };
        statusThread.execute();
    }
}
