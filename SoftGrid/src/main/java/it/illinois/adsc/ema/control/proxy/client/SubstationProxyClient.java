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
package it.illinois.adsc.ema.control.proxy.client;

import it.illinois.adsc.ema.control.ied.pw.PWModelDetails;
import it.illinois.adsc.ema.control.proxy.ProxyType;
import it.illinois.adsc.ema.control.proxy.context.ProxyClientContext;
import it.illinois.adsc.ema.control.proxy.context.ProxyContextFactory;
import it.illinois.adsc.ema.control.proxy.infor.ProxyInformation;
import it.illinois.adsc.ema.control.proxy.infor.ProxyVariant;
import it.illinois.adsc.ema.control.proxy.server.ProxyServer;
import it.illinois.adsc.ema.control.proxy.util.ProxyClientUtil;
import it.illinois.adsc.ema.control.proxy.util.ProxyTimeLoger;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import org.openmuc.openiec61850.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Prageeth Mahendra
 */
public class SubstationProxyClient implements ClientEventListener, PowerProxyClient {
    private final static Logger logger = LoggerFactory.getLogger(SubstationProxyClient.class);
    private PWModelDetails modelDetails;
    private static ProxyClientContext proxyClientContext = null;
    private ServerModel serverModel;
    private ClientAssociation association;
    private long startTime = System.currentTimeMillis();
    private int iedID;
    private boolean connectedToBus;

    public SubstationProxyClient(int iedID) {
        this.iedID = iedID;
    }

    public void init(ProxyType proxyType) {
        proxyClientContext = ProxyContextFactory.getInstance().getProxyContext(iedID, this);
        ProxyServer.getInstance().start();
    }

    @Override
    public List<ProxyInformation> interrogationRequest(int commonAddress) {
        System.out.println(serverModel);
        System.out.println(modelDetails.getModelNodeReference());
        List<ProxyInformation> proxyInformations = null;
        if (serverModel == null) {
            try {
                startProxy(modelDetails);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServiceError serviceError) {
                serviceError.printStackTrace();
            }
            if (serverModel == null) {
                return proxyInformations;
            }
        }
        String postFix = "";
        proxyInformations = new ArrayList<ProxyInformation>();
        if (modelDetails.getDataKeyValueFields() != null) {
            for (String postfix : modelDetails.getDataKeyValueFields().keySet()) {
                FcModelNode modelNode = findModelNode(serverModel, modelDetails.getModelNodeReference() + postfix);
                if (modelNode == null) {
                    System.out.println("Null ModelNode");
                    continue;
                }
                logger.info("Re-Loading..." + modelDetails.getModelNodeReference());
                System.out.println("Re-Loading..." + modelDetails.getModelNodeReference());
                try {
                    ProxyTimeLoger.resetStartTime();
                    association.getDataValues(modelNode);

                    String variableName = modelNode.getName();
                    ProxyInformation proxyInformation = new ProxyInformation();
                    proxyInformation.setParamType(ProxyClientUtil.getObjectVariableType(variableName));
                    proxyInformation.setParameter(variableName);
                    proxyInformation.setVariant(new ProxyVariant());
                    proxyInformation.getVariant().setString(getValue(modelNode));
                    ProxyTimeLoger.logDuration("interrogationRequest");
                    proxyInformation.setDeviceType(ProxyClientUtil.getDeviceType(modelNode));
                    proxyInformation.setIedId(this.iedID);
                    proxyInformations.add(proxyInformation);

                } catch (ServiceError serviceError) {
                    serviceError.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return proxyInformations;
    }

    private FcModelNode findModelNode(ServerModel serverModel, String ref) {
        for (Fc fc : Fc.values()) {
            if (fc.equals(Fc.CO)) {
                continue;
            }
            FcModelNode modelNode = (FcModelNode) serverModel.findModelNode(ref, fc);
            if (modelNode != null) {
                return modelNode;
            }
        }
        return null;
    }

    private String getValue(ModelNode modelNode) {
        if (modelNode == null) {
            return null;
        } else if (modelNode instanceof BdaDoubleBitPos) {
            return String.valueOf(((BdaDoubleBitPos) modelNode).getValue()[0]);
        }else if(modelNode instanceof BdaFloat32)
        {
            return String.valueOf(((BdaFloat32) modelNode).getFloat());
        }
        else {
            return modelNode.toString();
        }
    }

    @Override
    public boolean handleControlCommand(int qualifier, Object valueObject) {
        if (qualifier > 0 && serverModel != null && modelDetails != null) {
            ProxyTimeLoger.resetStartTime();
            FcModelNode modCtlModel = (FcModelNode) serverModel.findModelNode(modelDetails.getModelNodeReference()
                    + ProxyClientUtil.getObjectReference(qualifier), Fc.CO);
            if (modCtlModel != null) {
                ProxyClientUtil.setIedFeildValues(modCtlModel, qualifier, valueObject);
                try {
                    association.setDataValues(modCtlModel);
                    ProxyTimeLoger.logDuration("handleControlCommand");
                    return true;
                } catch (ServiceError serviceError) {
                    serviceError.printStackTrace();
                } catch (IOException serviceError) {
                    serviceError.printStackTrace();
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void newReport(Report report) {
        logger.info("got report with dataset ref: " + report.getDataSet().getReferenceStr());
        // do something with the report
    }

    @Override
    public void associationClosed(IOException e) {
        logger.info("Association was closed");
    }

    @Override
    public int getIedID() {
        return iedID;
    }

    public void setIedID(int iedID) {
        this.iedID = iedID;
    }

    public void startProxy(PWModelDetails modelDetails) throws IOException, ServiceError {
        String usageString = "usage: org.openmuc.openiec61850.SubstationProxyClient <host> <port>";
        this.modelDetails = modelDetails;
        System.out.println(modelDetails.toString());
        String proxyHost = modelDetails.getIpAddress();
        int clientPort = modelDetails.getPortNumber();
        logger.info("Default Proxy Server Port: " + ConfigUtil.GATEWAY_CC_PORT);
        if (proxyHost != null || clientPort > 0) {
            logger.info(usageString);
            logger.info("Default Host Address : " + proxyHost);
            logger.info("Default Client Port: " + clientPort);
        } else {
            return;
        }
        InetAddress address;
        try {
            address = InetAddress.getByName(proxyHost);
        } catch (UnknownHostException e) {
            logger.error("Unknown host: " + proxyHost);
            return;
        }

        ClientSap clientSap = new ClientSap();
        // alternatively you could use ClientSap(SocketFactory factory) to e.g. connect using SSL
        // optionally you can set some association parameters (but usually the default should work):
        // clientSap.setTSelRemote(new byte[] { 0, 1 });
        // clientSap.setTSelLocal(new byte[] { 0, 0 });
        SubstationProxyClient eventHandler = this;

        logger.info("Attempting to connect to server " + proxyHost + " on port " + clientPort);
        try {
            association = clientSap.associate(address, clientPort, null, eventHandler);
        } catch (IOException e) {
            // an IOException will always indicate a fatal exception. It indicates that the association was closed and
            // cannot be recovered. You will need to create a new association using ClientSap.associate() in order to
            // reconnect.
            logger.error("Error connecting to server: " + e.getMessage());
            return;
        }

        try {
            // requestModel() will call all GetDirectory and GetDefinition ACSI services needed to get the complete
            // server model
            serverModel = association.retrieveModel();

        } catch (ServiceError e) {
            logger.error("Service Error requesting model.", e);
            association.close();
            return;
        } catch (IOException e) {
            logger.error("Fatal IOException requesting model.", e);
            return;
        }
        // get the values of all data attributes in the model:
        association.getAllDataValues();
        for (ModelNode modelNode : serverModel.getChildren()) {
            connectedToBus = modelNode.getName().contains("IED_Bus");
        }

    }

    public boolean isConnectedToBus() {
        return connectedToBus;
    }

    public void setConnectedToBus(boolean connectedToBus) {
        this.connectedToBus = connectedToBus;
    }

    public void stop() {
        ProxyServer.getInstance().stop();
    }
}
