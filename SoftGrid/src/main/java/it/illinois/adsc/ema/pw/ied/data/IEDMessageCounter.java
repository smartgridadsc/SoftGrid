//package it.ilinois.adsc.ema.pw.ied.data;
//
//import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
//import it.illinois.adsc.ema.pw.log.IEDLogFormatter;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.logging.*;
//
///**
// * Created by prageethmahendra on 22/6/2016.
// */
//public class IEDMessageCounter {
//
//    private static Logger logger = null;
//    private static int msgCount = 0;
//    private static double totalMsgCount = 0;
//    private static int total = 0;
//    private static int numberOfSeconds = 0;
//    private static int maxCount = 0;
//    private static Timer uploadCheckerTimer = new Timer(true);
//    private static long execTime;
//    private static double totalExecTime;
//
//    public static void logMessageReceived(String message, long duration) {
//        execTime = duration;
//        if (logger == null) {
//            initLogger();
//            runScheduler();
//        }
//        totalMsgCount++;
//        totalExecTime += duration;
//        msgCount++;
//
//    }
//
//    private static void runScheduler() {
//        uploadCheckerTimer.schedule(
//                new TimerTask() {
//                    public void run() {
//                        total += msgCount;
//                        numberOfSeconds++;
//                        maxCount = Math.max(maxCount, msgCount);
//                        logger.info("!SEC! = " + msgCount + " Max = " + maxCount + " Avg = " + (int) total / numberOfSeconds + " Avg ExecTime = "+(totalExecTime/1000)/totalMsgCount);
//                        msgCount = 0;
//                    }
//                }, 100, 1000);
//    }
//
//    private static synchronized void initLogger() {
//        FileHandler fileTxt = null;
//        Formatter formatterTxt;
//
//        if (logger != null) {
//            return;
//        }
//        // suppress the logging output to the console
//        logger = Logger.getLogger("IEDMSGCount");
//        logger.setLevel(Level.INFO);
//        try {
//            fileTxt = new FileHandler( getLogFolder() + "\\" + ConfigUtil.MESSAGE_MONITOR_LOG_FILE, 1000000, 2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (fileTxt != null) {
//            // create a TXT formatter
//            logger.addHandler(fileTxt);
//        }
//        formatterTxt = new IEDLogFormatter(true);
//        fileTxt.setFormatter(formatterTxt);
//        logger.addHandler(new ConsoleHandler());
//        // create an HTML formatter
//    }
//
//    public static String getLogFolder() {
//        File file = new File(ConfigUtil.LOG_FILE);
//        String logPath = file.getAbsolutePath().replace(file.getName(),"");
//        return logPath;
//    }
//}
