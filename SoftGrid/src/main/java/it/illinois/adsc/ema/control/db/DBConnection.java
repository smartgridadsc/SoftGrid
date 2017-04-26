package it.illinois.adsc.ema.control.db;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import it.illinois.adsc.ema.control.proxy.util.DeviceType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prageethmahendra on 21/4/2017.
 */
public class DBConnection {

    private static DBConnection connection = null;
    private MysqlDataSource dataSource = new MysqlDataSource();

    private DBConnection() {
        init();
    }

    public static DBConnection getConnection() {
        if (connection == null) {
            connection = new DBConnection();
        }
        return connection;
    }

    private void init() {
        dataSource.setUser("root");
        dataSource.setPassword("root");
        dataSource.setDatabaseName("trans_data");
        dataSource.setServerName("localhost");
    }

    public boolean isStable() {
        String stbQuery = "SELECT 1 as stable FROM dual " +
                "WHERE (select MAX(MVALUE) from TRANS_DATA WHERE START_TIME > NOW()) -" +
                " (select MIN(MVALUE) from TRANS_DATA WHERE START_TIME > NOW()) < 0.1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            long curTime = System.currentTimeMillis();
            long end = curTime + 1000;
            stmt = conn.prepareStatement(stbQuery);
            rs = stmt.executeQuery();
            // todo no need to return a list. should get only the first object and return
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MYSQLDBUtil.closeResultSet(rs);
            MYSQLDBUtil.closeStatement(stmt);
            MYSQLDBUtil.closeConnection(conn);
        }
        return false;
    }

    public DataObject getDataObject(int iedID, DeviceType deviceType, StateType stateType) {
        String deviceID = _generateDeviceID(iedID, deviceType, stateType);
        if (deviceID == null) {
            return null;
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            System.out.println(deviceID);
            long curTime = System.currentTimeMillis();
            long end = curTime + 1000;
            stmt = conn.prepareStatement("SELECT * FROM TRANS_DATA WHERE DEVICE_ID like '%"
                    + deviceID + "%' AND START_TIME BETWEEN ? AND ? ORDER BY SAVE_COUNT, START_TIME");
            stmt.setTimestamp(1, new Timestamp(curTime));
            stmt.setTimestamp(2, new Timestamp(end));
            rs = stmt.executeQuery();
            // todo no need to return a list. should get only the first object and return
            if (rs.next()) {
                DataObject dataObject = new DataObject();
                dataObject.init();
                dataObject.load(conn, rs);
                return dataObject;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MYSQLDBUtil.closeResultSet(rs);
            MYSQLDBUtil.closeStatement(stmt);
            MYSQLDBUtil.closeConnection(conn);
        }
        return null;
    }

    private String _generateDeviceID(int iedID, DeviceType deviceType, StateType stateType) {
        switch (stateType) {
            case FREQUENCY:
                if (deviceType == DeviceType.BUS) {
                    return "Bus   " + iedID + "  Frequency";
                }
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        while (true) {
            DataObject dataObjects = DBConnection.getConnection().getDataObject(
                    48, DeviceType.BUS, StateType.FREQUENCY);
            System.out.println(dataObjects.toString());
            System.out.println("Stable=" + DBConnection.getConnection().isStable());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
