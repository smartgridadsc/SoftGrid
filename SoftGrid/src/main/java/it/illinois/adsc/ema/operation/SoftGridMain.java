package it.illinois.adsc.ema.operation;

import it.adsc.smartpower.substatin.concenter.ControlCenterWindow;
import it.adsc.smartpower.substation.monitoring.ui.SPMainFrame;
import it.illinois.adsc.ema.common.webservice.ServiceConfig;
import it.illinois.adsc.ema.webservice.RestServiceApplication;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by prageethmahendra on 30/9/2016.
 */
public class SoftGridMain {
    public static final String MULTICAST_IP = "224.0.0.200";
    public static final int MULTICAST_SOCKET = 6767;

    public static void main(String[] args) {
        try {
            System.out.println("init.....................................!");
            boolean isRemote = args.length > 0 && args[0].equalsIgnoreCase("Remote");
            boolean isWebService = args.length > 0 && args[0].equalsIgnoreCase("Service");
            boolean isServiceClient = args.length > 0 && args[0].equalsIgnoreCase("ServiceClient");
            boolean isProxy = args.length > 0 && args[0].equalsIgnoreCase("PRX");
            boolean isCC = args.length > 0 && args[0].equalsIgnoreCase("CC");
            if (isWebService) {
                ServiceConfig.BASE_URI = args.length >= 2 ? "http://" + args[1] + ":8080/softgrid/" : ServiceConfig.BASE_URI;
                RestServiceApplication.main(args);
            } else if (isServiceClient) {
                ControlCenterWindow.getInstance().displyServiceClient();
            }
            else if(isProxy)
            {
                SPMainFrame.getInstance().displayMonitorWindow();
            }
//            else if(isCC)
//            {
//
//                args =  {"-f", ControlCenter.CONFIG_PEROPERTY_FILE, "CC", "192.168.0.192"};
//                SmartPowerControler.initiate(args);
//            }
            else {
                if (isRemote) {
                    listenToMulticastCommands();
                }
                SPMainFrame.getInstance().displayMonitorWindow();
                if (isRemote) {
                    SPMainFrame.getInstance().removeChartPanel();
                    SPMainFrame.getInstance().startIEDs(args.length >= 2 ? args[1] : null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listenToMulticastCommands() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean exit = false;
                do {
                    if (exit) {
                        break;
                    }
                    MulticastSocket socket = null;
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_IP);
                        socket = new MulticastSocket(MULTICAST_SOCKET);
                        socket.joinGroup(group);
                        while (true) {
                            if (exit) {
                                break;
                            }
                            System.out.println("Listening multicast .. " + MULTICAST_IP + ":" + MULTICAST_SOCKET);
                            byte[] buf = new byte[1000];
                            DatagramPacket recv = new DatagramPacket(buf, buf.length);
                            socket.receive(recv);
                            final String command = new String(buf, 0, buf.length).trim();
                            switch (command) {
                                case "RESET":
                                    System.out.println(">>>>> Reset Command received...!");
                                    exit = true;
                                    SPMainFrame.getInstance().stopIEDServers();
                                    break;
                                case "INIT":
                                    SPMainFrame.getInstance().displayMonitorWindow();
                                    SPMainFrame.getInstance().startIEDs(null);
                                    break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        socket.close();
                    }
                } while (true);
            }
        });
        thread.start();
    }
}
