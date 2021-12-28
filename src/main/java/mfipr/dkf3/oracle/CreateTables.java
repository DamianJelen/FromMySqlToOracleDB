package mfipr.dkf3.oracle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateTables {
    protected static List<String> createFilesList = new ArrayList<>();

    /** Wczytanie do listy ścieżek plików do tworzenia tabel */
     private static void readCFList() throws IOException {
         Path path = Paths.get("src/main/java/mfipr/dkf3/mySql/pliki.txt");
         if(Files.exists(path)) {
             for(String s : Files.readAllLines(path)) {
                 String tmpStr = s.split(",")[0];
                 String fileName = tmpStr.substring(tmpStr.lastIndexOf("/") + 1).replaceAll("dump","c");
                 String src = tmpStr.substring(0, tmpStr.lastIndexOf("/")).replaceAll("dump", "create/") + fileName;
                 createFilesList.add(src);
             }
         }
     }

     protected static String readQuery(String fileName) throws IOException {
         String resultStr = "";
         Path path = Paths.get(fileName);
         if(Files.exists(path)) {
             for(String line : Files.readAllLines(path)) {
                 resultStr += " " + line;
             }
         }
         return resultStr;
     }

    public static void runQueries() {
        try {
            readCFList();
            Connection conn = ConnOracle.connOracle();
            for(String file : createFilesList){
                String query = readQuery(file);
                String tabInDB = file.substring(file.lastIndexOf("/") + 3, file.length() -4);
                if(!ConnOracle.existTable(conn, tabInDB)) {
                    System.out.println("Tworzona jest tabela z zapytania: " + tabInDB + ".sql");
                    ConnOracle.executeQuery(conn, query);
                } else {
                    System.out.println("Nie utworzono tabeli " + tabInDB + ". Już isnieje taka tabela.");
                }
            }
            conn.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
