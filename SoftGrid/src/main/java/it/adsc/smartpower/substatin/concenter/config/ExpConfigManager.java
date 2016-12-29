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
package it.adsc.smartpower.substatin.concenter.config;

import it.adsc.smartpower.substatin.concenter.ControlCenterWindow;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by prageethmahendra on 30/8/2016.
 */
public class ExpConfigManager {
    public static final String CONFIG_EXP_NAME = "EXP_NAME";
    public static final String CONFIG_DESCRIPTION = "DESCRIPTION";
    public static final String CONFIG_CMD_FILE_PATH = "CMD_FILE_PATH";
    public static final String CONFIG_CASE_FILE = "CASE_FILE";
    public static final String GATEWAY_IP= "GATEWAY_IP";
    public static final String SERVICE_IP= "SERVICE_IP";


    private static ExpConfigManager instance;
    public static final String configFileName = "config.properties";
    private static Properties defaultProperties = new Properties();
    File expDirectory = new File("Experiments");

    private ExpConfigManager() {
        init();
    }

    private void init() {
        defaultProperties.setProperty(CONFIG_EXP_NAME, "");
        defaultProperties.setProperty(CONFIG_DESCRIPTION, "");
        defaultProperties.setProperty(CONFIG_CMD_FILE_PATH, "");
        defaultProperties.setProperty(CONFIG_CASE_FILE, "");
        defaultProperties.setProperty(GATEWAY_IP, "");
        defaultProperties.setProperty(SERVICE_IP, "");

        if (expDirectory.exists() && expDirectory.isDirectory()) {
            expDirectory.mkdir();
        }
    }

    public static ExpConfigManager getInstance() {
        if (instance == null) {
            instance = new ExpConfigManager();
        }
        return instance;
    }

    public String crateExperimentDirector(String expName) {
        String configDir = expDirectory.getAbsolutePath() + "\\" + expName;
        System.out.println("configDir = " + configDir);
        File newConfigDir = new File(configDir);
        if (!newConfigDir.mkdir()) {
            System.out.println("Unable to create the config folder.");
            return null;
        }
        String configFilePath = configDir + "\\" + configFileName;
        File newConfigProperties = new File(configFilePath);
        try {
            newConfigProperties.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter propertyWriter = null;
        try {
            propertyWriter = new FileWriter(newConfigProperties);
            defaultProperties.store(propertyWriter, "");
            return configDir;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (propertyWriter != null) {
                try {
                    propertyWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public HashMap<String, String> getExperimentList() {
        HashMap<String, String> expNames = new HashMap<String, String>();
        if(!expDirectory.exists())
        {
            try {
                expDirectory.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (File expFile : expDirectory.listFiles()) {
            expNames.put(expFile.getAbsolutePath(), expFile.getName());
        }
        return expNames;
    }

    public void removeExperimentConfig(String expName) {
        String configDir = expDirectory.getAbsolutePath() + "\\" + expName;
        System.out.println("configDir = " + configDir);
        File confiDir = new File(configDir);
        if (confiDir.exists() && confiDir.isDirectory()) {
            deleteFolder(confiDir);
        }
    }

    private void deleteFolder(File dir) {
        if (dir.exists())
            if (dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    deleteFolder(file);
                }
            }
        if (!dir.delete()) {
            JOptionPane.showMessageDialog(ControlCenterWindow.getInstance(), "File in use. Unable to delete.\n" + dir.getAbsolutePath());
        }
    }
}
