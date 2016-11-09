//package org.openmuc.j60870;
//
//import it.illinois.adsc.ema.control.SmartPowerControler;
//import it.illinois.adsc.ema.control.proxy.server.ProxyServer;
//import it.illinois.adsc.ema.control.proxy.server.SecurityEventListener;
//import it.illinois.adsc.ema.control.proxy.server.SecurityHandler;
//import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.concurrent.TimeoutException;
//
///**
// * Created by prageethmahendra on 14/6/2016.
// */
//public class SubstationSecureClient implements SecurityEventListener {
//    private static SubstationSecureClient instance;
//    private Connection substationConnection = null;
//    private static ConnectionEventListener connectionEventListener;
//    private static boolean INBUILT_SECURITY_MEDIATION = true;
//
//    private SubstationSecureClient() {
//    }
//
//    public static SubstationSecureClient getInstance() {
//        if (instance == null) {
//            instance = new SubstationSecureClient();
//        }
//        return instance;
//    }
//    public void connect(ConnectionEventListener eventListener) throws IOException {
//        INBUILT_SECURITY_MEDIATION = IEC60870104Server.SECURITY_INBUILT;
//        ProxyServer.LOCAL_API_MODE = INBUILT_SECURITY_MEDIATION;
//        if (INBUILT_SECURITY_MEDIATION) {
//            SecurityHandler.getInstance().addSecurityEventListener(this);
//            ConfigUtil.SERVER_TYPE = "PRX";
//            String[] args = {"-f", ConfigUtil.CONFIG_PEROPERTY_FILE,  ConfigUtil.SERVER_TYPE};
//            ProxyServer.getInstance().setConnectionWrapper(new IEC60870104ConnectionWrapper(eventListener));
//            SmartPowerControler.initiate(args);
//        } else {
//            connectionEventListener = eventListener;
//            InetAddress address;
//            try {
//                address = InetAddress.getByName(IEC60870104Server.substationAddress);
//            } catch (UnknownHostException e) {
//                System.out.println("Unknown host: " + IEC60870104Server.substationAddress);
//                return;
//            }
//            connectToSubstation(address, eventListener);
//        }
//
//    }
//
//    private void connectToSubstation(InetAddress address, ConnectionEventListener eventListener) {
//        try {
//            ClientSap clientSap = new ClientSap();
//            final Connection clientConnection = clientSap.connect(address, IEC60870104Server.port);//, InetAddress.getByName("10.0.1.8"), 2434);
//            Runtime.getRuntime().addShutdownHook(new Thread() {
//                @Override
//                public void run() {
//                    clientConnection.close();
//                }
//            });
//            substationConnection = clientConnection;
//            System.out.println("Security event listener attached...!");
//            SecurityHandler.getInstance().addSecurityEventListener(this);
//            clientConnection.startDataTransfer(eventListener, 5000);
//            System.out.println("successfully connected. : " + IEC60870104Server.substationAddress + " Port : " + IEC60870104Server.port);
//        } catch (IOException e) {
//            System.out.println("Error in connection : Security Module --> Substation : " + IEC60870104Server.substationAddress + ".");
//            e.printStackTrace();
//        } catch (TimeoutException e2) {
//            System.out.println("starting data transfer timed out.");
//            e2.printStackTrace();
//        }
//    }
//
//    @Override
//    public void readyToExecute(ASdu aSdu) {
//        if (INBUILT_SECURITY_MEDIATION) {
//            ProxyServer.getInstance().newASdu(aSdu);
//        } else {
//            if (substationConnection == null) {
//                try {
//                    connect(connectionEventListener);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                substationConnection.send(aSdu);
//                System.out.println("***ASDU delivered to Substation...!\n" + aSdu.toString().replace('\n', ','));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void readyToExecute(ASdu aSdu, int qualifier, Object newState) {
//        readyToExecute(aSdu);
//    }
//
//
//}