package it.adsc.smartpower.substatin.concenter;

import it.adsc.smartpower.substatin.concenter.config.ExpConfigManager;

import java.util.Properties;

/**
 * Created by prageethmahendra on 29/8/2016.
 */
public class Experiment {
    private String name;
    private String configPath;
    private Properties properties;
    private String gatewayIP;

    public Experiment(String name, String configPath) {
        this.name = name;
        this.configPath = configPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigPath() {
        return configPath;
    }

    public String getConfigFilePath() {
        return configPath + "\\config.properties";
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getScriptFilePath() {
        return configPath + "\\" + properties.get(ExpConfigManager.CONFIG_CMD_FILE_PATH);
    }

    public String getDownloadPath() {
        return configPath + "\\logs_downloaded\\";
    }

    public String getCaseFilePath() {
        return configPath + "\\" + properties.getProperty(ExpConfigManager.CONFIG_CASE_FILE);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getGatewayIP() {
        return properties.getProperty(ExpConfigManager.GATEWAY_IP);
    }

    public void setGatewayIP(String gatewayIP) {
        properties.setProperty(ExpConfigManager.GATEWAY_IP, gatewayIP);
    }
}
