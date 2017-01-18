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
package it.illinois.adsc.ema.softgrid.concenter.ui;

import it.illinois.adsc.ema.control.SmartPowerControler;
import it.illinois.adsc.ema.control.center.ControlCenterClient;
import it.illinois.adsc.ema.control.ied.StatusHandler;
import com.sun.jna.*;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

/**
 * Created by SmartPower on 20/5/2016.
 */
public class ControlCenter extends JFrame implements ActionListener, ControlCenterGUI, CCControler {
    private static final String SPMAINFRAME_START_BAT = ".\\spmainframe.bat";
    public static String CONFIG_PEROPERTY_FILE = ".\\config.properties";
    private static ControlCenter instance;

    private JTextField commandText = new JTextField();
    private JTextArea resultArea = new JTextArea();
    private JButton executeButton = new JButton("Execute");
    //    private JToggleButton enableSecurityButton = new JToggleButton("Disable Security");
//    private JButton clearAllButton = new JButton("Reset All");
    private JButton exitButton = new JButton("Exit");
    //    private JButton monitor = new JButton("Monitor");
    private JToggleButton startIedButton = new JToggleButton("Start");
    //  private JToggleButton startProxyButton = new JToggleButton("Start All Gateways");
//  private JToggleButton startControlButton = new JToggleButton("Start Control");
//  private JToggleButton pymonitor = new JToggleButton("PY-Monitor");
    private JPanel mainPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private ControlCenterClient controlCenterClient = null;
    private static final HashMap<String, ControlCenterClient> ccProxyConnectionMap = new HashMap<>();
    private JScrollPane scrollPane = new JScrollPane();
    JViewport viewport = new JViewport();
    SwingWorker iedWorker = null;
    SwingWorker proxyWorker = null;
    Process pythonProcess = null;
    //  Thread pythonThread = null;
    Thread monitorThread = null;
//  Process idProcess = null;
//  Process proxyProcess = null;


    private ControlCenter() throws HeadlessException {
        super("Control Center");
        setupGUI();
    }

    private void setupGUI() {
        this.getContentPane().setLayout(new GridBagLayout());
        this.getContentPane().add(mainPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(buttonPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(commandText, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(6, 6, 6, 6), 0, 0));
        mainPanel.add(executeButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
        scrollPane.setViewport(viewport);

        scrollPane.getViewport().add(resultArea, viewport);
        mainPanel.add(scrollPane, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 6, 6, 6), 0, 0));

        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonPanel.add(startIedButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
//      buttonPanel.add(startProxyButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
//      buttonPanel.add(startControlButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
//      buttonPanel.add(pymonitor, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
//      buttonPanel.add(executeButton,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
//        buttonPanel.add(enableSecurityButton, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
//        buttonPanel.add(monitor, new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
//        buttonPanel.add(clearAllButton, new GridBagConstraints(6, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
        buttonPanel.add(exitButton, new GridBagConstraints(7, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
//      buttonPanel.add(new JLabel(),new GridBagConstraints(3, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
//      buttonPanel.add(new JApplet(),new GridBagConstraints(3, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));

        commandText.setSize(new Dimension(100, 22));
        commandText.setMinimumSize(new Dimension(100, 22));
        commandText.setMaximumSize(new Dimension(100, 22));

        executeButton.setSize(new Dimension(100, 20));
        executeButton.setMinimumSize(new Dimension(100, 20));
        executeButton.setMaximumSize(new Dimension(100, 20));

//        enableSecurityButton.setSize(new Dimension(150, 20));
//        enableSecurityButton.setMinimumSize(new Dimension(150, 20));
//        enableSecurityButton.setMaximumSize(new Dimension(150, 20));

//        monitor.setSize(new Dimension(150, 20));
//        monitor.setMinimumSize(new Dimension(150, 20));
//        monitor.setMaximumSize(new Dimension(150, 20));

        exitButton.setSize(new Dimension(150, 20));
        exitButton.setMinimumSize(new Dimension(150, 20));
        exitButton.setMaximumSize(new Dimension(150, 20));

//        clearAllButton.setSize(new Dimension(150, 20));
//        clearAllButton.setMinimumSize(new Dimension(150, 20));
//        clearAllButton.setMaximumSize(new Dimension(150, 20));

        startIedButton.setSize(new Dimension(150, 20));
        startIedButton.setMinimumSize(new Dimension(150, 20));
        startIedButton.setMaximumSize(new Dimension(150, 20));

//      startProxyButton.setSize(new Dimension(150, 20));
//      startProxyButton.setMinimumSize(new Dimension(150, 20));
//      startProxyButton.setMaximumSize(new Dimension(150, 20));

//      startControlButton.setSize(new Dimension(150, 20));
//      startControlButton.setMinimumSize(new Dimension(150, 20));
//      startControlButton.setMaximumSize(new Dimension(150, 20));
//      pymonitor.setSize(new Dimension(150, 20));
//      pymonitor.setMinimumSize(new Dimension(150, 20));
//      pymonitor.setMaximumSize(new Dimension(150, 20));

        executeButton.addActionListener(this);
//        enableSecurityButton.addActionListener(this);
//        monitor.addActionListener(this);
        exitButton.addActionListener(this);
        startIedButton.addActionListener(this);
//      startControlButton.addActionListener(this);
//      pymonitor.addActionListener(this);
//      startProxyButton.addActionListener(this);
//        clearAllButton.addActionListener(this);
//
//      pymonitor.setVisible(false);
//      monitor.setVisible(false);
//      startProxyButton.setVisible(false);
//      startControlButton.setVisible(false);
//        enableSecurityButton.setSelected(true);
//      enableSecurity();
        deleteLogFiles();
    }

    public static ControlCenter getInstance() {
        if (instance == null) {
            instance = new ControlCenter();
        }
        return instance;
    }

    @Override
    public void startCCClient(String gatewayIP, int port) {
        showWindow();
        startControlCenter(gatewayIP, port);
    }

    @Override
    public void runCommand(String command) {
        String ipColanPort = null;
        if (command.contains(">")) {
            String[] ipPartArray = command.split(">");
            if (ipPartArray.length >= 2 && ipPartArray[1] != null && ipPartArray[1].contains(":")) {
                ipColanPort = ipPartArray[1].trim();
            }
        }
        runCommand(command, ipColanPort);
    }

    @Override
    public boolean isLive(String ip, int port) {
        System.out.println("is live check " + ip + "_" + port);
        ControlCenterClient controlCenterClient = ccProxyConnectionMap.get(getIPPortString(ip, port));
        if (controlCenterClient != null &&
                controlCenterClient.getConnection() != null &&
                !controlCenterClient.getConnection().isClosed()) {
            return true;
        } else {
            ccProxyConnectionMap.remove(getIPPortString(ip, port));
            return false;
        }

    }

    public void runCommand(String command, String ipColanPort) {
        ControlCenterClient controlCenterClient = null;
        if (ipColanPort != null && ipColanPort.length() > 0) {
            controlCenterClient = ccProxyConnectionMap.get(ipColanPort);
            if (controlCenterClient == null) {
                controlCenterClient = this.controlCenterClient;
            }
        }
        if (controlCenterClient != null) {
            try {
                controlCenterClient.runCommand(command);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    StatusHandler.statusChanged("CC_" + StatusHandler.ERROR);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            System.out.println("Control Center : Command received before startup...!");
        }
    }

    @Override
    public void stopCCClient() {
        this.setVisible(false);
        if (controlCenterClient != null) {
            controlCenterClient.kill();
        }
        instance = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitButton) {
            System.exit(0);
        }
//        else if (e.getSource() == clearAllButton) {
//            resultArea.setText("Server ShutDown Initiated...!");
//            SmartPowerControler.killAll();
//
//            if (controlCenterClient != null) {
//                controlCenterClient.kill();
//                ControlCenterClient_Attacker.getInstance(null).kill();
//            }
//            if (iedWorker != null) {
//                iedWorker.cancel(true);
//            }
//            if (proxyWorker != null) {
//                proxyWorker.cancel(true);
//            }
//            controlCenterClient = null;
//            commandText.setText("");
//            startIedButton.setEnabled(true);
//            startIedButton.setSelected(false);
////          startProxyButton.setEnabled(true);
////          startProxyButton.setSelected(false);
//          startControlButton.setEnabled(true);
//          startControlButton.setSelected(false);
//            addLogMessage("ShutDown Completed...!");
//            deleteLogFiles();
//        }
//    else if (e.getSource() == enableSecurityButton) {
//            enableSecurity();
//        }
//        else if (e.getSource() == monitor) {
//            monitorThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        startMonitorThread();
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            });
//            monitorThread.start();
//        }
        else if (e.getSource() == executeButton) {
            String commandString = commandText.getText().trim();
            try {
//                if (controlCenterClient == null) {
//                    startControlCenter();
//                }
                if (controlCenterClient != null) {
                    controlCenterClient.runCommand(commandString);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
//        else if (e.getSource() == pymonitor) {
//            if (pymonitor.isSelected()) {
//                pythonThread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            startMonitorThread();
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                });
//                pythonThread.start();
//
//            }
//            else {
//                if (pythonProcess != null) {
//                    pythonProcess.destroy();
//                    pythonProcess = null;
//                }
//                if (pythonThread != null) {
//                    pythonThread.stop();
//                    pythonThread = null;
//                }
//            }
//        }
//        else if (startIedButton == e.getSource()) {
//            startControlCenter(null);
//        }
//        else if (e.getSource() == startProxyButton) {
//            startProxies();
//
//        }
//        else if (e.getSource() == startControlButton) {
//            startControlCenter();
//        }
    }

    private void deleteLogFiles() {
        File iedLogFile1 = new File(ConfigUtil.LOG_FILE + ".0");
        if (iedLogFile1.exists()) {
            iedLogFile1.delete();
        }
        File iedLogFile2 = new File(ConfigUtil.LOG_FILE + ".1");
        if (iedLogFile2.exists()) {
            iedLogFile2.delete();
        }

    }

    private void enableSecurity() {
//        CCSecurityHandler.setEnable(!enableSecurityButton.isSelected());
//        SecurityHandler.getInstance().setEnabled(!enableSecurityButton.isSelected());
    }

    @Override
    public String newASdu(String result) {
//        result = result.replace("\n", " : ");
//        resultArea.setText(resultArea.getText() + "\n\n" + result);
//        scrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
//        progressScroll();
        addLogMessage(result);
        return null;
    }

    private void progressScroll() {
        scrollPane.updateUI();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        scrollPane.updateUI();
    }

//    private void startProxies() {
//        if (!startProxyButton.isEnabled()) {
//            return;
//        }
//
//        proxyWorker = new SwingWorker() {
//            @Override
//            protected Object doInBackground() throws Exception {
//                ControlCenter.getInstance().addLogMessage("Starting Proxy Gateway...!");
//                Thread.sleep(1000);
//                String[] args = {"-f", CONFIG_PEROPERTY_FILE, "PRX"};
//                SmartPowerControler.initiate(args);
//                return null;
//            }
//        };
//        proxyWorker.execute();
//        startProxyButton.setEnabled(false);
//    }

    private void startControlCenter(String gatewayIP, int port) {
        showWindow();
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                String ipPort = getIPPortString(gatewayIP, port);
                controlCenterClient = ControlCenterClient.getInstance(ipPort);
                if (controlCenterClient == null || controlCenterClient.getConnection() == null ||
                        controlCenterClient.getConnection().isClosed()) {
                    ControlCenter.getInstance().addLogMessage("Starting Control Center Client...!");
                    File file = new File(CONFIG_PEROPERTY_FILE);
                    if (!file.exists()) {
                        CONFIG_PEROPERTY_FILE = "config\\config.properties";
                        file = new File(CONFIG_PEROPERTY_FILE);
                        if (!file.exists()) {
                            System.out.println("Control Center config.properties file not found at\n" + file.getAbsolutePath());
                        }
                    }
                    String[] args = {"-f", CONFIG_PEROPERTY_FILE, "CC", gatewayIP, String.valueOf(port)};
                    SmartPowerControler.initiate(args);
                    controlCenterClient = ControlCenterClient.getInstance(ipPort);
                    controlCenterClient = ControlCenterClient.getInstance(ipPort);
                    if (controlCenterClient.getConnection() != null && !controlCenterClient.getConnection().isClosed()) {
                        controlCenterClient.setControlCenterGUI(ControlCenter.this);
                        ccProxyConnectionMap.put(ipPort, controlCenterClient);
                        try {
                            StatusHandler.statusChanged("CC_STARTED " + ipPort);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ControlCenter.getInstance().addLogMessage("Control Center Status : CC_STARTED");
                    }
                }
                return null;
            }
        };
        swingWorker.execute();


//            startControlButton.setEnabled(false);

//            try {
//                Thread.sleep(1000);
//                startMonitorThread();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    private String getIPPortString(String gatewayIP, int port) {
        return gatewayIP + ":" + port;
    }

    public static void main(String[] args) {

        showWindow();
    }

    private static void showWindow() {
        ControlCenter.getInstance().setSize(924, 1024);
        ControlCenter.getInstance().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    for (String s : ccProxyConnectionMap.keySet()) {
                        StatusHandler.statusChanged("CC_" + StatusHandler.NOTSTARTED + " " + s);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
//                System.exit(0);
            }
        });
        ControlCenter.getInstance().setVisible(true);
    }

//    public void startIEDs() {
//        iedWorker = new SwingWorker() {
//
//
//            @Override
//            protected Object doInBackground() throws Exception {
//                addLogMessage("Initialize Power World...DONE");
//                addLogMessage("Initialize COM interface...DONE");
//                addLogMessage("Initialize IED interfaces...DONE");
//                addLogMessage("Starting IED Servers.........!(This may take few seconds)");
//                String[] args = {"-f", CONFIG_PEROPERTY_FILE, "IED", "local"};
//                SmartPowerControler.initiate(args);
//                return null;
//            }
//        };
//        iedWorker.execute();
//        startIedButton.setEnabled(false);
//    }


    public Process startMonitorThread() throws Exception {

        String batfilePath = new File(SPMAINFRAME_START_BAT).getAbsolutePath();
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

    String postFixNewLines = "\n\n\n\n\n\n\n";

    public synchronized void processAndAddLogMessage(String message) {
        addLogMessage(message);
        if (message.contains("IED") && message.contains("is Started...!")) {
//            startProxies();
        } else if (message.contains("Proxy Gateway Started...!")) {
//            startControlCenter();
        }
    }

    public synchronized void addLogMessage(String message) {
        resultArea.setText(resultArea.getText().replace(postFixNewLines, "") + "\n" + message);// + postFixNewLines);
        resultArea.updateUI();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressScroll();
            }
        });
    }

    public static void destroyProcess(Process process) {
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
}

/**
 * This interface use to kernel32.
 */
interface Kernel32 extends Library {
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


