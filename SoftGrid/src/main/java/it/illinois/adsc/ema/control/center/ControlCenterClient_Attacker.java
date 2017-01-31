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
import it.illinois.adsc.ema.control.conf.IedNodeType;
import it.illinois.adsc.ema.control.conf.generator.ConfigGenerator;
import it.illinois.adsc.ema.softgrid.concenter.ui.ControlCenterGUI;
import org.openmuc.j60870.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public final class ControlCenterClient_Attacker implements ConnectionEventListener {

    private static ControlCenterClient_Attacker instance;
    private ControlCenterGUI controlCenterGUI = null;
    private static boolean received;
    private ControlCenterContext controlCenterContext;
    private ExperimentData experimentData;

    private int breakingPercentage = 0;
    private List<Integer> addressSpace = new ArrayList<Integer>();
    final HashMap<String, Connection> proxyConectionMap = new HashMap<String, Connection>();

    private ControlCenterClient_Attacker(ControlCenterContext controlCenterContext) {
        this.controlCenterContext = controlCenterContext;
    }

    public static ControlCenterClient_Attacker getInstance(ControlCenterContext controlCenterContext) {
        if (instance == null) {
            instance = new ControlCenterClient_Attacker(controlCenterContext);
        }
        return instance;
    }

    public void setControlCenterGUI(ControlCenterGUI controlCenterGUI) {
        this.controlCenterGUI = controlCenterGUI;
    }

    private void printUsage() {
        System.out.println("Custom\n");
        System.out.println("SYNOPSIS\n\torg.openmuc.j60870.app.ClientApp <host> [-p <port>] [-ca <common_address>]");
        System.out.println("DESCRIPTION\n\tA client/master application to access IEC 60870-5-104 slaves.");
        System.out.println("OPTIONS");
        System.out.println("\t<host>\n\t    The address of the slave you want to access.\n");
        System.out.println("\t-p <port>\n\t    The port to connect to. The default port is 2404.\n");
        System.out.println("\t-ca <common_address>\n\t    The address of the target station or the broad cast address. The default is 1.\n");
    }

    public void startClient(String ipAddress, int portNumber) {
        startClient(ipAddress, portNumber, 9);
    }

    public void startClient(String ipAddress, int portNumber, int breakingPercentage) {

        HashMap<String, Integer> proxyIpPort = new HashMap<String, Integer>();
        proxyIpPort.put(ipAddress, portNumber);
        startClient(proxyIpPort, "CB", "linestatus", "true", breakingPercentage);
    }

    public void startClient(HashMap<String, Integer> proxyIpPort, String deviceType, String field, String value, int bp) {
        this.breakingPercentage = bp;
        if (proxyIpPort == null || proxyIpPort.isEmpty()) {
            printUsage();
            return;
        }
        ClientSap clientSap = new ClientSap();

        try {
            for (String ipAddress : proxyIpPort.keySet()) {

                System.out.println("Atempt_1 : Connecting.....: " + ipAddress + " Port : " + proxyIpPort.get(ipAddress));
                Connection clientConnection;
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        if (proxyConectionMap != null) {
                            for (Connection connection : proxyConectionMap.values()) {
                                connection.close();
                            }
                        }
                    }
                });
                InetAddress address;
                try {
                    address = InetAddress.getByName(ipAddress);
                } catch (UnknownHostException e) {
                    System.out.println("Unknown host: " + ipAddress);
                    return;
                }

                try {
                    clientConnection = clientSap.connect(address, proxyIpPort.get(ipAddress));//, InetAddress.getByName("10.0.1.8"), 2434);
                    clientConnection.startDataTransfer(this, 5000);
                    proxyConectionMap.put(ipAddress.split("\\.")[3], clientConnection);
                } catch (IOException e) {
                    System.out.println("Unable to connect to remote host: " + ipAddress + ".");
                } catch (TimeoutException e2) {
                    throw new IOException("starting data transfer timed out.");
                }
                System.out.println("successfully connected. : " + ipAddress + " Port : " + proxyIpPort.get(ipAddress));
            }
            System.out.println("successfully connected. ");
            String line;
            received = true;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int circuiteBreakerCount = 0;
            int attackDeviceCount = 0;
            int operatingIEDCount = 0;
            List<ExperimentData> experimentDatas = new ArrayList<ExperimentData>();

            for (IedNodeType iedNodeType : controlCenterContext.getIedVariableMap().values()) {
                String identifierString = iedNodeType.getPWCaseFileName();
                if (deviceType != null && identifierString != null && deviceType.equals("CB") && (identifierString.contains("CB") || identifierString.contains("Load"))) {
                    circuiteBreakerCount++;
                    attackDeviceCount++;
                    addressSpace.add(Integer.parseInt(iedNodeType.getPort()) - ConfigGenerator.FIRST_PORT);
                } else if (deviceType != null && identifierString != null && deviceType.equals(identifierString)) {
                    attackDeviceCount++;
                } else if (!(deviceType != null && identifierString != null && identifierString.contains("BUS") || deviceType != null && identifierString != null && identifierString.contains("GEN"))) {
                    operatingIEDCount++;
                }
            }
            if (!(deviceType != null && deviceType.equals("CB"))) {
                attackDeviceCount = circuiteBreakerCount;
            }

            received = true;
            Random rand = new Random(System.nanoTime());
            String lineStatus = "false";
            int expCount = 0;

            for (int j = 0; j < 1; j++) {
                lineStatus = lineStatus.equals("true") ? "false" : "true";
                System.out.println("SWAP>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                List<Integer> tempAddressSpace = new ArrayList<Integer>();
                tempAddressSpace.addAll(addressSpace);
//                breakingPercentage = 0;
                for (int i = 0; i < attackDeviceCount; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    if (!received) {
//                        i--;
//                        continue;
//                    }
                    experimentData = new ExperimentData();
                    experimentData.setLineStatus(lineStatus);
                    experimentData.setExpNo(expCount++);
                    int iedAddress = tempAddressSpace.get(rand.nextInt(tempAddressSpace.size()));
                    tempAddressSpace.remove(new Integer(iedAddress));
                    int per = (i * 100 / attackDeviceCount);
                    if ((per - breakingPercentage) < 0) {
                        sendCommand(proxyConectionMap, CommandParser.parseCommandString("scommand " + iedAddress + " " + field + "=" + value));
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println("percent " + per + " : breakingPercentage = " + breakingPercentage);
//                        System.out.println("Executed Address Count = " + i);
//                        experimentData.setPercentage(per);
//                        experimentDatas.add(experimentData);
//                        sendCommand(proxyConectionMap, CommandParser.parseCommandString("interrogation " + 0));
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Last Interrogation...!");
                sendCommand(proxyConectionMap, CommandParser.parseCommandString("interrogation all" ));
            }
            for (ExperimentData data : experimentDatas) {
                System.out.println(data);
            }
        } catch (IOException e) {
            System.out.println("Connection closed for the following reason: " + e.getMessage());
            return;
        }
    }

    private void sendCommand(HashMap<String, Connection> proxyConectionMap, Command command) {
        try {
            for (String clientAddress : proxyConectionMap.keySet()) {
                if (command != null && controlCenterContext.validate(command)) {
                    MessageFactory.sendCommand(command, proxyConectionMap.get(clientAddress), controlCenterGUI, null);
                }
            }
        } catch (IOException e1) {
            System.out.println("Connection to server was interrupted. Will quit.");
            return;
        }
    }

    @Override
    public void newASdu(ASdu aSdu) {
        if (aSdu != null && controlCenterGUI != null) {
            controlCenterGUI.newASdu(aSdu.toString());
        }
        if (aSdu.getCommonAddress() == 0 && aSdu.getInformationObjects() != null) {
            for (InformationObject informationObject : aSdu.getInformationObjects()) {
                for (InformationElement[] informationElements : informationObject.getInformationElements()) {
                    if (informationElements[0] instanceof IeShortFloat) {
                        System.out.println("informationElements = " + informationElements[0]);
                        if ((addressSpace.contains(new Integer(informationObject.getInformationObjectAddress())))) {
                            experimentData.setBusFrequency(((IeShortFloat) informationElements[0]).getValue());
                            received = true;
                        } else {
                            experimentData.setLoadRank(((IeShortFloat) informationElements[0]).getValue());
                        }
                        break;
                    }
                }
            }
        }
        if (ControlCenterClient.getCCSecurityHandler() != null) {
            ControlCenterClient.getCCSecurityHandler().newASdu(aSdu, System.nanoTime());
        }
    }

    @Override
    public void connectionClosed(IOException e) {
        System.out.print("Received connection closed signal. Reason: ");
        if (!e.getMessage().isEmpty()) {
            System.out.println(e.getMessage());
        } else {
            System.out.println("unknown");
        }
        received = true;
    }

    public void kill() {
        try {
            instance = null;
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        for (Connection connection : proxyConectionMap.values()) {
            connection.close();
        }
    }
}
