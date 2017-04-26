//package it.illinois.adsc.ema.control.db.test;
//
//
//import it.illinois.adsc.ema.control.db.*;
//import it.illinois.adsc.ema.control.proxy.util.DeviceType;
//import junit.framework.Assert;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
///**
// * Created by prageethmahendra on 17/4/2017.
// */
//public class DBIntegrationTest {
//
//    public static void main(String[] args) {
//        CSVDataConnection dataConnection = new CSVDataConnection();
//        dataConnection.init();
//        Connection con = null;
//        ResultSet rs = null;
//        PreparedStatement stmt = null;
//        try {
//            con = CSVDataConnection.getConnection();
//            DataObject dataObject = new DataObject();
//            dataObject.init();
//            dataObject.setValue("ABC");
//            dataObject.setStateType(StateType.FREQUENCY);
//            dataObject.setDeviceType(DeviceType.BUS);
//            dataObject.setDeviceID(100);
//            dataObject.setDataValueType(DataValueType.DOUBLE);
//            dataObject.insert(con);
//            stmt = con.prepareStatement("select * from " + MYSQLDBUtil.TRANSIENT_TABLE_NAME);
//            rs = stmt.executeQuery();
//            dataObject = new DataObject();
//            dataObject.init();
//            while (rs.next()) {
//                dataObject.load(con, rs);
//            }
//            Assert.assertEquals(dataObject.getValue(), "ABC");
//            Assert.assertEquals(dataObject.getStateType(), StateType.FREQUENCY);
//            Assert.assertEquals(dataObject.getDeviceType(), DeviceType.BUS);
//            Assert.assertEquals(dataObject.getDeviceID(), 100);
//            Assert.assertEquals(dataObject.getDataValueType(), DataValueType.DOUBLE);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            MYSQLDBUtil.closeResultSet(rs);
//            MYSQLDBUtil.closeStatement(stmt);
//            MYSQLDBUtil.closeConnection(con);
//        }
//    }
//}
//
