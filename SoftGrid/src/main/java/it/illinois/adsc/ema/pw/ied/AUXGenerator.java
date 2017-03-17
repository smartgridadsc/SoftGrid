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

import com.sun.org.apache.bcel.internal.generic.NEW;
import it.illinois.adsc.ema.pw.PWComFactory;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static it.illinois.adsc.ema.pw.ied.IedControler.LOG_DATA;

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
            do {
                try {
                    Thread.sleep(spentTime += 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (LOG_DATA.isEmpty()) {
                    spentTime = 0;
                }
            }
            while (spentTime < 8000);
            try {
                generateAuxFile();
            } catch (Exception e) {
                System.out.println("Error in generating contingency analysis AUX file.");
            }
        } while (true);
    }

    private void generateAuxFile() {
        if (LOG_DATA.size() > 0) {
            // run extra empty command aux file to generate additional data ( if in-case)
            LOG_DATA = new ArrayList<String>();
            IedControler.RESET_TIME = true;
            count++;
            File oldPWB = new File(NEW_AUX_FILE + (count) % rotation_count + ".PWB");
            File newPWB = new File(NEW_AUX_FILE + (count + 1) % rotation_count + ".PWB");
            PWComFactory.getSingletonPWComInstance().saveState();
            PWComFactory.getSingletonPWComInstance().saveCase(newPWB.getAbsolutePath(), ConfigUtil.CASE_FILE_TYPE, true);
            if (oldPWB.exists()) {
                writeToAux(new File(NEW_AUX_FILE + count % rotation_count + ".aux"), LOG_DATA);
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

    private void writeToAux(File auxFile, List<String> log_data) {
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
