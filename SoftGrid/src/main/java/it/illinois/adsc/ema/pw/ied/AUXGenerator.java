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
package it.illinois.adsc.ema.pw.ied;

import com.alee.utils.FileUtils;
import it.illinois.adsc.ema.control.db.DBConnection;
import it.illinois.adsc.ema.pw.PWComFactory;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static it.illinois.adsc.ema.pw.ied.IedControler.LOG_DATA;
import static it.illinois.adsc.ema.pw.ied.IedControler.OLD_LOG_DATA;

/**
 * Created by prageethmahendra on 17/5/2016.
 */
public class AUXGenerator implements Runnable {
    private static AUXGenerator auxGenerator = null;
    private static int rotation_count = 10;

    private String NEW_AUX_FILE = "\\auxFile\\ContingencyAux_auxFiles\\";
    private String AUX_FILE = "CONTINGENCY_template_v18.aux";
    private int count = 0;

    private AUXGenerator() {
        super();
        File file = new File(ConfigUtil.LIMIT_VIOLATION_CSV_PATH);
        String auxFilePath = file.getAbsolutePath().replace(file.getName(), "");
        if (new File(auxFilePath).exists()) {
            NEW_AUX_FILE = auxFilePath + "\\" + NEW_AUX_FILE;
            AUX_FILE = auxFilePath + "\\resources\\" + AUX_FILE;
        }
    }

    @Override
    public void run() {
        clearVioloationCounts();
        do {
            long spentTime = 0;
            // Aux files are generated only if there is a control command.
            // control commands executed within last 8 seconds will be added to the aux file
//            do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (LOG_DATA.isEmpty()) {
//                    spentTime = 0;
                continue;
            }
//            }
//            while (spentTime < 8000);
            try {
                generateAuxFile(DBConnection.getConnection().isStable());
            } catch (Exception e) {
                System.out.println("Error in generating contingency analysis AUX file.");
            }
        } while (true);
    }

    private void generateAuxFile(boolean caseFileReset) {
        if (LOG_DATA.size() > 0) {
            // run extra empty command aux file to generate additional data ( if in-case)
            OLD_LOG_DATA.addAll(LOG_DATA);
            LOG_DATA = new ArrayList<String>();
            count++;
            File oldPWB = new File(NEW_AUX_FILE + (count) % rotation_count + ".PWB");
            File newPWB = new File(NEW_AUX_FILE + (count + 1) % rotation_count + ".PWB");
            long duration = 60 + System.currentTimeMillis()/1000 - IedControler.START_TIME/1000;
            if (caseFileReset) {
                IedControler.RESTART_TIMER = true;
                IedControler.START_TIME = System.currentTimeMillis();
                duration = 60 + System.currentTimeMillis()/1000 - IedControler.START_TIME/1000;
                PWComFactory.getSingletonPWComInstance().saveState();
                PWComFactory.getSingletonPWComInstance().saveCase(newPWB.getAbsolutePath(), ConfigUtil.CASE_FILE_TYPE, true);
            } else {
                FileUtils.copyFile(oldPWB, newPWB);
            }
            if(duration > 60)
            {
                System.out.println("proceeding...!");
            }
            if (oldPWB.exists()) {
                writeToAux(new File(NEW_AUX_FILE + count % rotation_count + ".aux"), OLD_LOG_DATA, duration);
            }
            if (caseFileReset) {
                OLD_LOG_DATA = new ArrayList<>();
                caseFileReset = false;
            }
        }
    }

    private void clearVioloationCounts() {
        File file = new File(ConfigUtil.LIMIT_VIOLATION_CSV_PATH);
        if (file.exists()) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeToAux(File auxFile, List<String> log_data, long duration) {
        File templateFile = new File(AUX_FILE);
        try {
            auxFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (templateFile.exists()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(auxFile));
                BufferedReader reader = new BufferedReader(new FileReader(templateFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.contains("<$DURATION$>"))
                    {
                        line = line.replace("<$DURATION$>", String.valueOf(duration) + ".0");
                    }
                    if (line.equals("<$OPEN_ALL$>")) {
                        break;
                    }
                    writer.write(line);
                    writer.write('\n');
                }
                for (String command : log_data) {
                    writer.write(command);
                    writer.write('\n');
                }
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.write('\n');
                }
                writer.flush();
                writer.close();
            } else {
                System.out.println("File not exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void startThread() {
        if (auxGenerator == null) {
            auxGenerator = new AUXGenerator();
            Thread thread = new Thread(auxGenerator);
            thread.start();
        }
    }
}
