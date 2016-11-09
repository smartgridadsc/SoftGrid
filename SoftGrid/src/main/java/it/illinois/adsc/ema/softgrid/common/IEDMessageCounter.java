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
package it.illinois.adsc.ema.softgrid.common;


import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

/**
 * Created by prageethmahendra on 22/6/2016.
 */
public class IEDMessageCounter {

    private static Logger logger = null;
    private static int msgCount = 0;
    private static double totalMsgCount = 0;
    private static int total = 0;
    private static int numberOfSeconds = 0;
    private static int maxCount = 0;
    private static Timer uploadCheckerTimer = new Timer(true);
    private static long execTime;
    private static double totalExecTime;

    public static void logMessageReceived(String message, long duration) {
        execTime = duration;
        if (logger == null) {
            initLogger();
            runScheduler();
        }
        totalMsgCount++;
        totalExecTime += duration;
        msgCount++;

    }

    private static void runScheduler() {
        uploadCheckerTimer.schedule(
                new TimerTask() {
                    public void run() {
                        total += msgCount;
                        numberOfSeconds++;
                        maxCount = Math.max(maxCount, msgCount);
                        logger.info("!SEC! = " + msgCount + " Max = " + maxCount + " Avg = " + (int) total / numberOfSeconds + " Avg ExecTime = "+(totalExecTime/1000)/totalMsgCount);
                        msgCount = 0;
                    }
                }, 100, 1000);
    }

    private static synchronized void initLogger() {
        FileHandler fileTxt = null;
        Formatter formatterTxt;

        if (logger != null) {
            return;
        }
        // suppress the logging output to the console
        logger = Logger.getLogger("IEDMSGCount");
        logger.setLevel(Level.INFO);
        try {
            fileTxt = new FileHandler( getLogFolder() + "\\" + ConfigUtil.MESSAGE_MONITOR_LOG_FILE, 1000000, 2);
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
        // create an HTML formatter
    }

    public static String getLogFolder() {
        File file = new File(ConfigUtil.LOG_FILE);
        String logPath = file.getAbsolutePath().replace(file.getName(),"");
        return logPath;
    }
}
