package it.illinois.adsc.ema.pw.ied;

import com.sun.org.apache.bcel.internal.generic.NEW;
import it.illinois.adsc.ema.pw.PWComFactory;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
            NEW_AUX_FILE = auxFilePath +"\\"+ NEW_AUX_FILE;
            AUX_FILE = auxFilePath +"\\resources\\"+ AUX_FILE;
        }
    }

    @Override
    public void run() {
        clearVioloationCounts();
        boolean fileCreated = false;
        while (true) {
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<String> LOG_DATA = IedControler.LOG_DATA;
            if (LOG_DATA.size() > 0 || fileCreated) {
                // run extra empty command aux file to generate additional data ( if in-case)
                fileCreated = LOG_DATA.size() > 0;
                IedControler.LOG_DATA = new ArrayList<String>();
                IedControler.RESET_TIME = true;
                count++;
                File oldPWB = new File(NEW_AUX_FILE + (count) % rotation_count + ".PWB");
                File newPWB = new File(NEW_AUX_FILE + (count + 1) % rotation_count + ".PWB");
                PWComFactory.getSingletonPWComInstance().saveState();
                PWComFactory.getSingletonPWComInstance().saveCase(newPWB.getAbsolutePath(), ConfigUtil.CASE_FILE_TYPE, true);
                if (oldPWB.exists()) {
                    writeToAux(new File(NEW_AUX_FILE + count % rotation_count + ".aux"), LOG_DATA);
                    fileCreated = true;
                }
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
