/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
package it.illinois.adsc.ema.softgrid.monitoring.ui;

import com.alee.extended.layout.VerticalFlowLayout;
import com.sun.jna.Library;
import com.sun.jna.Native;
import it.illinois.adsc.ema.control.LogEventListener;
import it.illinois.adsc.ema.softgrid.monitoring.ui.config.ConfigPanel;
import it.illinois.adsc.ema.softgrid.monitoring.ui.alerts.MainAlert;
import it.illinois.adsc.ema.softgrid.monitoring.ui.message.MessageUIHandler;
import it.illinois.adsc.ema.control.SoftGridController;
import it.illinois.adsc.scl.SclGenerator;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.Timer;

/**
 * Created by prageethmahendra on 26/4/2016.
 */
public class SPMainFrame extends JFrame implements ActionListener, WindowListener, LogEventListener, IEDControler {
    private BufferedWriter dataFileWriter = null;

    private static int MAX_VISIBLE_ALERTS = 13;
    private static int ALERT_STORE_DURATION = 60000;
    private static SPMainFrame instance;

    private MessageUIHandler messageHandler;
    private ChartPanel chartPanel;
    private XYSeriesCollection dataset = new XYSeriesCollection();
    private JPanel alertPanel = new JPanel();
    private JPanel transientPanel = new JPanel();
    private JSplitPane splitPane = new JSplitPane();
    private JTabbedPane resultTabbedPane = new JTabbedPane();
    private JTabbedPane mainTabbedPane = new JTabbedPane();

    private JTextArea logTextArea = new JTextArea();
    private JScrollPane logAreaScrollPane = new JScrollPane();
    private JTextPane queryTextArea = new JTextPane();
    private JTextPane stateTextArea = new JTextPane();
    private JScrollPane altertScrolPane = new JScrollPane();
    private JScrollPane queryScrolPane = new JScrollPane();
    private JLabel alertTitile = new JLabel("Limit Violations");
    private JButton monitorButton = new JButton("");
    private JButton exitButton = new JButton("");
    private JButton clearButton = new JButton("");
    private JButton runButton = new JButton("");
    private ArrayList<MonitorConfig> monitorConfigs = new ArrayList<MonitorConfig>();
    private double startTime = 1;
    private int alertCount = 0;
    private SMAlert lastAlert = null;
    private long minTimeRange = 0;
    private long maxTimeRange = 0;
    private HashMap<SMAlert, Long> alertTimeMap = new HashMap<SMAlert, Long>();
    private Thread pythonThread = null;
    private Process pythonProcess = null;
    private int serverStatus = 0;

    private MainAlert frequencyViolation = new MainAlert("Frequency",
            String.valueOf(System.nanoTime()), "Main Frequency violation alert item");
    private MainAlert voltageViolation = new MainAlert("Voltage",
            String.valueOf(System.nanoTime()), "Main Voltage violation alert item");
    private MainAlert branchlimitViolation = new MainAlert("Branch Limit",
            String.valueOf(System.nanoTime()), "Main Branch Limit violation alert item");
    private Timer refreshTimer = null;
    private boolean chartPanelVisible = true;

    private SPMainFrame() throws HeadlessException {
        super("SoftGrid Monitor");
        try {
            setupGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() throws Exception {
        this.getContentPane().setLayout(new GridBagLayout());
        chartPanel = getChartPanel();
        this.setPreferredSize(new Dimension(800, 700));

        alertPanel.setPreferredSize(new Dimension(200, 700));
        alertPanel.setMinimumSize(new Dimension(200, 700));
        alertPanel.setMaximumSize(new Dimension(200, 700));

        transientPanel.setPreferredSize(new Dimension(200, 700));
        transientPanel.setMinimumSize(new Dimension(200, 700));
        transientPanel.setMaximumSize(new Dimension(200, 700));

        queryTextArea.setPreferredSize(new Dimension(300, 100));
        queryTextArea.setMinimumSize(new Dimension(300, 100));
        queryTextArea.setMaximumSize(new Dimension(300, 100));

        monitorButton.setPreferredSize(new Dimension(30, 30));
        monitorButton.setMinimumSize(new Dimension(30, 30));
        monitorButton.setMaximumSize(new Dimension(30, 30));
        monitorButton.setToolTipText("Execute and Monitor");

        exitButton.setPreferredSize(new Dimension(30, 30));
        exitButton.setMinimumSize(new Dimension(30, 30));
        exitButton.setMaximumSize(new Dimension(30, 30));
        exitButton.setToolTipText("Close and Exit");

        clearButton.setPreferredSize(new Dimension(30, 30));
        clearButton.setMinimumSize(new Dimension(30, 30));
        clearButton.setMaximumSize(new Dimension(30, 30));
        clearButton.setToolTipText("Reset");

        runButton.setPreferredSize(new Dimension(30, 30));
        runButton.setMinimumSize(new Dimension(30, 30));
        runButton.setMaximumSize(new Dimension(30, 30));
        runButton.setToolTipText("Initialize the server...!");

        alertPanel.setLayout(new VerticalFlowLayout());
        transientPanel.setLayout(new BorderLayout());
//      System.out.println("new File(\"../MonitorEngine/Images/execute-xxl.png\").exists() = " + new File("../MonitorEngine/Images/execute-xxl.png").exists());
//      System.out.println("execute-xxl.png = " + new File("execute-xxl.png").exists());
//      System.out.println("new File().getAbsolutePath() = " + new File("openmuc.jar").getAbsolutePath());
        Image img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("execute-xxl.png"))).getImage();
        Image newimg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(newimg);
        monitorButton.setIcon(icon);

        img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("stop-xxl.png"))).getImage();
        newimg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        exitButton.setIcon(icon);

        img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("reset-xxl.png"))).getImage();
        newimg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        clearButton.setIcon(icon);

        img = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("start-xxl.png"))).getImage();
        newimg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        runButton.setIcon(icon);
        altertScrolPane.getViewport().add(alertPanel, null);
        altertScrolPane.setBorder(BorderFactory.createEtchedBorder());
        logAreaScrollPane.getViewport().add(logTextArea, null);
        logAreaScrollPane.setBorder(BorderFactory.createEtchedBorder());

        JPanel tempQueryPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
//      tempQueryPanel.setBorder(BorderFactory.createTitledBorder("Monitor Query"));
        tempQueryPanel.setLayout(new GridBagLayout());
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Color.gray);
        buttonPanel.add(runButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 12, 3, 12), 0, 0));
        buttonPanel.add(monitorButton, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 12, 3, 12), 0, 0));
        buttonPanel.add(clearButton, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 12, 3, 12), 0, 0));
        buttonPanel.add(exitButton, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 12, 3, 12), 0, 0));
//      tempQueryPanel.add(splitPane, new GridBagConstraints(0, 0, 1, 6, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        tempQueryPanel.add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        tempQueryPanel.add(queryScrolPane, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainTabbedPane.add(splitPane, "Controller");

        mainTabbedPane.add(ConfigPanel.getInstance(), "Configuration");

        splitPane.setTopComponent(tempQueryPanel);
        splitPane.setDividerLocation(152);
        splitPane.setBottomComponent(resultTabbedPane);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.getContentPane().add(mainTabbedPane, new GridBagConstraints(0, 0, 1, 2, 0.75, 0.25, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(12, 12, 12, 0), 0, 0));
//      this.getContentPane().add(alertTitile, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(12, 0, 0, 12), 0, 0));
//      this.getContentPane().add(altertScrolPane, new GridBagConstraints(1, 1, 1, 2, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 12, 12), 0, 0));
//      tempQueryPanel.add(queryScrolPane, new GridBagConstraints(0, 0, 1, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
//      this.getContentPane().add(logAreaScrollPane, new GridBagConstraints(0, 2, 1, 1, 0.75, 0.75, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(12, 12, 12, 0), 0, 0));
        exitButton.addActionListener(this);
        monitorButton.addActionListener(this);
        clearButton.addActionListener(this);
        clearButton.setEnabled(false);
        clearButton.setVisible(false);
        monitorButton.setEnabled(true);
        runButton.addActionListener(this);
        queryTextArea.setContentType("text/html");
        queryTextArea.setText("select overloadrank from virtual");

        messageHandler = new MessageUIHandler(logTextArea, logAreaScrollPane);
        ConfigPanel.getInstance().setupConfigPanel();
    }

    public static SPMainFrame getInstance() {
        if (instance == null) {
            instance = new SPMainFrame();
        }
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitButton) {
            windowClosing(null);
        } else if (e.getSource() == monitorButton) {
            executeMonitorQuery();
        } else if (e.getSource() == clearButton) {
            alertPanel.removeAll();
            stopIEDServers();
            logAreaScrollPane.setVisible(true);
            chartPanel.setVisible(false);
            runButton.setEnabled(true);
            clearButton.setEnabled(false);

        } else if (e.getSource() == runButton && logAreaScrollPane.isVisible()) {
            System.out.println("Initializing...!");
            loadConfigurations();
            switch (ConfigUtil.SERVER_TYPE.toUpperCase()) {
                case "IED":
                    System.out.println("Starting IEDs...!");
                    startIEDs(null);
                    break;
                case "PRX":
                case "ACM":
                    startPRXs();
                    break;
            }
            runButton.setEnabled(false);
            clearButton.setEnabled(true);
        }
    }

    public void loadConfigurations() {
        ConfigPanel.getInstance().setupConfigPanel();
    }


    private void executeMonitorQuery() {
        String query = queryTextArea.getText().toLowerCase();

        query = query.replaceAll("<html>", " ").
                replaceAll("<head>", "").
                replaceAll("<body>", " ").
                replaceAll("</head>", " ").
                replaceAll("</body>", "").
                replaceAll("<b>", "").
                replaceAll("<br>", " ").
                replaceAll("<font color=\"blue\">", " ").
                replaceAll("<font color=\"black\">", " ").
                replaceAll("</font>", " ").
                replaceAll("</b>", " ").
                replaceAll("</html>", "").
                replaceAll("\n", " ").trim();

        ArrayList<MonitorConfig> monitorConfigs = ConfigGenerator.executeNewQuery(query);
        if (monitorConfigs != null && monitorConfigs.size() > 0) {
            this.monitorConfigs = monitorConfigs;
        }

        query = query.replaceAll("select|SELECT", "<font color=\"black\"><br>\n SELECT</font>").
                replaceAll("from|FROM", "<font color=\"black\">FROM</font>").
                replaceAll("where|WHERE", "<font color=\"black\">WHERE</font>");

        query = "<html><b><font color=\"blue\"> " + query + "</b></font></html>";
        queryTextArea.setText(query);
    }

    private void startMonitor() {
        if (refreshTimer != null) {
            return;
        }
        refreshTimer = new Timer(true);
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                while (true) {
                    if (refreshTimer == null || !chartPanelVisible) {
                        break;
                    }
                    try {
                        refresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 1000, 500);
    }

    private void stopMonitor() {
        refreshTimer.cancel();
        refreshTimer = null;
    }

    @Override
    public void logEvent(String event) {
        if (logTextArea != null && event != null) {
            logTextArea.setText(logTextArea.getText() + "\n" + event);
//          logTextArea.updateUI();
        } else {
            System.out.println("event = " + event);
        }
    }

    public void startIEDs(final String caseFilePath) {
        runButton.setEnabled(false);
        clearButton.setEnabled(true);
        serverStatus = 0;
        setTitle("SoftGrid Monitor");
        SwingWorker iedWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                System.out.println("Starting IED initialization...!");
                resultTabbedPane.add(logAreaScrollPane, "IED Log");
                queryScrolPane.getViewport().add(queryTextArea, null);
                messageHandler.addLogMessage("Initialize Power World...DONE");
                messageHandler.addLogMessage("Initialize COM interface...DONE");
                messageHandler.addLogMessage("Initialize IED interfaces...DONE");
                messageHandler.addLogMessage("Starting IED Servers.........!(This may take few seconds)");
                it.illinois.adsc.ema.control.conf.generator.ConfigGenerator.generateConfigXml(ConfigUtil.SCL_PATH, ConfigUtil.CONFIG_PEROPERTY_FILE, ConfigUtil.IP);
                messageHandler.addLogMessage("Generating SCL files...!");
                SclGenerator.generateSCLFiles();
                messageHandler.addLogMessage("SCL files generated.");
                String[] args = {"-f", ConfigUtil.CONFIG_PEROPERTY_FILE, ConfigUtil.SERVER_TYPE, "local"};
                mainTabbedPane.add(altertScrolPane, "Transient Monitor");
                monitorButton.setVisible(true);
                SoftGridController.setIEDLogEventListener(SPMainFrame.this);
                executeMonitorQuery();
                startPython();
                System.out.println("All Init Operations are executed...!");
                SoftGridController.initiate(args);
                try {
                    dataFileWriter = new BufferedWriter(new FileWriter(new File(ConfigUtil.EXP_DATA_FILE)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        iedWorker.execute();
        startMonitor();
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public void stopIEDServers() {
        SwingWorker iedWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                messageHandler.addLogMessage("Stop all operations...!");
                stopMonitor();
                messageHandler.addLogMessage("Close all windows...!");
                windowClosing(null);
                messageHandler.addLogMessage("exit IED server...!");
                setVisible(false);
                messageHandler.addLogMessage("Stop all IED threads...!");
                SoftGridController.killAll();
                System.exit(0);
                return null;
            }
        };
        iedWorker.execute();
    }

    private void startPRXs() {
        SwingWorker iedWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                resultTabbedPane.add(logAreaScrollPane, "Gateway Log");
                stateTextArea.setEnabled(false);
                stateTextArea.setDisabledTextColor(Color.blue);
                queryScrolPane.getViewport().add(stateTextArea, null);
                stateTextArea.setText("Initializing Proxy gateway...");
                if (!ConfigUtil.PW_TO_SCL_MAPPING.isEmpty()) {
                    it.illinois.adsc.ema.control.conf.generator.ConfigGenerator.generateConfigXml(ConfigUtil.SCL_PATH, ConfigUtil.CONFIG_PEROPERTY_FILE, ConfigUtil.IP);
                }
                if (ConfigUtil.SERVER_TYPE.equals("PRX")) {
                    String[] args = {"-f", ConfigUtil.CONFIG_PEROPERTY_FILE, ConfigUtil.SERVER_TYPE, "local"};
                    SoftGridController.setPRXLogEventListener(SPMainFrame.this);
                    SoftGridController.initiate(args);
                } else if (ConfigUtil.SERVER_TYPE.equals("ACM")) {
                    String[] args = {ConfigUtil.SERVER_TYPE};
//                    IEC60870104Server.main(args);
                }
                stateTextArea.setText("Gateway Started..!" +
                        "\nGateway Server Protocol : IEC 60870-5-104 " +
                        "\nIED Protocol : IEC 61850");
                return null;
            }
        };
        iedWorker.execute();
    }


    private ChartPanel getChartPanel() {
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Power Grid Status",   // chart title
                "Time (S)",             // domain axis label
                "Units",                // range axis label
                dataset,                // data
                PlotOrientation.VERTICAL, // the plot orientation
                true,                   // legend
                true,                   // tooltips
                false                   // urls
        );
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        if (minTimeRange < maxTimeRange) {
            xyPlot.getDomainAxis().setAutoRange(true);
            //setRange(minTimeRange, maxTimeRange);
            return new ChartPanel(chart);
        }
        return null;
    }

    long lastLine = 0;

    private void refresh() {
        if (!chartPanelVisible) {
            return;
        }
        try {
            File file = new File(ConfigUtil.LIMIT_VIOLATION_RECORD_FILE);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Invalid Limit Violation Path\n" + file.getAbsolutePath());
//                    System.exit(0);
                }
            }

            BufferedReader bufferedReader;
            String line = "";
            for (MonitorConfig monitorConfig : monitorConfigs) {
                monitorConfig.setXySeries(new XYSeries(monitorConfig.toString()));
            }
            long lineCount = -1;
            long racordCount = 0;
            String type = "";
            boolean found = false;
            for (int j = 0; j < 2; j++) {
                File logFile = new File(ConfigUtil.LOG_FILE);
                if (!logFile.exists()) {
                    logFile = new File(ConfigUtil.LOG_FILE + "." + j);
                    if (!logFile.exists()) {
                        System.out.println("Invalid IED log file path.\n" + logFile.getAbsolutePath());
                        break;
                    }
                } else {
                    j = 2;
                }
                bufferedReader = new BufferedReader(new FileReader(logFile));
                while ((line = bufferedReader.readLine()) != null) {
                    lineCount++;
                    String[] data = line.split(":");
                    if (data.length >= 4 && data[0].equals("Data")) {
                        double timeinMilis = (Double.parseDouble(data[1]) - startTime) * 100.00 / 100000.00;
                        if (timeinMilis < 0) startTime -= 10;
//                      Day day = new Day(Long.parseLong(data[1]));
                        type = data[3];
//                      select the type
                        ArrayList<MonitorConfig> newMonitors = new ArrayList<MonitorConfig>();
                        ArrayList<MonitorConfig> removableQueries = new ArrayList<MonitorConfig>();
                        for (MonitorConfig monitorConfig : monitorConfigs) {
                            if (monitorConfig.getDeviceType().equalsIgnoreCase(type)) {
                                if (monitorConfig.getKeyValueMap().isEmpty()) {
                                    found = true;
                                } else {
                                    for (String key : monitorConfig.getKeyValueMap().keySet()) {
                                        found = false;
                                        for (int i = 4; i < data.length; i++) {
                                            if (data[i].equalsIgnoreCase(key) && data.length >= i + 1) {
                                                if (data[i + 1].trim().equalsIgnoreCase(monitorConfig.getKeyValueMap().get(key))) {
                                                    found = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (found) {
                                    for (int i = 4; i < data.length; i++) {
                                        if (monitorConfig.getXySeries() != null && data[i].equalsIgnoreCase(monitorConfig.getVariable())) {
                                            double value = 0;
                                            try {
                                                if (data[i + 1].equals("Closed")) {
                                                    value = 0;
                                                } else if (data[i + 1].equals("Open")) {
                                                    value = 1;
                                                } else {
                                                    value = Double.parseDouble(data[i + 1]);
                                                }
                                                if (this.isVisible() && timeinMilis > 0 && value < 0.5 && monitorConfig.getVariable().contains("BusKVVolt")) {
                                                    JOptionPane.showMessageDialog(this, "Blackout occurred...!");
                                                    try {
                                                        Thread.sleep(7000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    stopIEDServers();
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("data[2] = " + data[2]);
                                                e.printStackTrace();
                                                return;
                                            }
                                            monitorConfig.getXySeries().add(timeinMilis, value);
                                            maxTimeRange = (long) timeinMilis;
                                            if (maxTimeRange > 0 && dataFileWriter != null) {
                                                dataFileWriter.write(maxTimeRange + " , " + monitorConfig.getVariable() + " , " + String.valueOf(value) + "\n");
                                                dataFileWriter.flush();
                                            }
                                            if (racordCount >= 100000) {
                                                XYDataItem xyDataItem = monitorConfig.getXySeries().remove(0);
                                                minTimeRange = xyDataItem.getX().longValue();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (monitorConfigs.size() < 10) {
                            monitorConfigs.addAll(newMonitors);
                        }
                        if (racordCount < 50000) {
                            racordCount++;
                        }
                    }
                }
                bufferedReader.close();
            }
            SMAlert smAlert = lastAlert;
            while (smAlert != null && smAlert.getPreviouseAlert() != null) {
                smAlert = smAlert.getPreviouseAlert();
                long addedTime = alertTimeMap.get(smAlert);
                if ((System.currentTimeMillis() - addedTime) > ALERT_STORE_DURATION) {
                    removeAlert(smAlert);
                }
            }
            FileReader fileReader = null;
            BufferedReader violationReader = null;
            try {
                fileReader = new FileReader(file);
                violationReader = new BufferedReader(fileReader);
                while ((line = violationReader.readLine()) != null) {
                    String[] vlData = line.split(",");
                    try {
                        long addedTime = Long.parseLong(vlData[0]);
                        if ((System.currentTimeMillis() - addedTime) <= ALERT_STORE_DURATION) {
                            addAlert(addedTime, new SMAlert(vlData[3], vlData[2], vlData[1]));
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    violationReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            lastLine = lineCount;
            dataset.removeAllSeries();
            for (MonitorConfig monitorConfig : monitorConfigs) {
                if (monitorConfig.getXySeries() != null) {
                    dataset.addSeries(monitorConfig.getXySeries());
                }
            }
            ChartPanel tempNewPanel = getChartPanel();
            if (tempNewPanel != null) {
                JPanel tempPanel = chartPanel;
                chartPanel = tempNewPanel;
                chartPanel.updateUI();
                Component component = resultTabbedPane.getSelectedComponent();
                resultTabbedPane.remove(tempPanel);
                resultTabbedPane.add(chartPanel, "Chart Panel");
                resultTabbedPane.setSelectedComponent(component == tempPanel ? chartPanel : component);
//              this.getContentPane().add(chartPanel, new GridBagConstraints(0, 2, 1, 1, 0.75, 0.75, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(12, 0, 12, 0), 0, 0));
//              if (tempPanel != null) {
//                  this.getContentPane().remove(tempPanel);
//              }
                chartPanel.updateUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getContentPane().repaint();
    }

    private void removeAlert(SMAlert smAlert) {
        if (smAlert != null) {
            alertPanel.remove(smAlert);
            alertTimeMap.remove(smAlert);
            if (smAlert.getNextAlert() != null) {
                smAlert.getNextAlert().setPreviouseAlert(null);
                smAlert.setNextAlert(null);
            }
        }
    }


    public void addAlert(Long time, SMAlert smAlert) {
        if (smAlert.isFrequencyViolation()) {
            frequencyViolation.addFqAlert(smAlert);
            smAlert = frequencyViolation;
        } else if (smAlert.isBranchLimitViolation()) {
            branchlimitViolation.addFqAlert(smAlert);
            smAlert = branchlimitViolation;
        } else if (smAlert.isVoltageViolation()) {
            voltageViolation.addFqAlert(smAlert);
            smAlert = voltageViolation;
        }
        smAlert.formatText();
        for (Component component : alertPanel.getComponents()) {
            if (component instanceof SMAlert) {
                SMAlert existingAlerts = (SMAlert) component;
                if (existingAlerts.equals(smAlert)) {
                    // allert already in teh queue
                    existingAlerts.updateUI();
                    return;
                }
            }
        }
        if (smAlert.getText().length() > 0) {
            alertTimeMap.put(smAlert, time);
            alertPanel.add(smAlert);
            smAlert.setPreviouseAlert(lastAlert);
            if (lastAlert != null) {
                lastAlert.setNextAlert(smAlert);
            }
            lastAlert = smAlert;
            alertCount++;
        }
//        if (alertCount > MAX_VISIBLE_ALERTS) {
//            for (int i = 0; i < MAX_VISIBLE_ALERTS; i++) {
//                if (smAlert != null) {
//                    smAlert = smAlert.getPreviouseAlert();
//                }
//            }
//            removeAlert(smAlert);
//            alertCount--;
//        }
    }

    public void displayMonitorWindow(String title) {
        if (title != null && title.equals("PRX")) {
            setTitle("SoftGrid Gateway Monitor");
        }
        this.setVisible(true);
        this.addWindowListener(this);
        this.setSize(new Dimension(1300, 800));
    }

    public static void main(String[] args) {
        SPMainFrame.getInstance().displayMonitorWindow(args.length > 0 ? args[0] : null);
    }

    private void startPython() {
        pythonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String batCommands = "cd " + ConfigUtil.PYTHON_FILE_PATH + "\n" +
                            "python PYCOB_PW_SM.py  \"" + ConfigUtil.POWER_WORLD_EXE + "\"";
                    File batFile = new File(ConfigUtil.PYTHON_FILE_PATH + File.separator + "pythonproxy.bat");
                    FileWriter fw = null;
                    try {
                        if (!batFile.exists()) {
                            batFile.createNewFile();
                        }
                        fw = new FileWriter(batFile);
                        fw.write(batCommands);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (fw != null) {
                            try {
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    startPythonThread(batFile.getAbsolutePath());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        pythonThread.start();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    public Process startPythonThread(String batfilePath) throws Exception {

        File file = new File("state.file");
        file.createNewFile();
        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            URL[] urls = ((URLClassLoader) cl).getURLs();
            for (URL url : urls) {
                System.out.println(url.getFile());
            }

            Runtime runTime = Runtime.getRuntime();
            pythonProcess = runTime.exec("cmd.exe /k " + batfilePath);
            InputStream inputStream = pythonProcess.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            InputStream errorStream = pythonProcess.getErrorStream();
            InputStreamReader esr = new InputStreamReader(errorStream);

            String n1 = "";
            StringBuffer standardOutput = new StringBuffer();
            while ((n1 = reader.readLine()) != null) {
                System.out.println(n1);
            }
//          System.out.println("Standard Output: " + standardOutput.toString());
            int n2;
            char[] c2 = new char[1024];
            StringBuffer standardError = new StringBuffer();
            while ((n2 = esr.read(c2)) > 0) {
                standardError.append(c2, 0, n2);
            }
            System.out.println("Standard Error: " + standardError.toString());
            return pythonProcess;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void destroyProcess(Process process) {
        final String classGetName = process.getClass().getName();
        if ("JAVA_LANG_WIN32_PROCESS".equals(classGetName) || "java.lang.ProcessImpl".equals(process.getClass().getName())) {
            // determine the pid on windowsplattforms
            process.destroy();
            try {
                Field field = process.getClass().getDeclaredField("handle");
                field.setAccessible(true);
                final int pid = Kernel32.INSTANCE.GetProcessId((Long) field.get(process));
                // killing the task.
                Runtime.getRuntime().exec("taskkill " + pid);

            } catch (SecurityException e) {
                System.out.println("Error in Killing the process:" + e);
            } catch (NoSuchFieldException e) {
                System.out.println("Error in Killing the process:" + e);
            } catch (IOException e) {
                System.out.println("Error in Killing the process:" + e);
            } catch (IllegalArgumentException e) {
                System.out.println("Error in Killing the process:" + e);
            } catch (IllegalAccessException e) {
                System.out.println("Error in Killing the process:" + e);
            }

        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
//      pythonProcess.destroy();
//      destroyProcess(pythonProcess);
        File file = new File("state.file");
        file.delete();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if (ConfigUtil.SERVER_TYPE != null && !ConfigUtil.SERVER_TYPE.equalsIgnoreCase("CC")) {
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    public void removeChartPanel() {
        chartPanelVisible = false;
        if (resultTabbedPane != null && chartPanel != null) {
            resultTabbedPane.remove(chartPanel);
        }
    }
}

/**
 * This interface use to kernel32.
 */
interface Kernel32 extends Library {

    /**
     *
     */
    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

    /**
     * GetProcessId.
     *
     * @param hProcess hProcess
     * @return return the PID.
     */
    int GetProcessId(Long hProcess);
    // NOTE : Do not change the GetProcessId method name.
}



