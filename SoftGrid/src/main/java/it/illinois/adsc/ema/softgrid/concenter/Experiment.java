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
package it.illinois.adsc.ema.softgrid.concenter;

import it.illinois.adsc.ema.softgrid.concenter.config.ExpConfigManager;

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

    public void copyProperties(Properties expConfigProperties) {
        if (properties == null || expConfigProperties == null) {
            return;
        }
        for (Object key : expConfigProperties.keySet()) {
            properties.put(key, expConfigProperties.get(key));
        }
    }
}
