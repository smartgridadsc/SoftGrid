package it.adsc.smartpower.substation.monitoring.ui;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prageethmahendra on 5/5/2016.
 */
public class MonitorConfig {
    private String deviceType;
    private HashMap<String, String> keyValueMap;
    private String variable;
    private String seriesName;
    private XYSeries xySeries;
    private List<MonitorConfig> monitorConfigs;

    public MonitorConfig() {
        super();
        init();
    }

    private void init() {
        deviceType = "";
        keyValueMap = new HashMap<String, String>();
        variable = new String();
        seriesName = "";
        xySeries = null;
    }

    public XYSeries getXySeries() {
        return xySeries;
    }

    public void setXySeries(XYSeries xySeries) {
        this.xySeries = xySeries;
    }


    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public HashMap<String, String> getKeyValueMap() {
        return keyValueMap;
    }

    public void setKeyValueMap(HashMap<String, String> keyValueMap) {
        this.keyValueMap = keyValueMap;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public List<MonitorConfig> getMonitorConfigs() {
        return monitorConfigs;
    }

    public void setMonitorConfigs(List<MonitorConfig> monitorConfigs) {
        this.monitorConfigs = monitorConfigs;
    }

    @Override
    protected Object clone() {
        MonitorConfig monitorConfig = new MonitorConfig();
        monitorConfig.setDeviceType(deviceType);
        monitorConfig.setVariable(variable);
        monitorConfig.setKeyValueMap(keyValueMap);
        monitorConfig.setSeriesName(seriesName);
        return monitorConfig;
    }

    @Override
    public String toString() {
        String str = deviceType + " :" + variable + ":";
        for (String key : keyValueMap.keySet()) {
            str = str + key + "=" + keyValueMap.get(key);
        }
        return str;
    }

}
