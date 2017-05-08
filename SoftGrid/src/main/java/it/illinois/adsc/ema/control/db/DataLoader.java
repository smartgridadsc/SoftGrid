//package it.illinois.adsc.ema.control.db;
//
//import org.h2.tools.Csv;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Savepoint;
//
///**
// * Created by prageethmahendra on 20/4/2017.
// */
//@Deprecated
//public class DataLoader {
//
//    public void go() throws IOException, SQLException {
//        Connection con = null;
//        ResultSet rs = null;
//        System.out.println("Connected to database.");
//        Savepoint savept1 = null;
//        try {
//            con = CSVDataConnection.getConnection();
//            con.setAutoCommit(false);
//            savept1 = con.setSavepoint();
//            Csv csv = new Csv();
//            rs = csv.read("file name", null, "");
//            while (rs.next()) {
//                DataObject dataObject = new DataObject();
//                dataObject.init();
//                dataObject.setDeviceID(DataObject._getNextID());
//                dataObject.save(con, SavableStatus.INSERT);
//            }
//            con.commit();
//            con.releaseSavepoint(savept1);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            if (con != null) {
//                con.rollback(savept1);
//            }
//        } finally {
//            MYSQLDBUtil.closeConnection(con);
//            MYSQLDBUtil.closeResultSet(rs);
//        }
//    }
//}
