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
package it.illinois.adsc.ema.config;

import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by prageethmahendra on 4/8/2016.
 * This class read the configuration file
 */
public class ConfigManager {
    private static Properties properties;
    private static Properties securityProerties;
    private static String configFileName = "C:\\EMA\\Demo\\smartpower\\smartpower\\SmartPower\\config.properties";

    /**
     * initialize the config parameters
     */
    public static void init() {
        File file = new File(configFileName);
        if (!file.exists()) {
            // execute from source code project and the config files is in the SmartPower Folder.
            String projectConfigFile = "..//SmartPower//config.properties";
            file = new File(projectConfigFile);
        }
        if (file.exists()) {
            properties = new Properties();
            try {
                properties.load(new FileReader(file));
            } catch (IOException e) {
                e.printStackTrace();
                properties = null;
            }
        } else {
            System.out.println("Unable to find the " + configFileName + " file.");
        }
    }

    public static void initSecurityConfig() {
        String securityConfigFileName = getConfigValue("security.config.file");
        if (securityConfigFileName == null || securityConfigFileName.isEmpty()) {
            System.out.println("Missing configuration security.config.file.");
            return;
        }
        File file = new File(securityConfigFileName);
        if (!file.exists()) {
            // execute from source code project and the config files is in the SmartPower Folder.
            String projectConfigFile = "..//SmartPower//" + securityConfigFileName;
            file = new File(projectConfigFile);
        }
        if (file.exists()) {
            securityProerties = new Properties();
            try {
                securityProerties.load(new FileReader(file));
            } catch (IOException e) {
                e.printStackTrace();
                securityProerties = null;
            }
        } else {
            System.out.println("Unable to find the " + securityConfigFileName + " file.");
        }
    }

    public static String getConfigValue(String key) {
        if (properties == null) {
            init();
        }
        return properties == null ? null : properties.getProperty(key);
    }

    public static String getSecurityConfigValue(String key) {
        if (securityProerties == null) {
            initSecurityConfig();
        }
        return securityProerties == null ? null : securityProerties.getProperty(key);
    }

    public static void updateConfigUtil()
    {
        ConfigUtil.CONFIG_PEROPERTY_FILE = getConfigValue("ConfFile");
    }
}
