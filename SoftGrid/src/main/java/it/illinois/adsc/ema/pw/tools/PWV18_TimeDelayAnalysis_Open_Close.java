package it.illinois.adsc.ema.pw.tools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by prageethmahendra on 3/3/2016.
 */
public class PWV18_TimeDelayAnalysis_Open_Close {
    private static final int FULL_BREAKER_OPEN = 1;
    private static final int FULL_BREAKER_OPEN_TIME_DELAY = 2;
    private static final int FULL_BREAKER_OPEN_DELAY_MITIGATION = 3;
    private static final int FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION = 4;
    //  private static final String AUX_FILE = "C:\\EMA\\SmartPower\\smartpower\\SmartPower\\CONTINGENCY1.aux";
    private static final String AUX_FILE = "C:\\EMA\\New\\smartpower\\SmartPower\\CONTINGENCY_template_v18.aux";
    private static String NEW_AUX_FILE_PATH = "C:\\EMA\\New\\smartpower\\SmartPower\\ContingencyAux\\";
    private static final String REPLACE_TEXT = "<$OPEN_ALL$>";
    private static int MAX_PERCENT = 90;
    private static String filePostFix = "_1";
    public static double MITIGATION_DURATION = 0.7;
    public static double NETWORK_DELAY = 0.2;
    public static double ALLOWED_MITIGATION_COMMAND_COUNT = 3;
    public static int ExperimentCount = 20;

    public static double DelayUpperBound = 1;
    public static int RANDOM_FUNCTION = RandomGenerator.FIXED_5;

    public void generateAAuxForContigency(int mode) {
        List<TransientCommand> transientCommands = new ArrayList<TransientCommand>();
        TransientCommand.RANDOM_DELAY = false;
        //MITIGATION_DURATION = Double.MAX_VALUE;
        switch (mode) {
            case FULL_BREAKER_OPEN_TIME_DELAY:
                TransientCommand.RANDOM_DELAY = true;
                filePostFix = "P_DELAY_RANDOM" + filePostFix + ".aux";
                TransientCommand.MITIGATION = false;
                break;

            case FULL_BREAKER_OPEN_DELAY_MITIGATION:
                TransientCommand.RANDOM_DELAY = true;
                TransientCommand.MITIGATION = true;
                filePostFix = "P_DELAY_RANDOM_MITIGATION" + filePostFix + ".aux";
                break;
            case FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION:
                TransientCommand.COUNTBASED = true;
                //MITIGATION_DURATION = 1.5;
                TransientCommand.RANDOM_DELAY = true;
                TransientCommand.MITIGATION = false;
                filePostFix = "P_DELAY_RANDOM_COUNTBASED_MITIGATION" + filePostFix + ".aux";
                break;
            default:
                filePostFix = "P_NO_DELAY" + filePostFix + ".aux";
        }
        transientCommands = generateCommands(generateKeyValues(), "Branch");
        TransientCommand.CALC_MITIGATION_DURATION = 0.0;
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
        File file = new File(NEW_AUX_FILE_PATH + (MAX_PERCENT == 100 ? "ALL" : String.valueOf(MAX_PERCENT)) + filePostFix);
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
                    transientCommand.handleTimeLimits();
                }
                Collections.sort(transientCommands);
                TransientCommand tempCommand = null;
                List<String> textList = new ArrayList<String>();
                for (TransientCommand transientCommand : transientCommands) {
                    if (TransientCommand.COUNTBASED) {
                        if (ALLOWED_MITIGATION_COMMAND_COUNT - 1 < transientCommands.indexOf(transientCommand)) {
                            TransientCommand.RANDOM_DELAY = true;
                            TransientCommand.MITIGATION = true;
                        }
                        if (!TransientCommand.MITIGATION) {
                            tempCommand = transientCommand;
                        } else if (tempCommand != null) {
                            TransientCommand.CALC_MITIGATION_DURATION = MITIGATION_DURATION + tempCommand.getTimeDelay();
                            tempCommand = null;
                        }
                    }
                    String text = transientCommand.toString();
                    if (text.length() > 0 && !textList.contains(text)) {
                        textList.add(text);
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
        while (!tempKeyValues.isEmpty() && ((keyValues.size() - tempKeyValues.size()) * 100 / keyValues.size()) < MAX_PERCENT) {
            Random random = new Random(System.nanoTime());
            int index = random.nextInt(tempKeyValues.size());
            String key = tempKeyValues.get(index);
            tempKeyValues.remove(key);
            transientCommands.add(new TransientCommand(key, type));
        }
        return transientCommands;
    }

    public static void main(String[] args) {
        List<Configuration> configurations = new ArrayList<Configuration>();
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION, 1.1, 3, 100, 1.5, RandomGenerator.LINEAR));

//        configurations.add(new Configuration(FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION, 1.1, 3, 100, 5, RandomGenerator.FIXED_5_30P ));

//        configurations.add(new Configuration(FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION, 0.7, 3, 100, 1.5, RandomGenerator.LINEAR ));

//        configurations.add(new Configuration(FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION, 1.1, 3, 100, 5, RandomGenerator.LINEAR ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION, 2, 3, 100, 5, RandomGenerator.LINEAR ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION, 3, 3, 100, 5, RandomGenerator.LINEAR ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION, 1.1, 6, 100, 5, RandomGenerator.LINEAR ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION, 1.1, 9, 100, 5, RandomGenerator.LINEAR ));
//
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_TIME_DELAY, 1.1, 3, 100, 1, RandomGenerator.LINEAR ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN, 0.0, 0.00, 0, 100, 0 * (0.6 + 0.02), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.02, 0, 100, 2 * (0.01 + 0.02), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.2, 0, 100, 2 * (0.6 + 0.2), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.2, 0, 100, 2 * (0.01 + 0.2), RandomGenerator.LINEAR));

//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.02, 0, 100, 3 * (0.6 + 0.02), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.02, 0, 100, 3 * (0.01 + 0.02), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.2, 0, 100, 3 * (0.6 + 0.2), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.2, 0, 100, 3 * (0.01 + 0.2), RandomGenerator.LINEAR));

//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.02, 0, 100, 4 * (0.6 + 0.02), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.02, 0, 100, 4 * (0.01 + 0.02), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.2, 0, 100, 4 * (0.6 + 0.2), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.2, 0, 100, 4 * (0.01 + 0.2), RandomGenerator.LINEAR));

//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.02, 0, 100, 5 * (0.6 + 0.02), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.02, 0, 100, 5 * (0.01 + 0.02), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.2, 0, 100, 5 * (0.6 + 0.2), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.2, 0, 100, 5 * (0.01 + 0.2), RandomGenerator.LINEAR));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_COUNTBASED_DELAY_MITIGATION, 1.1, 0.0, 3, 100, 5, RandomGenerator.NU_FIRST));
//todo
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.02, 0, 100, 1, RandomGenerator.NU_FIRST ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.02, 0, 100, 1, RandomGenerator.NU_FIRST ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.2, 0, 100, 1, RandomGenerator.NU_FIRST ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.2, 0, 100, 1, RandomGenerator.NU_FIRST ));
//
//
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.02, 0, 100, 1, RandomGenerator.NU_LAST ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.02, 0, 100, 1, RandomGenerator.NU_LAST ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.6, 0.2, 0, 100, 1, RandomGenerator.NU_LAST ));
//        configurations.add(new Configuration(FULL_BREAKER_OPEN_DELAY_MITIGATION, 0.01, 0.2, 0, 100, 1, RandomGenerator.NU_LAST ));


        String path = "C:\\EMA\\Experiments\\25-04-2016\\";
        for (Configuration configuration : configurations) {

            MITIGATION_DURATION = configuration.getMitigatinoDuration();
            ALLOWED_MITIGATION_COMMAND_COUNT = configuration.getCommandCount();
            ExperimentCount = configuration.getExperimentCount();
            DelayUpperBound = configuration.getDelayUpperBound();
            NETWORK_DELAY = configuration.getNetworkDelay();
            RANDOM_FUNCTION = RandomGenerator.LINEAR;
            NEW_AUX_FILE_PATH = path + "ContingencyAux_" + configuration + "_" + configurations.indexOf(configuration) + "Index\\";
            System.out.println(NEW_AUX_FILE_PATH);
            try {
                Files.createDirectories(Paths.get(NEW_AUX_FILE_PATH));
                Files.createDirectories(Paths.get(NEW_AUX_FILE_PATH + "csv\\"));
                Files.createDirectories(Paths.get(NEW_AUX_FILE_PATH + "result\\"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            PWV18_TimeDelayAnalysis_Open_Close pwTimeDelayAnalysis = new PWV18_TimeDelayAnalysis_Open_Close();
            MAX_PERCENT = 10;
//            for (MAX_PERCENT = 10; MAX_PERCENT <= 50; MAX_PERCENT += 10) {
                for (int i = 1; i <= ExperimentCount; i++) {
                    filePostFix = "_" + i;
                    pwTimeDelayAnalysis.generateAAuxForContigency(configuration.getType());
                }
//            }
        }
    }
}