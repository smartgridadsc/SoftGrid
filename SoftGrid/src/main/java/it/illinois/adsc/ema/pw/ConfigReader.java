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
package it.illinois.adsc.ema.pw;

import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import java.io.*;
import java.util.Properties;

/**
 * Created by prageethmahendra on 8/8/2016.
 */
public class ConfigReader {
    private static File configFile = null;
    private static String defaultConfigFileName = "config.properties";

    public static Properties getAllProperties(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            file = new File(file.getAbsolutePath() + File.separator + defaultConfigFileName);
        }
        if (file != null && file.exists()) {
            configFile = file;
            if (configFile.exists()) {
                FileInputStream fi = null;
                try {
                    Properties properties = new Properties();
                    fi = new FileInputStream(configFile);
                    properties.load(fi);
                    for (Object key : properties.keySet()) {
                        String keyString = key.toString();
                        String value = properties.getProperty(keyString);
                        updateConfigUtils(keyString, value);
                    }
                    if (ConfigUtil.SERVER_TYPE.equalsIgnoreCase("IED")) {
                        ConfigUtil.init();
                    }
                    return properties;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fi != null) {
                        try {
                            fi.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void updateConfigUtils(final String keyString, final String value) {
        switch (keyString) {
            case "MULTI_IP_IED_MODE_ENABLED":
                ConfigUtil.MULTI_IP_IED_MODE_ENABLED =  value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"));
                break;
            case "ConfFile":
                ConfigUtil.CONFIG_PEROPERTY_FILE = value;
                break;
            case "ServerType":
                ConfigUtil.SERVER_TYPE = value;
                break;
            case "ip":
                ConfigUtil.IP = value;
                break;
            case "CASE_FILE_PATH":
                ConfigUtil.CASE_FILE_PATH = value;
                break;
            case "CASE_FILE_NAME":
                ConfigUtil.CASE_FILE_NAME = value;
                break;
            case "CASE_FILE_TEMP":
                ConfigUtil.CASE_FILE_TEMP = value;
                break;
            case "CASE_FILE_MONITOR":
                ConfigUtil.CASE_FILE_MONITOR = value;
                break;
            case "VIRTUAL_CLOCK_CYCLE_DURATION":
                if (value.length() > 0)
                    try {
                        ConfigUtil.VIRTUAL_CLOCK_CYCLE_DURATION = Integer.parseInt(value);
                        break;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                ConfigUtil.VIRTUAL_CLOCK_CYCLE_DURATION = 20;
                break;
            case "CLOCK_CONTINGENCY_NAME":
                ConfigUtil.CLOCK_CONTINGENCY_NAME = value;
                break;
            case "CASE_FILE_TYPE":
                ConfigUtil.CASE_FILE_TYPE = value;
                break;
            case "SCL_PATH":
                ConfigUtil.SCL_PATH = value;
                break;
            case "GENERATE_SCL":
                ConfigUtil.GENERATE_SCL = value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"));
                break;
            case "PW_TO_SCL_MAPPING":
                ConfigUtil.PW_TO_SCL_MAPPING = value;
                break;
            case "IED_TYPE_TO_FIELD_MAPPING":
                ConfigUtil.IED_TYPE_TO_FIELD_MAPPING = value;
                break;
            case "ACM_SECURITY_PROPERTY_FILE":
                ConfigUtil.ACM_SECURITY_PROPERTY_FILE = value;
                break;
            case "GATEWAY_CC_PORT":
                if (value != null && value.trim().length() > 0) {
                    try {
                        ConfigUtil.GATEWAY_CC_PORT = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ConfigUtil.GATEWAY_CC_PORT = 2404;
                        e.printStackTrace();
                    }
                }
                break;
            case "PROXY_SERVER_LOCAL_API_MODE":
                ConfigUtil.PROXY_SERVER_LOCAL_API_MODE = value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"));
                break;
            case "CC_CONSOLE_INTERACTIVE":
                ConfigUtil.CC_CONSOLE_INTERACTIVE = value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"));
                break;
            case "EXP_DATA_FILE":
                ConfigUtil.EXP_DATA_FILE = value;
                break;
            case "LIMIT_VIOLATION_RECORD_FILE":
                ConfigUtil.LIMIT_VIOLATION_RECORD_FILE = value;
                break;
            case "CONFIG_FOLDER":
                ConfigUtil.CONFIG_FOLDER = value;
                break;
            case "PYTHON_FILE_PATH":
                ConfigUtil.PYTHON_FILE_PATH = value;
                break;
            case "PYTHON_FILE_NAME":
                ConfigUtil.PYTHON_FILE_NAME = value;
                break;
            case "POWER_WORLD_EXE":
                ConfigUtil.POWER_WORLD_EXE = value;
                break;
            case "POWER_WORLD_CLSID":
                ConfigUtil.POWER_WORLD_CLSID = value;
                break;
            case "LOG_FILE":
                ConfigUtil.LOG_FILE = value;
                break;
            case "ConfigFilePath":
                ConfigUtil.CONFIG_FILE_PATH = value;
                break;
        }

    }

}
