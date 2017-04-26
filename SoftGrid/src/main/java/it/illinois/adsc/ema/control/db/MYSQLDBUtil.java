package it.illinois.adsc.ema.control.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by prageethmahendra on 13/4/2017.
 */
public class MYSQLDBUtil {
    public static final String TRANSIENT_TABLE_NAME = "TRANSIENT_DATA";

    public static void closeConnection(Connection con)
    {
        if(con != null)
        {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(Statement stmt)
    {
        if(stmt != null)
        {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if(rs != null)
        {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
