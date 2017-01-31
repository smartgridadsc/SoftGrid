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
package org.openmuc.j60870.loger;

import it.illinois.adsc.ema.softgrid.common.IEDLogFormatter;
import org.openmuc.j60870.job.MessageHandler;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

/**
 * Created by prageethmahendra on 28/6/2016.
 */
public class PRXMessageCounter {
    public static final int RECEIVED = 0;
    public static final int CONF = 1;
    public static final int SEND = 2;
    public static final int SEC_CONF = 3;
    private static Logger logger = null;
    static int maxReceived, totalReceived, count, avgReceived, received, conf, send, secConf;
    private static Timer uploadCheckerTimer;
    final static PRXMessageCounter prxMessageCounter = new PRXMessageCounter();

    private static void runScheduler() {
        synchronized (prxMessageCounter) {
            if (uploadCheckerTimer != null) {
                return;
            }
            uploadCheckerTimer = new Timer(true);
            uploadCheckerTimer.schedule(
                    new TimerTask() {
                        public void run() {
                            count++;
                            maxReceived = Math.max(maxReceived, received);
                            avgReceived = totalReceived / count;

                            logger.info("COUNT = " + count + "TOT = " + totalReceived + " AVG= " + avgReceived + " R=" + received + " QSize = " + MessageHandler.getInstance().getQueueSize());
                            received = 0;
                            conf = 0;
                            send = 0;
                            secConf = 0;
                        }
                    }, 100, 1000);
        }
    }


    public static void logMessage(int type) {
        if (logger == null) {
            initLogger();
            runScheduler();
        }
        switch (type) {
            case RECEIVED:
                received++;
                totalReceived++;
                break;
            case CONF:
                conf++;
                break;
            case SEND:
                send++;
                break;
            case SEC_CONF:
                secConf++;
                break;
        }
    }

    private static synchronized void initLogger() {
        FileHandler fileTxt = null;
        Formatter formatterTxt;
        if (logger != null) {
            return;
        }
//      suppress the logging output to the console
        logger = Logger.getLogger("Gateway");
        logger.setLevel(Level.INFO);
        try {
            fileTxt = new FileHandler("GatewayLog.log", 1000000, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileTxt != null) {
//          create a TXT formatter
            logger.addHandler(fileTxt);
        }
        formatterTxt = new IEDLogFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(new ConsoleHandler());
//      create an HTML formatter
    }
}
