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
package it.adsc.smartpower.substation.monitoring.ui;

import java.util.ArrayList;
import java.util.StringTokenizer;
import it.adsc.smartpower.substation.monitoring.ui.MonitorConfig;

/**
 * Created by prageethmahendra on 5/5/2016.
 */
public class ConfigGenerator {
    private static boolean anotherScript = true;

    public static ArrayList<MonitorConfig> executeNewQuery(String query) {

        anotherScript = true;
        StringTokenizer stringTokenizer = new StringTokenizer(query, " \t\n\r");
        if (stringTokenizer.countTokens() < 4) {
            return null;
        }
        String token = "";
        if (!stringTokenizer.nextToken().trim().equalsIgnoreCase("select")) {
            return null;
        }
        ArrayList<MonitorConfig> monitorConfigs = new ArrayList<MonitorConfig>();
        while (anotherScript) {
            ArrayList<String> variables = getVariables(stringTokenizer);
            MonitorConfig config = getBasicConfigObject(stringTokenizer);
            if (variables != null && variables.size() > 0 && config != null) {
                for (String variable : variables) {
                    config = (MonitorConfig) config.clone();
                    config.setVariable(variable);
                    monitorConfigs.add(config);
                }
            } else {
                anotherScript = false;
            }
        }

        return monitorConfigs;
    }

    private static MonitorConfig getBasicConfigObject(StringTokenizer stringTokenizer) {
        MonitorConfig config = null;
        if (stringTokenizer.hasMoreTokens()) {
            config = new MonitorConfig();
            config.setDeviceType(stringTokenizer.nextToken().trim());
            if (stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().trim().equalsIgnoreCase("where")) {
                while (stringTokenizer.hasMoreElements()) {
                    String token = stringTokenizer.nextToken();
                    if (token.trim().equalsIgnoreCase("select")) {
                        break;
                    }
                    String[] rule = token.trim().split("=");
                    if (rule.length == 2) {
                        config.getKeyValueMap().put(rule[0].trim(), rule[1].trim());
                    } else {
                        return null;
                    }
                }
            }
        }
        return config;
    }

    private static ArrayList<String> getVariables(StringTokenizer stringTokenizer) {
        ArrayList<String> variables = new ArrayList<String>();
        String token = "";
        while (stringTokenizer.hasMoreTokens() && (token = stringTokenizer.nextToken()) != null) {
            if (token.equalsIgnoreCase("from")) {
                break;
            }
            variables.add(token.trim());
        }
        return variables;
    }
}
