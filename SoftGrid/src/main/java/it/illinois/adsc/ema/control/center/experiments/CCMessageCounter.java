package it.illinois.adsc.ema.control.center.experiments;

import it.illinois.adsc.ema.softgrid.common.IEDLogFormatter;
import org.openmuc.j60870.ASdu;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.*;
import java.util.logging.Formatter;

/**
 * Created by prageethmahendra on 22/6/2016.
 */
public class CCMessageCounter {
    public static boolean RECEIVED = true;
    private Logger logger = null;
    private static int msgCount = 0;
    private static int avgCount = 0;
    private static int tickCount = 1;
    private static int totMsgCount = 0;
    private long startTime = System.currentTimeMillis();
    private long confTime = System.currentTimeMillis();
    private long responseTime = System.currentTimeMillis();
    private static long maxResponseTime = 0;
    private static volatile double totalResponseTime = 0;
    private static long avgResponseTime = 0;
    private static Timer uploadCheckerTimer = null;
    private static ConcurrentLinkedQueue<AsduCommand> asduCommandStack = new ConcurrentLinkedQueue<AsduCommand>();
    private static HashMap<String, Integer> addressCount = new HashMap<String, Integer>();

    public static volatile boolean SLOW = false;
    public static volatile int SENT = 1000;

    public void logMessageReceived(ASdu aSdu) {
        if (logger == null) {
            initLogger();
            runScheduler();
        }
        boolean nanosecond = true;
        long times = (nanosecond ? 1000000 : 1);
        long delay = 1500 * times;

        if (aSdu != null) {
            if (aSdu.getTypeId().name().equals("M_ME_NC_1") || aSdu.getTypeId().name().startsWith("M_ME")) {
//               synchronized (addressCount) {
//                   String IOA = String.valueOf(aSdu.getCommonAddress());
//                   if (addressCount.get(IOA) == null) {
//                       addressCount.put(IOA, 1);
//                   } else {
//                       addressCount.put(IOA, addressCount.get(IOA) - 1);
//                   }
//               }
//              response
                RECEIVED = true;
                SLOW = asduCommandStack.size() > 100;
                if (SLOW) {
//                 synchronized (addressCount) {
//                     int count = 0;
//                     List<String> removable = new ArrayList<>();
//                     for (String ioa : addressCount.keySet()) {
//                         if(addressCount.get(ioa) >20)
//                         {
//                             count++;
//                             logger.info("ioa = " + ioa + " Count = "+ addressCount.get(ioa));
//                         }
//                         if(Integer.parseInt(ioa) > 400)
//                         {
//                             removable.add(ioa);
//                         }
//                     }
//                     for (String s : removable) {
//                         addressCount.remove(s);
//                     }
//                     logger.info(">>>>>>>>>>>>>>> Count = "+ count);
//                 }
                }
                AsduCommand asduCommand = null;
                asduCommand = asduCommandStack.poll();
                if (asduCommand == null) {
                    logger.info("Response arrived before enqueu...!");
                    totMsgCount++;
                    totalResponseTime += 10000;
                    return;
                }
                responseTime = (nanosecond ? System.nanoTime() : System.currentTimeMillis()) - asduCommand.getSentTimeMillis();
                if (responseTime <= 0) {
                    logger.info(String.valueOf(asduCommand.getSentTimeMillis() + " , " + (nanosecond ? System.nanoTime() : System.currentTimeMillis())));
                } else {
                    if (responseTime > delay) {
                        responseTime = responseTime - delay;
                    }
                    if (totMsgCount > 50000) {
                        totMsgCount = 1;
                        totalResponseTime = 0;
                    }
                    logger.info(String.valueOf(responseTime / times) + /*" , " + String.valueOf(avgResponseTime / times) + " , " + String.valueOf(msgCount / tickCount) + */" , " + String.valueOf(totMsgCount) + " , " + asduCommandStack.size());
                    totMsgCount++;
                    if (totMsgCount > 50000 || avgResponseTime > 0) {
                        totMsgCount = avgResponseTime == 0 ? 1 : totMsgCount;
                        totalResponseTime += responseTime / times;
                        avgResponseTime = (long) (totalResponseTime * times) / totMsgCount;
                        maxResponseTime = Math.max(maxResponseTime, responseTime);
                    }
                }
            } else if (aSdu.getCauseOfTransmission().name().endsWith("_CON")) {
//              confirmation
                confTime = nanosecond ? System.nanoTime() : System.currentTimeMillis();
            } else {
//              sent
                startTime = nanosecond ? System.nanoTime() : System.currentTimeMillis();
                asduCommandStack.add(new AsduCommand(aSdu));
//              synchronized (addressCount) {
//                  String IOA = String.valueOf(aSdu.getCommonAddress());
//                  if (addressCount.get(IOA) == null) {
//                      addressCount.put(IOA, 1);
//                  } else {
//                      addressCount.put(IOA, addressCount.get(IOA) + 1);
//                  }
//              }
                msgCount++;
            }
        }
        return;
    }

    private synchronized void runScheduler() {
        if (uploadCheckerTimer != null) {
            return;
        }
        uploadCheckerTimer = new Timer(true);
        uploadCheckerTimer.schedule(
            new TimerTask() {
                public void run() {
//                  logger.info("!SEC! Sent=" + msgCount + ", Total Sent = " + totMsgCount + ", avgResTime= " + avgResponseTime + ", maxResTime = " + maxResponseTime);
//                  logger.info(msgCount + " , " + avgResponseTime/1000000);
                    avgCount = totMsgCount / tickCount;
                    tickCount++;
                }
            }, 100, 1000);
    }


    private synchronized void initLogger() {
        FileHandler fileTxt = null;
        Formatter formatterTxt;
        if (logger != null) {
            return;
        }
        // suppress the logging output to the console
        logger = Logger.getLogger("CC");
        logger.setLevel(Level.INFO);
        try {
            fileTxt = new FileHandler("CCMessage.log", 100000000, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileTxt != null) {
            // create a TXT formatter
            logger.addHandler(fileTxt);
        }
        formatterTxt = new IEDLogFormatter(true);
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(new ConsoleHandler());
    }
}
