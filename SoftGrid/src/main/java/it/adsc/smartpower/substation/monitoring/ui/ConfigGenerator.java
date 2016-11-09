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
