package it.illinois.adsc.ema.control.db;

import java.sql.*;

/**
 * Created by prageethmahendra on 13/4/2017.
 */
@Deprecated
public class CSVDataConnection {
    private static final DBType DB_TYPE = DBType.H2;

    public CSVDataConnection() {
    }

    public void init() {
        Statement stmt = null;
        Connection con = null;
        try {
            Class.forName("org.h2.Driver");
            con = getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("DROP TABLE " + MYSQLDBUtil.TRANSIENT_TABLE_NAME);
            stmt.executeUpdate("CREATE TABLE " + MYSQLDBUtil.TRANSIENT_TABLE_NAME + " ( OBJ_ID INTEGER (50)," +
                    " DEVICE_TYPE NUMBER ," +
                    " STATE_TYPE NUMBER ," +
                    " VALUE_TYPE NUMBER ," +
                    " DATA_VALUE VARCHAR (50))");
            stmt.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        switch (DB_TYPE) {
            case MYSQL:
                return DriverManager.getConnection("jdbc:http://localhost:3686/mysql", "test", "");
            default:
                return DriverManager.getConnection("jdbc:h2:~/test", "test", "");
        }
    }



    // todo remove this after testing
    public static void main(String[] args) {
        try {
            new CSVDataConnection().init();
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("DROP TABLE table1");
            stmt.executeUpdate("CREATE TABLE table1 ( user varchar(50), ID NUMBER )");
            stmt.executeUpdate("INSERT INTO table1 ( user ) VALUES ( 'Claudio' )");
            stmt.executeUpdate("INSERT INTO table1 ( user ) VALUES ( 'Bernasconi' )");

            ResultSet rs = stmt.executeQuery("SELECT * FROM table1");
            while (rs.next()) {
                String name = rs.getString("user");
                System.out.println(name);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
