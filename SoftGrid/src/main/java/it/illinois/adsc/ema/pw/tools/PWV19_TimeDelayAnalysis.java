package it.illinois.adsc.ema.pw.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by prageethmahendra on 3/3/2016.
 */
public class PWV19_TimeDelayAnalysis {
    private static final int FULL_BREAKER_OPEN = 1;
    private static final int FULL_BREAKER_OPEN_TIME_DELAY = 2;
    private static final int FULL_BREAKER_OPEN_MITIGATION = 3;
    //    private static final String AUX_FILE = "C:\\EMA\\SmartPower\\smartpower\\SmartPower\\CONTINGENCY1.aux";
    private static final String AUX_FILE = "C:\\EMA\\New\\smartpower\\SmartPower\\CONTINGENCY_template_v18.aux";
    private static final String NEW_AUX_FILE = "C:\\EMA\\New\\smartpower\\SmartPower\\ContingencyAux\\";
    private static final String REPLACE_TEXT = "<$OPEN_ALL$>";
    private static int MAX_PERCENT = 90;
    private static String filePostFix = "_1";
    public static double MITIGATION_DURATION = 5.0;

    public void generateAAuxForContigency(int mode) {
        List<TransientCommand> transientCommands = new ArrayList<TransientCommand>();
        TransientCommand.RANDOM_DELAY = false;
        //MITIGATION_DURATION = Double.MAX_VALUE;
        switch (mode) {
            case FULL_BREAKER_OPEN_TIME_DELAY:
                TransientCommand.RANDOM_DELAY = true;
                filePostFix = "P_DELAY_RANDOM" + filePostFix + ".aux";
                break;

            case FULL_BREAKER_OPEN_MITIGATION:
                //MITIGATION_DURATION = 1.5;
                TransientCommand.RANDOM_DELAY = true;
                TransientCommand.MITIGATION = true;
                filePostFix = "P_DELAY_RANDOM_MITIGATION" + filePostFix + ".aux";
                break;
            default:
                filePostFix = "P_NO_DELAY" + filePostFix + ".aux";
        }
        transientCommands = generateCommands(generateKeyValues(), "Branch");
        writeToAux(transientCommands);
    }

    private List<String> generateKeyValues() {
        List<String> keys = new ArrayList<String>();
        File file = new File("C:\\EMA\\SmartPower\\smartpower\\SmartPower\\scl\\CB");
        if (file.exists() && file.isDirectory()) {
            for (File sclFile : file.listFiles()) {
                String[] keyValues = sclFile.getName().split("_");
                String values = "\'" + keyValues[1] + "\' \'" + keyValues[2] + "\' \'" + keyValues[3].split("\\.")[0] + "\'\"";
                keys.add(values);
            }
        }

        file = new File("C:\\EMA\\SmartPower\\smartpower\\SmartPower\\scl\\TRANS");
        if (file.exists() && file.isDirectory()) {
            for (File sclFile : file.listFiles()) {
                String[] keyValues = sclFile.getName().split("_");
                String values = "\'" + keyValues[1] + "\' \'" + keyValues[2] + "\' \'" + keyValues[3].split("\\.")[0] + "\'\"";
                keys.add(values);
            }
        }
        return keys;
    }

    private void writeToAux(List<TransientCommand> transientCommands) {
        File dummyFile = new File(AUX_FILE);
        File file = new File(NEW_AUX_FILE + (MAX_PERCENT == 100 ? "ALL" : String.valueOf(MAX_PERCENT)) + filePostFix);
        try {
            if (dummyFile.exists()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                BufferedReader reader = new BufferedReader(new FileReader(dummyFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals(REPLACE_TEXT)) {
                        break;
                    }
                    writer.write(line);
                    writer.write('\n');
                }
                for (TransientCommand transientCommand : transientCommands) {
                    String text = transientCommand.toString();
                    if (text.length() > 0) {
                        writer.write(text);
                        writer.write('\n');
                    }
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

    private List<TransientCommand> generateCommands(List<String> keyValues, String type) {
        List<TransientCommand> transientCommands = new ArrayList<TransientCommand>();
        List<String> tempKeyValues = new ArrayList<String>();
        tempKeyValues.addAll(keyValues);
        while (!tempKeyValues.isEmpty() && ((keyValues.size() - tempKeyValues.size()) * 100 / keyValues.size()) <= MAX_PERCENT) {
            Random random = new Random(System.nanoTime());
            int index = random.nextInt(tempKeyValues.size());
            String key = tempKeyValues.get(index);
            tempKeyValues.remove(key);
            transientCommands.add(new TransientCommand(key, type));
        }
        return transientCommands;
    }

    private void logContingencyValidationReport() {
    }

    public static void main(String[] args) {
        PWV18_TimeDelayAnalysis pwTimeDelayAnalysis = new PWV18_TimeDelayAnalysis();
        for (MAX_PERCENT = 10; MAX_PERCENT <= 100; MAX_PERCENT += 5) {
            for (int i = 1; i <= 10; i++) {
                filePostFix = "_" + i;
                pwTimeDelayAnalysis.generateAAuxForContigency(FULL_BREAKER_OPEN_MITIGATION);
            }
        }

    }
}

