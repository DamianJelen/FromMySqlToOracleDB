package mfipr.dkf3.oracle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DropTables {
    private static List<String> dropQueriesList = new ArrayList<>();

    /**
     * Wczytanie do listy pytań SQL do usuwania tabel
     */
    private static void generateDropQueryList() throws IOException {
        Path path = Paths.get("src/main/java/mfipr/dkf3/mySql/pliki.txt");
        if (Files.exists(path)) {
            for (String src : Files.readAllLines(path)) {
                String tmpStr = src.split(",")[0];
                String query = tmpStr.substring(tmpStr.lastIndexOf("/") + 1, tmpStr.lastIndexOf(".")).replaceAll("dump_", "drop table ");
                dropQueriesList.add(query);
            }
        }
    }


    public static void runDropQueries() {
        try {
            generateDropQueryList();
            Connection conn = ConnOracle.connOracle();
            for (String query : dropQueriesList) {
                if (ConnOracle.existTable(conn, query)) {
                    System.out.println("Wykonano: " + query);
                    ConnOracle.executeQuery(conn, query);
                }
            }
            conn.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
