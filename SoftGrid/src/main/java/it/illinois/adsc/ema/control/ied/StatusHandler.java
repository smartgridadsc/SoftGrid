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
package it.illinois.adsc.ema.control.ied;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by prageethmahendra on 13/9/2016.
 */
public class StatusHandler {
    public static final String NOTSTARTED = "NOT_STARTED";
    public static final String STARTED = "STARTED";
    public static final String STOPED = "STOPED";
    public static final String INITIATED = "INITIATED";
    public static final String ERROR = "ERROR";
    public static final String Control_Status_Prefix = "CC";
    public static String CURRENT_STATUS = NOTSTARTED;
    private static File file = new File("C:\\EMA\\Demo\\smartpower\\SmartPower\\logs\\status.log");

    public static synchronized void statusChanged(String status) throws Exception {
        if (status != null) {
            status = status.trim().toUpperCase();
            if (status.length() == 0 || !(status.equals(NOTSTARTED) ||
                    status.equals(STARTED) ||
                    status.equals(STOPED) ||
                    status.equals(INITIATED) ||
                    status.equals(ERROR) ||
                    status.startsWith(Control_Status_Prefix))) {
                status = "Invalid Status...[" + status + "]";
            } else {
                CURRENT_STATUS = status;
            }
        } else {
            System.out.println("Invalid Status...");
            throw new Exception("Null Status...");
        }

        if (!file.exists()) {
            file = new File("status.log");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(status + "\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCurrentStatus() {
        return CURRENT_STATUS;
    }

    public static void reset() {
        try {
            if (!file.exists()) {
                file = new File("status.log");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
