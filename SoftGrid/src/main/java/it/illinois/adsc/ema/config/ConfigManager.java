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
