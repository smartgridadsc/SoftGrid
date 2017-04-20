package it.illinois.adsc.ema.control.db;

import it.illinois.adsc.ema.control.proxy.util.DeviceType;

import java.sql.*;

/**
 * Created by prageethmahendra on 13/4/2017.
 */
public class DataObject extends SavableAdaptor {
    private StateType stateType;
    private DeviceType deviceType;
    private long objID;
    private String value;
    private DataValueType dataValueType;
    private static volatile long lastObjectId = 0;

    public DataObject() {
    }

    public void init() {
        stateType = StateType.FREQUENCY;
        deviceType = DeviceType.BUS;
        objID = 0;
        value = "50.0";
        dataValueType = DataValueType.DOUBLE;
    }


    public void insert(Connection con) {
        PreparedStatement ps = null;
        try {
            int count = 0;
            ps = con.prepareStatement("INSERT INTO  " + H2DBUtil.TRANSIENT_TABLE_NAME + " ( OBJ_ID ," +
                    " DEVICE_TYPE  ," +
                    " STATE_TYPE  ," +
                    " VALUE_TYPE  ," +
                    " DATA_VALUE ) VALUES (?,?,?,?,?)");
            ps.setLong(++count, objID);
            ps.setInt(++count, deviceType.getValue());
            ps.setInt(++count, stateType.getValue());
            ps.setInt(++count, dataValueType.getValue());
            ps.setString(++count, value);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void delete(Connection con) {
        PreparedStatement ps = null;
        try {
            int count = -1;
            ps = con.prepareStatement("DELETE FROM " + H2DBUtil.TRANSIENT_TABLE_NAME + " WHERE OBJ_ID = ?");
            ps.setLong(++count, objID);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void update(Connection con) {
        PreparedStatement ps = null;
        try {
            int count = -1;
            ps = con.prepareStatement("UPDATE " + H2DBUtil.TRANSIENT_TABLE_NAME + " SET " +
                    " DEVICE_TYPE = ? ," +
                    " STATE_TYPE  = ? ," +
                    " VALUE_TYPE  = ? ," +
                    " DATA_VALUE = ? WHERE OBJ_ID = ?");
            ps.setInt(++count, deviceType.getValue());
            ps.setInt(++count, stateType.getValue());
            ps.setInt(++count, dataValueType.getValue());
            ps.setString(++count, value);
            ps.setLong(++count, objID);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void load(Connection con, ResultSet rs) {
        try {
            value = rs.getString("DATA_VALUE");
            objID = rs.getLong("OBJ_ID");

            switch (rs.getInt("VALUE_TYPE")) {
                case 0:
                    dataValueType = DataValueType.DOUBLE;
                    break;
                case 1:
                    dataValueType = DataValueType.LINESTATUS;
                    break;
                case 2:
                    dataValueType = DataValueType.STRING;
                    break;
                case 3:
                    dataValueType = DataValueType.BOOL;
                    break;
                default:
                    dataValueType = DataValueType.STRING;
            }

            switch (rs.getInt("STATE_TYPE")) {
                case 0:
                    stateType = StateType.FREQUENCY;
                    break;
                default:
                    stateType = StateType.FREQUENCY;
            }

            switch (rs.getInt("DEVICE_TYPE")) {
                case 0:
                    deviceType = DeviceType.ROOT;
                    break;
                case 1:
                    deviceType = DeviceType.CIRCUITE_BREACKER;
                    break;
                case 2:
                    deviceType = DeviceType.GENERATOR;
                    break;
                case 3:
                    deviceType = DeviceType.TRANSFORMER;
                    break;
                case 4:
                    deviceType = DeviceType.SHUNT;
                    break;
                case 5:
                    deviceType = DeviceType.BUS;
                    break;
                case 6:
                    deviceType = DeviceType.BRANCH;
                    break;
                case 7:
                    deviceType = DeviceType.LOAD;
                    break;
                case 8:
                    deviceType = DeviceType.MONITOR;
                    break;
                default:
                    deviceType = DeviceType.ROOT;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public long getObjID() {
        return objID;
    }

    public void setObjID(long objID) {
        this.objID = objID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataValueType getDataValueType() {
        return dataValueType;
    }

    public void setDataValueType(DataValueType dataValueType) {
        this.dataValueType = dataValueType;
    }

    public static long _getNextID() {
        return lastObjectId++;
    }
}
