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
package it.illinois.adsc.ema.control.center;


import it.illinois.adsc.ema.control.center.command.Command;
import it.illinois.adsc.ema.control.center.command.CommandParser;
import it.illinois.adsc.ema.control.center.command.MessageFactory;
import it.illinois.adsc.ema.control.center.experiments.CCMessageCounter;
import it.illinois.adsc.ema.control.center.security.CCSecurityHandler;
import it.illinois.adsc.ema.control.ied.StatusHandler;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.softgrid.concenter.ui.ControlCenterGUI;
import org.openmuc.j60870.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * Created by prageethmahendra on 2/2/2016.
 */


public class ControlCenterClient implements ConnectionEventListener, Runnable {

    private ControlCenterGUI controlCenterGUI = null;
    //  private static boolean received;
    private ControlCenterContext controlCenterContext;
    public static final HashMap<String, Connection> PROXY_CONNECTION_MAP = new HashMap<String, Connection>();
    private static HashMap<Object, ControlCenterClient> instanceMap = new HashMap<>();
    private static CCSecurityHandler cCSecurityHandler = null;
    private boolean securityEnabled = false;
    private static CCMessageCounter ccMessageCounter = new CCMessageCounter();
    private Connection clientConnection;
    public static boolean manualExperimentMode = true;
    private String IP_ADDRESS = "192.168.0.173";
    private int PORT = 2404;

    public ControlCenterClient(Connection connection, ControlCenterContext controlCenterContext, boolean securityEnabled, String ip, int port) {
        clientConnection = connection;
        this.securityEnabled = securityEnabled;
        this.controlCenterContext = controlCenterContext;
        this.IP_ADDRESS = ip;
        this.PORT = port;
        init();
    }

    private ControlCenterClient(ControlCenterContext controlCenterContext, String ip, int port) {
        this.securityEnabled = true;
        this.controlCenterContext = controlCenterContext;
        this.IP_ADDRESS = ip;
        this.PORT = port;
        init();
    }

    private synchronized void init() {

    }

    public static ControlCenterClient getInstance(String ipPort) {
        return instanceMap.get(ipPort);
    }

    public static ControlCenterClient getInstance(ControlCenterContext controlCenterContext, String ipPort) {
        ControlCenterClient instance = instanceMap.get(ipPort);
        if (instance == null) {
            String[] elements = ipPort.split(":");
            if (elements.length != 2) {
                return null;
            } else {
                String ip = elements[0];
                int port = Integer.valueOf(elements[1]);
                instance = new ControlCenterClient((controlCenterContext == null ? new ControlCenterContext(ConfigUtil.CONFIG_PEROPERTY_FILE) : controlCenterContext), ip, port);
            }
            instanceMap.put(ipPort, instance);
        }
        return instance;
    }

    public static CCSecurityHandler getCCSecurityHandler() {
        return cCSecurityHandler;
    }

    private void printUsage() {
        System.out.println("Custom\n");
        System.out.println("SYNOPSIS\n\t org.openmuc.j60870.app.ClientApp <host> [-p <port>] [-ca <common_address>]");
        System.out.println("DESCRIPTION\n\t A client/master application to access IEC 60870-5-104 slaves.");
        System.out.println("OPTIONS");
        System.out.println("\t<host>\n\t The address of the slave you want to access.\n");
        System.out.println("\t-p <port>\n\t The port to connect to. The default port is 2404.\n");
        System.out.println("\t-ca <common_address>\n\t The address of the target station or the broad cast address. The default is 1.\n");
    }

    @Override
    public void run() {
        startClient();
        if (manualExperimentMode) {
            periodicInterrogation();
        }
//        try {
//            new BufferedReader(new InputStreamReader(System.in)).readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public synchronized void startClient() {
//        if (proxyIpPort == null || proxyIpPort.isEmpty()) {
//            printUsage();
//            return;
//        }
        ClientSap clientSap = new ClientSap();
        if (clientConnection == null || clientConnection.isClosed()) {
//            for (String ipAddress : proxyIpPort.keySet()) {
            System.out.println("Attempt_1 : Connecting.....: " + IP_ADDRESS + " Port : " + PORT);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (PROXY_CONNECTION_MAP != null) {
                        for (Connection connection : PROXY_CONNECTION_MAP.values()) {
                            connection.close();
                        }
                    }
                }
            });
            InetAddress address = null;
            boolean error = false;
            try {
                address = InetAddress.getByName(IP_ADDRESS);
            } catch (UnknownHostException e) {
                System.out.println("Unknown Host: " + IP_ADDRESS);
                error = true;
            }

            if (!error) {
                try {
                    clientConnection = clientSap.connect(address, PORT);//, InetAddress.getByName("10.0.1.8"), 2434);
                    clientConnection.startDataTransfer(this, 6000);
                    PROXY_CONNECTION_MAP.put(IP_ADDRESS.split("\\.")[3], clientConnection);
                } catch (IOException e) {
                    System.out.println("Unable To Connect To Remote Host: " + IP_ADDRESS + "." + ConfigUtil.GATEWAY_CC_PORT);
                    error = true;
                    try {
                        clientConnection.close();
                    } catch (Exception e1) {
                        System.out.println("Error in connection close.");
                    }
                    clientConnection = null;
                } catch (TimeoutException e2) {
                    e2.printStackTrace();
                    error = true;
                    try {
                        clientConnection.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    clientConnection = null;
                }
            }
            if (error) {
                try {
                    StatusHandler.statusChanged("CC" + StatusHandler.ERROR);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    try {
                        clientConnection.close();
                    } catch (Exception e2) {
                        e1.printStackTrace();
                    }
                    clientConnection = null;
                }
                return;
            }
        }
        if (securityEnabled) {
            cCSecurityHandler = CCSecurityHandler.getInstance();
        }
        System.out.println("Successfully Connected. ");
        String line;
        System.out.println("controlCenterContext.isRemoteInteractive() = " + controlCenterContext.isRemoteInteractive());
        if (manualExperimentMode) {
            periodicInterrogation();
        }
        if (!controlCenterContext.isRemoteInteractive()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    line = reader.readLine();
                    runCommand(line.trim());
                } catch (IOException e1) {
                    System.out.println("Connection To Server Was Interrupted. Will Quit.");
                    return;
                }
            }
        }


    }

    public void setControlCenterGUI(ControlCenterGUI controlCenterGUI) {
        this.controlCenterGUI = controlCenterGUI;
    }

    String commandString = "";
    public void runCommand(String commandString) throws IOException {
//        if(commandString.contains("test throughput"))
//        {
//            periodicInterrogation();
//            return;
//        }
        this.commandString = commandString;
            Command command = CommandParser.parseCommandString(commandString);
//        if (command.getCommandType().equals(CommandType.ATTACK)) {
//            periodicInterrogation();
//            CCUserGenerator.main(null);
//            return;
//        }
            for (String clientAddress : PROXY_CONNECTION_MAP.keySet()) {
                if (command != null && controlCenterContext.validate(command)) {
                    MessageFactory.sendCommand(command, PROXY_CONNECTION_MAP.get(clientAddress), controlCenterGUI, ccMessageCounter);
                }
            }

    }

//    public boolean runCommand(ASdu aSdu) throws IOException {
//        if (aSdu != null && PROXY_CONNECTION_MAP != null && PROXY_CONNECTION_MAP.size() > 0) {
//            for (String connectionKey : PROXY_CONNECTION_MAP.keySet()) {
//                PROXY_CONNECTION_MAP.get(connectionKey).send(aSdu);
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }

    @Override
    public void newASdu(ASdu aSdu) {
        System.out.println(this.getClass().toString() + "\nReceived ASDU:" + System.nanoTime() + "\n" + aSdu.toString().replace('\n', ','));
        CCTimeLoger.logDuration(aSdu.getTypeIdentification().name());
        if (controlCenterGUI != null) {
            controlCenterGUI.newASdu(aSdu.toString());
        }
        ccMessageCounter.logMessageReceived(aSdu);
//        if(aSdu.getCauseOfTransmission().getCode() == 47)
//        {
//            try {
//                runCommand(commandString);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void connectionClosed(IOException e) {
        System.out.print("Received connection closed signal. Reason: ");
        if (!e.getMessage().isEmpty()) {
            System.out.println(e.getMessage());
        } else {
            System.out.println("unknown");
        }
//        received = true;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        for (Connection connection : PROXY_CONNECTION_MAP.values()) {
            connection.close();
        }
        instanceMap.clear();
    }

    public void kill() {
        try {
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void periodicInterrogation() {
        CCMessageCounter.SLOW = false;
        Timer uploadCheckerTimer = new Timer(true);
        uploadCheckerTimer.schedule(
                new TimerTask() {
                    public void run() {
//                        try {
                            Random random = new Random();

                            CCMessageCounter.SENT++;
//                            String IOA = String.valueOf(CCMessageCounter.SENT % 7000);
                            String IOA = "1";//String.valueOf(Math.abs(random.nextLong() % 110));
                            if (!CCMessageCounter.SLOW) {
//                                runCommand("interrogation " + IOA);
                            }

//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
//                }, 5000, 300); // 66 MPS
//                }, 5000 200); // 100 MPS
                }, 5000, 500); // 33 MPS
    }

    public Connection getConnection() {
        return clientConnection;
    }

    public void setConnection(Connection connection) {
        this.clientConnection = connection;
    }

    public void startProfiler(Connection connection) {
        int count = 1;
        while (true) {
            for (int iter = 0; iter < 15; iter++) {
                int sleepTime = 900 / count;
                for (int i = 0; i < count; i++) {
                    try {
                        runCommand("interrogation 53");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("count = " + count);
                }
            }
            count++;
        }
    }

    public static void removeInstance(String ipPort) {
        instanceMap.remove(ipPort);
    }
}
