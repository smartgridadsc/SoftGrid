package it.illinois.adsc.ema.control.db;

import it.illinois.adsc.ema.control.proxy.util.DeviceType;

import java.sql.*;

/**
 * Created by prageethmahendra on 13/4/2017.
 */
public class DataObject {
    private String deviceID;
    private String value;
    private Timestamp timestamp;

    public DataObject() {
    }

    public void init() {
        deviceID = "";
        value = "50.0";
    }

    public void load(Connection con, ResultSet rs) {
        try {
            value = rs.getString("MVALUE");
            deviceID = rs.getString("DEVICE_ID");
            timestamp = rs.getTimestamp("START_TIME");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Timestamp=" + timestamp.toString() + ", DeviceID=" + deviceID + ", Value=" + value;
    }
}
