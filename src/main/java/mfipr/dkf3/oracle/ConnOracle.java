package mfipr.dkf3.oracle;

import java.sql.*;

public class ConnOracle {
    static Connection connOracle() throws SQLException {
        ConnAdress conn = new ConnAdress();
        return DriverManager.getConnection(conn.getAdress(conn.getDbName()), conn.getUSR1(), conn.getUSR2());
    }

    static void executeQuery(Connection conn, String query) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(query);
        statement.executeQuery();
    }

    static boolean existTable(Connection conn, String tableName) throws SQLException {
        PreparedStatement preStm = conn.prepareStatement("SELECT COUNT(*) FROM USER_TABLES WHERE UPPER(TABLE_NAME) LIKE ?");
        preStm.setString(1, tableName.substring(tableName.lastIndexOf(" ") + 1).toUpperCase());
        ResultSet resSet = preStm.executeQuery();
        resSet.next();
        return "1".equals(resSet.getString(1));
    }
}
