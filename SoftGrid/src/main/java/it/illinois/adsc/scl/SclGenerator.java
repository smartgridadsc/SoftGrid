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
package it.illinois.adsc.scl;

import com.alee.utils.FileUtils;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.pw.com4j.PWCom_com4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prageethmahendra on 26/7/2016.
 */
public class SclGenerator {
    private static List<String> deviceTypes = new ArrayList<String>();

    public static void generateSCLFiles() {
        generateSCLFiles(null);
    }

    public static void generateSCLFiles(String caseFile) {

        if (!ConfigUtil.GENERATE_SCL) {
            return;
        }

        deviceTypes.add("BUS");
        deviceTypes.add("CB");
        deviceTypes.add("GEN");
        deviceTypes.add("LOAD");
        deviceTypes.add("SHUNT");
        deviceTypes.add("TRANSFORMER");

//        if (caseFile != null && !caseFile.trim().isEmpty()) {
//            System.out.println("CASE_FILE changed to " + caseFile);
//            ConfigUtil.CASE_FILE_NAME = caseFile;
//            ConfigUtil.CASE_FILE_PATH = "";
//        } else {
//            System.out.println("CASE_FILE : " + ConfigUtil.CASE_FILE_NAME);
//        }
        // Handle case file
        String caseFilePath = ConfigUtil.CASE_FILE_NAME;

        // Create input folder
        String inputOutputDirector = ConfigUtil.CONFIG_FOLDER;
        File inputFile = new File(inputOutputDirector + "input\\");
        if (!inputFile.exists()) {
            inputFile.mkdir();
        }

        // Create output folder
        File outputFile = new File(inputOutputDirector + "output\\");
        if (!outputFile.exists()) {
            outputFile.mkdir();
        }
        // Create new scl file template directory
        String baseSCLTemplateDirector = inputOutputDirector + "input\\BASE\\";
        String sclMainDirectory = "TEMP_ICDs";
        File baseDirectory = new File(baseSCLTemplateDirector);
        if (baseDirectory.exists()) {
            File newInputDirectory = new File(inputFile.getAbsolutePath() + File.separator + sclMainDirectory + File.separator);
            if (!newInputDirectory.exists()) {
                newInputDirectory.mkdir();
                FileUtils.copyDirectory(baseDirectory, newInputDirectory);
            }

            // Create new out put scl file directory
            File outputFileDirectory = new File(outputFile.getAbsolutePath() + File.separator + sclMainDirectory + File.separator);
            if (!outputFileDirectory.exists()) {
                outputFileDirectory.mkdir();
            }

            // Load scl file
            System.out.println("Connecting to PowerWorld...!");
            if (PWCom_com4j.getInstance() != null) {
                System.out.println("SUCCESS");
            } else {
                System.out.println("Error in connecting PowerWorld...!");
                return;
            }


            for (String deviceType : deviceTypes) {
                System.out.println("Opening case file...!" + caseFilePath);
                Object res = PWCom_com4j.getInstance().openCase(caseFilePath, false);
                if (res != null && res.toString().isEmpty()) {
                    System.out.println("Successfully opened the case file.");
                }

                    loadKeyValueParameters(deviceType, caseFilePath, newInputDirectory.getAbsolutePath());
                System.out.println("Closing case file...!");
                res = PWCom_com4j.getInstance().closeCase();
                if (res != null && res.toString().isEmpty()) {
                    System.out.println("Successfully closed the case file.");
                }
            }

            System.out.println("Closing PowerWorld Connection...!");
            PWCom_com4j.getInstance().stop();
            System.out.println("PowerWorld meta data loaded...!");
            generateSCL(newInputDirectory.getAbsolutePath() + File.separator, outputFileDirectory.getAbsolutePath() + File.separator);
            copySCLToWorkingDirector(outputFileDirectory.getAbsolutePath() + File.separator);
        } else {
            System.out.println("Base case file not found...!");
            return;
        }
    }

    private static void copySCLToWorkingDirector(String outputFileDirectory) {
        File outputSCLFiles = new File(outputFileDirectory);
        File workingSCLFiles = new File(ConfigUtil.SCL_PATH);
        FileUtils.clearDirectory(workingSCLFiles);
        FileUtils.copyDirectory(outputSCLFiles, workingSCLFiles);
    }

    private static void loadKeyValueParameters(String deviceType, String caseFilePath, String newInputDirectoryPath) {


        String str = PWCom_com4j.getInstance().getFeildValues(deviceType);
        if (str != null) {
            System.out.println("SUCCESS");
        } else {
            System.out.println("Error in loading meta data...!");
            return;
        }
        String parameterFilePath = newInputDirectoryPath + File.separator + deviceType + File.separator + "data.txt";
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(parameterFilePath);
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void generateSCL(String inputPath, String outputPath) {
        File sclFolder = new File(outputPath);
        if (!sclFolder.exists()) {
            sclFolder.mkdir();
        }
        File templatePath = new File(inputPath);
        if (!templatePath.exists()) {
            return;
        }
        for (File file : templatePath.listFiles()) {
            if (!file.isDirectory()) {
                FileUtils.copyFile(file, new File(sclFolder.getAbsoluteFile() + File.separator + file.getName()));
            }
        }
        int count = 0;
        for (String deviceType : deviceTypes) {
            BufferedReader dataReader = null;
            BufferedReader templateReader = null;
            String folder = deviceType.toUpperCase();
            File file = new File(outputPath + deviceType + "\\");

            if (!file.exists()) {
                file.mkdir();
            }

            if (file.exists()) {
                for (String s : file.list()) {

                    File trsFile = new File(outputPath + deviceType + "\\" + s);
                    if (trsFile.exists()) {
                        System.out.println(outputPath + deviceType + "\\" + s);
                        trsFile.delete();
                    }
                }
            }

            try {
                dataReader = new BufferedReader(new FileReader(inputPath + folder + "\\data.txt"));
                String dataLine = "";

                while ((dataLine = dataReader.readLine()) != null) {
                    String[] dataElements;
                    dataElements = dataLine.trim().split(" ");
                    switch (deviceType) {
                        case "CB":
                            dataLine = dataElements[0] + "_" + dataElements[1] + "_" + dataElements[2];
                            break;
                        case "BUS":
                            dataLine = dataElements[0];
                            break;
                        case "GEN":
                            dataLine = dataElements[0] + "_" + dataElements[1];
                            break;
                        case "LOAD":
                            dataLine = dataElements[0] + "_" + dataElements[1];
                            break;
                        case "SHUNT":
                            dataLine = dataElements[0] + "_" + dataElements[1];
                            break;
                        case "TRANSFORMER":
                            dataLine = dataElements[0] + "_" + dataElements[1] + "_" + dataElements[2];
                    }

//                            switch (deviceType) {
//                        case "CB":
//                            dataElements = dataLine.split(" ");
//                            dataLine = dataElements[0] + "_" + dataElements[2] + "_" + dataElements[4];
//                            break;
//                        case "BUS":
//                            dataElements = dataLine.split("\t");
//                            dataLine = dataElements[0];
//                            break;
//                        case "GEN":
//                            dataElements = dataLine.split("\t");
//                            dataLine = dataElements[0] + "_" + dataElements[dataElements.length - 1];
//                            break;
//                        case "LOAD":
//                            dataElements = dataLine.split("\t");
//                            dataLine = dataElements[0] + "_" + dataElements[dataElements.length - 1];
//                            break;
//                        case "SHUNT":
//                            dataElements = dataLine.split("\t");
//                            dataLine = dataElements[0] + "_" + dataElements[2];
//                            break;
//                        case "TRANSFORMER":
//                            dataElements = dataLine.split(",");
//                            dataLine = dataElements[0] + "_" + dataElements[2] + "_" + dataElements[4];
//                            break;
//                    }
                    BufferedWriter sclWriter = null;
                    try {
                        templateReader = new BufferedReader(new FileReader(inputPath + folder + "\\" + deviceType + ".icd"));
                        String scdFileName = deviceType + "_" + dataLine + ".icd";
                        count++;
                        sclWriter = new BufferedWriter(new FileWriter(outputPath + folder + "\\" + scdFileName ));
                        String sclString = "";
                        while ((sclString = templateReader.readLine()) != null) {
//                            if (sclString.contains("<?sclDevice>")) {
//                                sclString = sclString.replace("<?sclDevice>", dataLine);
//                            }
                            if (sclString.contains("<?iedName>")) {
                                sclString = sclString.replace("<?iedName>", scdFileName.replace(".",""));
                            }
//                            if(sclString.contains("<?iedId_DOA>")){
//                                sclString = sclString.replace("<?iedId_DOA>", String.valueOf(count));
//                            }
                            if(sclString.contains("<?ipAddress>")){
                                sclString = sclString.replace("<?ipAddress>", ConfigUtil.IP);
                            }
                            sclWriter.write(sclString);
                        }
                        sclWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            sclWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            templateReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dataReader != null) {
                    try {
                        dataReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
