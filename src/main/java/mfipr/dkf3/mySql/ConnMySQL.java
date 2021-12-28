package mfipr.dkf3.mySql;

import java.io.IOException;
import java.sql.*;

import static mfipr.dkf3.mySql.Result.writeToFileQueryResult;

public class ConnMySQL {
    private static Connection connMySQL() throws SQLException {
        ConnAdress conn = new ConnAdress();
        return DriverManager.getConnection(conn.getAdress(conn.getDbName()),conn.getUSR1(),conn.getUSR2());
    }

    protected static void readDB(String query, int countColumn, String fileName) throws SQLException, IOException {
        Connection connMySql = connMySQL();
        PreparedStatement preStm = connMySql.prepareStatement(query);
        ResultSet resSet = preStm.executeQuery();
        while (resSet.next()) {
            String tmpStr = "";
            for (int i = 1; i <= countColumn; i++) {
                tmpStr += "_nbsp_" +  resSet.getString(i);
            }
            tmpStr = tmpStr.substring(6).replaceAll("[\\n\\t\\\u00AD\\ \\ \\ \\●\\•\\\u2028\\\uF02D\\\u2029]+", " ").replaceAll("[\\\u0082\\\u0085]+","").replaceAll("\\ $", "").replaceAll("[\\…]+","…");
            writeToFileQueryResult(fileName, tmpStr);
        }
        resSet.close();
    }
}
