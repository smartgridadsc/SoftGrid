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

package it.illinois.adsc.ema.control.proxy.server;/*
 * This file is part of j60870.
 * For more information visit http://www.openmuc.org
 * You are free to use code of this sample file in any
 * way you like and without any restrictions.
 */

import it.illinois.adsc.ema.control.LogEventListener;
import it.illinois.adsc.ema.control.proxy.context.ProxyContextFactory;
import it.illinois.adsc.ema.control.proxy.context.ProxyServerContext;
import it.illinois.adsc.ema.control.proxy.infor.InformationASduBridge;
import it.illinois.adsc.ema.control.proxy.infor.ProxyInformation;
import it.illinois.adsc.ema.control.proxy.server.handlers.ICommandHandler;
import it.illinois.adsc.ema.control.proxy.util.DeviceType;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.softgrid.concenter.ui.ControlCenter;
import org.openmuc.j60870.*;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ProxyServer implements ServerSapListener, ConnectionEventListener, ICommandHandler, SecurityEventListener {
    private static int connectionIdCounter = 1;
    public static boolean LOCAL_API_MODE = false;
    protected static ProxyServer instance;
    private Connection connection;
    private IEC60870104ConnectionWrapper iec60870104ConnectionWrapper;
    protected int connectionId;
    private ProxyServerContext proxyContext;
    private ServerSap serverSap;
//  private SecurityHandler securityHandler;
    private static LogEventListener logEventListener;

    protected ProxyServer() {
        init();
    }

    protected ProxyServer(Connection connection, int connectionId) {
        init();
        this.connection = connection;
        this.connectionId = connectionId;
    }

    private void init() {
        proxyContext = ProxyContextFactory.getInstance().getProxyContext(this);
        LOCAL_API_MODE = ConfigUtil.PROXY_SERVER_LOCAL_API_MODE;
//      TODO : security is not needed in the ProxyServer
        if (!LOCAL_API_MODE) {
            logEvent("LOCAL_API_MODE disabled..!");
//          securityHandler = SecurityHandler.getInstance();
//          securityHandler.setEnabled(false);
//          securityHandler.addSecurityEventListener(this);
        } else {
            logEvent("LOCAL_API_MODE enabled..!");
        }
//      JavawsConsoleController.getInstance().showConsoleIfEnabled();
    }

    private void logEvent(String logString) {
        if (logEventListener != null) {
            logEventListener.logEvent(logString);
        }
    }

    @Override
    public void readyToExecute(ASdu aSdu) {
        System.out.println("readyToExecute(ASdu aSdu) not implemented...! ");
    }

    @Override
    public void readyToExecute(ASdu aSdu, int qualifier, Object newState) {
        boolean result = true;
        logEvent(aSdu.toString());
        if (newState instanceof Boolean) {
            result = proxyContext.handleControlCommand(aSdu.getCommonAddress(), qualifier, (boolean) newState);
        } else {
            result = proxyContext.handleControlCommand(aSdu.getCommonAddress(), qualifier, newState);
        }
        if (result) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                sendInterrogationResults(aSdu);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ProxyServer getInstance() {
        if (instance == null) {
            instance = new ProxyServer();
        }
        return instance;
    }

    @Override
    public void connectionIndication(Connection connection) {
        int myConnectionId = connectionIdCounter++;
        logEvent("A client has connected using TCP/IP. Will listen for a StartDT request. Connection ID: " + myConnectionId);
        try {
            connection.waitForStartDT(new ProxyServer(connection, myConnectionId), 5000);
        } catch (IOException e) {
            logEvent("Connection (" + myConnectionId + ") interrupted while waiting for StartDT: " + e.getMessage() + ". Will quit.");
            return;
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        logEvent("Started data transfer on connection (" + myConnectionId + ") Will listen for incoming commands.");
    }

    @Override
    public void serverStoppedListeningIndication(IOException e) {
        logEvent("Server has stopped listening for new connections : \"" + e.getMessage() + "\". Will quit.");
    }

    @Override
    public void newASdu(ASdu aSdu) {
        try {
            switch (aSdu.getTypeIdentification()) {
                //  interrogation command
                //  There are two type of interrorgation commands
                //  substation system interrogation and group interrogation
                //  01. substation system interrogation : to get the complete data set of the substation
                //  02. group interrogation : to get the specific data set of the substation
                case C_IC_NA_1:
                    sendToCC(aSdu, true);
                    logEvent("Got Interrogation Command. Will send circuit breaker status value.\n");
                    sendInterrogationResults(aSdu);
                    break;
                case C_SC_NA_1:
                    sendToCC(aSdu, true);
                    logEvent("Single Command Received.\n");
                    hanldeSingleCommand(aSdu);
                    break;
                case C_SE_NC_1:
                    sendToCC(aSdu, true);
                    logEvent("Single Command Received.\n");
                    handleSetShortFloatCommand(aSdu);
                    break;
                case C_RP_NA_1:
                    sendToCC(aSdu, true);
                    logEvent("Reset Process Command.\n");
                    handleResetProcessCommand(aSdu);
                    break;
                default:
                    logEvent("Got unknown request: " + aSdu + ". Will not confirm it.\n");
            }
        } catch (EOFException e) {
            logEvent("Will quit listening for commands on connection (" + connectionId + ") because socket was closed.");
        } catch (IOException e) {
            logEvent("Will quit listening for commands on connection (" + connectionId + ") because of error: \"" + e.getMessage() + "\".");
        }
    }

    protected void handleResetProcessCommand(ASdu aSdu) {
        if (aSdu.getInformationObjects() != null && aSdu.getInformationObjects()[0] != null && aSdu.getInformationObjects()[0].getInformationElements() != null
                && aSdu.getInformationObjects()[0].getInformationElements()[0] != null &&
                aSdu.getInformationObjects()[0].getInformationElements()[0][0] != null) {
              IeQualifierOfResetProcessCommand ieQualifierOfResetProcessCommand = (IeQualifierOfResetProcessCommand) aSdu.getInformationObjects()[0].getInformationElements()[0][0];
//            if (securityHandler != null && ieQualifierOfResetProcessCommand.getValue() == 255) {
//                securityHandler.resetAll();
//            }
        }
    }

    protected synchronized void hanldeSingleCommand(ASdu aSdu) throws IOException {
        if (aSdu.getInformationObjects() != null && aSdu.getInformationObjects()[0] != null && aSdu.getInformationObjects()[0].getInformationElements() != null
                && aSdu.getInformationObjects()[0].getInformationElements()[0] != null &&
                aSdu.getInformationObjects()[0].getInformationElements()[0][0] != null) {
            IeSingleCommand ieSingleCommand = (IeSingleCommand) aSdu.getInformationObjects()[0].getInformationElements()[0][0];
            int qualifier = ieSingleCommand.getQualifier();
            boolean state = ieSingleCommand.isCommandStateOn();
            if (LOCAL_API_MODE) {
                readyToExecute(aSdu, qualifier, state);
            }
        }
    }

    public synchronized void sendInterrogationResults(ASdu aSdu) throws IOException {
        ProxyInformation proxyInformation = proxyContext.getIntegrationData(aSdu.getCommonAddress());
        if (proxyInformation.getDeviceType() == DeviceType.ROOT) {
            for (ProxyInformation information : proxyInformation.getDeviceInfor()) {
                deliver(aSdu, InformationASduBridge.getInformationObject(information), InformationASduBridge.getASDUTypeID(information));
            }
        } else {
            deliver(aSdu, InformationASduBridge.getInformationObject(proxyInformation), InformationASduBridge.getASDUTypeID(proxyInformation));
        }
    }

    protected void handleSetShortFloatCommand(ASdu aSdu) throws IOException {
        if (aSdu.getInformationObjects() != null &&
                aSdu.getInformationObjects()[0] != null &&
                aSdu.getInformationObjects()[0].getInformationElements() != null &&
                aSdu.getInformationObjects()[0].getInformationElements()[0] != null &&
                aSdu.getInformationObjects()[0].getInformationElements()[0][0] != null) {
            IeShortFloat ieShortFloat = (IeShortFloat) aSdu.getInformationObjects()[0].getInformationElements()[0][0];
            IeQualifierOfSetPointCommand ieQualifierOfSetPointCommand = (IeQualifierOfSetPointCommand) aSdu.getInformationObjects()[0].getInformationElements()[0][1];
            int qualifier = ieQualifierOfSetPointCommand.getQl();
            float value = ieShortFloat.getValue();
            if (LOCAL_API_MODE) {
                readyToExecute(aSdu, qualifier, new Float(value));
            }
//          else {
//              securityHandler.validateAndExecute(this, proxyContext, aSdu, qualifier, new Float(value));
//          }
        }
    }

    private void deliver(ASdu aSdu, List<InformationObject> informationObjectList, TypeId typeId) throws IOException {
        InformationObject[] informationObjects = null;
        if (informationObjectList != null) {
            informationObjects = new InformationObject[informationObjectList.size()];
            int i = 0;
            for (InformationObject informationObject : informationObjectList) {
                informationObjects[i] = informationObject;
                i++;
            }
        }
        ASdu resultAsdu = new ASdu(typeId, true, CauseOfTransmission.SPONTANEOUS, false, false, 0, aSdu.getCommonAddress(), informationObjects);
        logEvent("resultAsdu = " + resultAsdu);
        sendToCC(resultAsdu, false);
    }

    private void sendToCC(ASdu asduToCC, boolean confirmation) {
        if (confirmation) {
            ASdu aSdu = asduToCC;
            CauseOfTransmission cot = aSdu.getCauseOfTransmission();
            if (cot == CauseOfTransmission.ACTIVATION) {
                cot = CauseOfTransmission.ACTIVATION_CON;
            } else if (cot == CauseOfTransmission.DEACTIVATION) {
                cot = CauseOfTransmission.DEACTIVATION_CON;
            }
            asduToCC = new ASdu(aSdu.getTypeIdentification(), aSdu.isSequenceOfElements(), cot, aSdu.isTestFrame(),
                    aSdu.isNegativeConfirm(), aSdu.getOriginatorAddress(), aSdu.getCommonAddress(),
                    aSdu.getInformationObjects());
        }
        try {
            if (connection == null) {
                iec60870104ConnectionWrapper.send(asduToCC);
                logEvent("DELIVERED iec60870104ConnectionWrapper = " + asduToCC);
            } else {
                connection.send(asduToCC);
                logEvent("DELIVERED asduToCC = " + asduToCC);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionClosed(IOException e) {
        logEvent("Connection (" + connectionId + ") was closed. " + e.getMessage());
    }

    @Override
    public void connectionAttemptFailed(IOException e) {
        logEvent("Connection attempt failed: " + e.getMessage());
    }

    public void start() {
        if (serverSap == null && LOCAL_API_MODE) {
            serverSap = new ServerSap(ConfigUtil.GATEWAY_CC_PORT, this);
            try {
                serverSap.startListening();
            } catch (IOException e) {
                System.out.println("Unable to startProxy listening: \"" + e.getMessage() + "\". Will quit.["
                        + ConfigUtil.GATEWAY_CC_PORT + "]");
            }
            ControlCenter.getInstance().processAndAddLogMessage("Proxy Gateway Started...!");
        }
    }

    public void stop() {
        if (serverSap != null) {
            try {
                serverSap.stopListening();
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSap = null;
        }
        instance = null;
    }

    public static void main(String[] args) {
        new ProxyServer().start();
    }

    public void setConnectionWrapper(IEC60870104ConnectionWrapper iec60870104ConnectionWrapper) {
        this.iec60870104ConnectionWrapper = iec60870104ConnectionWrapper;
    }

    public static void setPRXEventListener(LogEventListener proxyLogEventListener) {
        logEventListener = proxyLogEventListener;
    }
}
