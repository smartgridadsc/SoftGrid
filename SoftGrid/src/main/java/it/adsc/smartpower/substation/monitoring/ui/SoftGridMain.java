//package it.adsc.smartpower.substation.monitoring.ui;
//
//import it.adsc.smartpower.substatin.concenter.ControlCenterWindow;
//import it.adsc.smartpower.substation.monitoring.EntiryFactory;
//import it.illinois.adsc.ema.webservice.RestServiceApplication;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.InetAddress;
//import java.net.MulticastSocket;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
///**
// * Created by prageethmahendra on 30/9/2016.
// */
//public class SoftGridMain {
//    public static final String MULTICAST_IP = "224.0.0.1";
//    public static final int MULTICAST_SOCKET = 6767;
//
//    public static void main(String[] args) {
//        try {
//            System.out.println("init.....................................!");
//            boolean isRemote = args.length > 0 && args[0].equalsIgnoreCase("Remote");
//            boolean isWebService = args.length > 0 && args[0].equalsIgnoreCase("Service");
//            boolean isServiceClient = args.length > 0 && args[0].equalsIgnoreCase("ServiceClient");
//            if(isWebService)
//            {
//                RestServiceApplication.main(args);
//            }
//            else if(isServiceClient)
//            {
//                ControlCenterWindow.getInstance().displyServiceClient();
//            }
//            else {
//                if (isRemote) {
//                    listenToMulticastCommands();
//                }
//                SPMainFrame.getInstance().displayMonitorWindow();
//                if (isRemote) {
//                    SPMainFrame.getInstance().startIEDs();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void listenToMulticastCommands() throws Exception {
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                boolean exit = false;
//                do {
//                    if (exit) {
//                        break;
//                    }
//                    MulticastSocket socket = null;
//                    try {
//                        InetAddress group = InetAddress.getByName(MULTICAST_IP);
//                        socket = new MulticastSocket(MULTICAST_SOCKET);
//                        socket.joinGroup(group);
//                        while (true) {
//                            if (exit) {
//                                break;
//                            }
//                            System.out.println("Listening multicast .. " + MULTICAST_IP + ":" + MULTICAST_SOCKET);
//                            byte[] buf = new byte[1000];
//                            DatagramPacket recv = new DatagramPacket(buf, buf.length);
//                            socket.receive(recv);
//                            final String command = new String(buf, 0, buf.length).trim();
//                            switch (command) {
//                                case "RESET":
//                                    System.out.println(">>>>> Reset Command received...!");
//                                    exit = true;
//                                    SPMainFrame.getInstance().stopIEDServers();
//                                    break;
//                                case "INIT":
//                                    SPMainFrame.getInstance().displayMonitorWindow();
//                                    SPMainFrame.getInstance().startIEDs();
//                                    break;
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        socket.close();
//                    }
//                } while (true);
//            }
//        });
//        thread.start();
//    }
//}
