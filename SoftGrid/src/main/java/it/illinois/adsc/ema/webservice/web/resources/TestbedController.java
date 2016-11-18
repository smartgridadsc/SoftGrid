package it.illinois.adsc.ema.webservice.web.resources;

import it.illinois.adsc.ema.common.webservice.ExperimentRequest;
import it.illinois.adsc.ema.common.webservice.ExperimentResponse;
import it.illinois.adsc.ema.common.webservice.ExperimentStatus;
import it.illinois.adsc.ema.common.webservice.ExperimentType;
import it.illinois.adsc.ema.operation.SoftGridMain;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.webservice.file.FileHandler;

import java.io.*;
import java.net.*;

/**
 * Created by prageethmahendra on 29/9/2016.
 */
public class TestbedController {
    private static final String STATUS_LOG_FILE = "status.log";
    private static Process process = null;

    public static ExperimentResponse initializeIEDs(ExperimentRequest experimentRequest) {
        ExperimentResponse experimentResponse = new ExperimentResponse();
        experimentResponse.setExperimentRequest(experimentRequest);
        experimentResponse.setExperimentStatus(ExperimentStatus.INIT_STATUS);
        if (process == null) {
            System.out.println("Experimenting...!");
            startServerThread("Remote");
        } else {
            broadcastTestbedCommand(ExperimentType.INIT.name());
        }
        return experimentResponse;
    }

    public static String getCurrentStatus(boolean iedStatus) {
        return getCurrentStatus(iedStatus, null, -1);
    }

    public static String getCurrentStatus(boolean iedStatus, String gatewayIP, int gatewayPort) {
        String currentStatus = "ERROR";
        String ipPortString = gatewayIP + ":" + gatewayPort;
        File statusFile = new File(STATUS_LOG_FILE);
        if (statusFile.exists()) {
            FileReader reader = null;
            BufferedReader bufferedReader = null;
            try {
                reader = new FileReader(statusFile);
                bufferedReader = new BufferedReader(reader);
                String tempString = "";
                while ((tempString = bufferedReader.readLine()) != null) {
                    if (!iedStatus && tempString.startsWith("CC") && tempString.endsWith(ipPortString)) {
                        currentStatus = tempString;
                    } else if (iedStatus && !tempString.startsWith("CC")) {
                        currentStatus = tempString;
                    }
                    // go to last status;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return currentStatus;
    }

    private static void startServerThread(final String serverType) {
        Thread iedThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // for source code execution
                    File file = new File(".\\");
                    boolean found = false;
                    String jarFileName = "SoftGrid.jar";
                    if (!file.exists()) {
                        //When the web service is start from the binary jar
                        file = new File(".");
                    }
                    System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
                    for (String localFileName : file.list()) {
                        found = localFileName.startsWith("SoftGrid") && localFileName.endsWith(".jar");
                        if (found) {
                            jarFileName = localFileName;
                            break;
                        }
                    }
                    String batCommands = "cd " + file.getAbsolutePath() + "\n" +
                            "java -jar " + jarFileName + " " + serverType + (FileHandler.LAST_DOWNLOADED_FILE_TYPE.equalsIgnoreCase("CASE_FILE") ? (" " + FileHandler.LAST_DOWNLOADED_FILE) : "");
                    System.out.println("Bat Command...!\n" + batCommands);
                    File batFile = new File(file.getAbsolutePath() + "\\SoftGridStartIEDs.bat");
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
                    process = StartServer(batFile.getAbsolutePath());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        iedThread.start();
    }

    private static Process StartServer(String batfilePath) throws Exception {

//        File file = new File(ConfigUtil.TEMP_STATE_FILE_PATH);
//        file.createNewFile();
        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            URL[] urls = ((URLClassLoader) cl).getURLs();
            for (URL url : urls) {
                System.out.println(url.getFile());
            }

            Runtime runTime = Runtime.getRuntime();
            process = runTime.exec("cmd.exe /k " + batfilePath);
            InputStream inputStream = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            InputStream errorStream = process.getErrorStream();
            InputStreamReader esr = new InputStreamReader(errorStream);

            String n1 = "";
            File IEDLog = new File(ConfigUtil.LOG_FILE);
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
            return process;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void stopIEDServers() {
        if (process != null) {
            try {
                Runtime.getRuntime().exec("TASKKILL /F /FI \"WINDOWTITLE eq SoftGrid*\"");
                Runtime.getRuntime().exec("taskkill /F /IM cmd.exe");
                Runtime.getRuntime().exec("taskkill /F /IM pwrworld.exe");
//                broadcastTestbedCommand(ExperimentType.RESET.name());
                String[] args = new String[0];
            } catch (IOException e) {
                e.printStackTrace();
            }
            process.destroy();
            process.destroyForcibly();
            process = null;
        }
    }

    private static void broadcastTestbedCommand(final String command) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MulticastSocket socket = null;
                DatagramPacket msgPacket = null;
                try {
                    InetAddress group = InetAddress.getByName(SoftGridMain.MULTICAST_IP);
                    socket = new MulticastSocket(SoftGridMain.MULTICAST_SOCKET);
                    socket.joinGroup(group);
                    msgPacket = new DatagramPacket(command.getBytes(),
                            command.getBytes().length, group, SoftGridMain.MULTICAST_SOCKET);
                    socket.send(msgPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}
