package mfipr.dkf3.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class Statistics {
    private static final String SCHEMA_STATS = "CALL DBMS_STATS.GATHER_SCHEMA_STATS('...')";


    protected static void runStats() {
        try (Connection conn = ConnOracle.connOracle()) {
            CallableStatement callStmt = conn.prepareCall(SCHEMA_STATS);
            callStmt.executeQuery();
            callStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
