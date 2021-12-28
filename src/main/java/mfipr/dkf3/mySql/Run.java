package mfipr.dkf3.mySql;

import java.io.IOException;
import java.sql.SQLException;

import static mfipr.dkf3.mySql.ConnMySQL.readDB;
import static mfipr.dkf3.mySql.Result.dropFileIfExist;

public class Run {
    public static void runExportDB() {
        String querySql = "";
        Query query = new Query();
        System.out.println(" ".hashCode());
        try {
            for(String[] src : query.dumpFilesList) {
                querySql = query.readFile(src[0]);
                String fileName = src[0].substring(src[0].lastIndexOf("/") + 6, src[0].length() - 4);
//                System.out.println("Sprawdza czy plik istnieje.");
                dropFileIfExist(fileName);
//                System.out.println("generowanie pliku: " + fileName);
                readDB(querySql, Integer.parseInt(src[1]), fileName);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
