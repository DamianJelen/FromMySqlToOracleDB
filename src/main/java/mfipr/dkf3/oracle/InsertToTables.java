package mfipr.dkf3.oracle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InsertToTables {
    private static List<String> insertSrcDataList = new ArrayList<>();
    private static final String QUERY = ") SELECT * FROM TMP_TABLE";

    public static void runInsertQuery() {
        try {
            generateInsertDataFilesList();
            for(String src : insertSrcDataList) {
                fastReadInsertFile(src, 200);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // 1. wczytanie do listy ścieżek plików do załadowania i nazw tabel
    protected static void generateInsertDataFilesList() throws IOException {
        Path path = Paths.get("src/main/java/mfipr/dkf3/mySql/pliki.txt");
        if(Files.exists(path)) {
            for(String s : Files.readAllLines(path)) {
                s = s.split(",")[0];
                String table = s.substring(s.lastIndexOf("/") + 6, s.length() - 4);
                String src = s.substring(0, s.lastIndexOf("/") + 1).replaceAll("dump", "insert") + table + ".txt";
                insertSrcDataList.add(src);
            }
        }
    }

    // 2. wczytanie pliku linia po linii
    protected static String readInsertFile(String src) throws SQLException, IOException {
        File file = new File(src);
        Connection conn = ConnOracle.connOracle();
        if(file.exists()) {
            int rowCount = 0;
            Scanner readLine = new Scanner(file);
            String table = src.substring(src.lastIndexOf("/") + 1, src.length() - 4);
            while (readLine.hasNextLine()) {
                // rozbić na tablicę i metoda zewnętrzna powinna już przekształcić to w taki tekst jak powienien wejść do INSERT0-a
                String insertValues = transformLineToInsertQuery(readLine.nextLine());
                String query = "INSERT INTO " + table + " VALUES(" + insertValues + ")";
                // execute insert query
                PreparedStatement preStm = conn.prepareStatement(query);
                rowCount = preStm.executeUpdate();
                preStm.close();
                ++rowCount;
            }
            conn.close();
            return "Załadowano " + rowCount + " wierszy do tabeli: " + src.substring(src.lastIndexOf("/") + 1, src.length() - 4);
        } else {
            return "Plike nie istnieje: " + src;
        }
    }

    // czytanie pliku kilku linii na raz (do zrobienia już później
    private static void readFile(String src, int rowNum) {
        File file = new File(src);
        try {
            Scanner readFile = new Scanner(file);
            int licz = 0, queryCount = 0;
            String subQuery = "";
            Boolean haveNextLine = readFile.hasNextLine();
            while (haveNextLine) {
                if(licz < rowNum && haveNextLine) {
                    subQuery += " SELECT " + transformLineToInsertQuery(readFile.nextLine()) + " FROM DUAL UNION ALL";
                    haveNextLine = readFile.hasNextLine();
                    ++licz;
                } else {
                    System.out.println("Paczka odczytanych linii z pliku " + ++queryCount);
                    System.out.println(subQuery.substring(0, subQuery.length() - 10));
                    System.out.println("---------------------------------------------");
                    subQuery = "";
                    licz = 0;
                }
            }
            // ostatnia paczka wierszy
            System.out.println("Paczka odczytanych linii z pliku " + ++queryCount);
            System.out.println(subQuery.substring(0, subQuery.length() - 10));
            System.out.println("---------------------------------------------");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void fastReadInsertFile(String srcFile, int rowNum) throws SQLException, IOException {
        File file = new File(srcFile);
        String fileKey = srcFile.substring(srcFile.lastIndexOf("/") + 1, srcFile.length() - 4);
        Connection conn = ConnOracle.connOracle();
        if(file.exists()) {
            Scanner readFile = new Scanner(file);
            int licz = 0, queryCount = 0;
            String subQuery = "";
            Boolean haveNextLine = readFile.hasNextLine();
            while (haveNextLine) {
                if(licz < rowNum && haveNextLine) {
                    subQuery += " SELECT " + transformLineToInsertQuery(readFile.nextLine()) + " FROM DUAL UNION ALL";
                    haveNextLine = readFile.hasNextLine();
                    ++licz;
                } else {
                    PreparedStatement preStmt = conn.prepareStatement(InsertQueries.getInsertQueryMap().get(fileKey) + " (" + subQuery.substring(0, subQuery.length() - 10) + QUERY);
                    preStmt.executeUpdate();
                    preStmt.close();
                    subQuery = "";
                    licz = 0;
                }
            }
            // ostatnia paczka wierszy
            PreparedStatement preStmt = conn.prepareStatement(InsertQueries.getInsertQueryMap().get(fileKey) + " (" + subQuery.substring(0, subQuery.length() - 10) + QUERY);
            preStmt.executeUpdate();
            preStmt.close();
            // jakiś komunikat by widziec co sie dzieje
            System.out.println("Załadowano table " + fileKey);
        } else {
            System.out.println("Plik nie istnieje: " + srcFile);
        }
    }


    // 3. rozbicie linii na tablicę i sprawdzenie jakiego typu są dane i dopisanie odp fragmentu przed
    private static String transformLineToInsertQuery(String txt) {
        String[] strArr = txt.split("_nbsp_");
        txt = "";
        int i = 0;
        for(String s : strArr){
            if(s.length() != s.replaceAll("'","").length()) {
                s = s.replaceAll("'","''");
                txt += "'" + s + "' as r" + ++i + ",";
            } else if("1900-01-01".equals(s)) {
                txt += "NULL as r" + ++i + ",";
            } else if(s.matches("^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2} [0-9]{2}\\:[0-9]{2}\\:[0-9]{2}$")) {
                s = "TO_DATE('" + s + "','YYYY-MM-DD HH24:MI:SS')";
                txt += s + " as r" + ++i + ",";;
            } else if(s.matches("^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}$")) {
                s = "TO_DATE('" + s + "','YYYY-MM-DD')";
                txt += s + " as r" + ++i + ",";
            }  else if(s.matches("^NULL$")) {
                txt += s + " as r" + ++i + ",";
            } else {
                txt += "'" + s + "' as r" + ++i +",";
            }
        }
        txt = txt.replaceAll("&", "'||chr(38)||'");
//        return "Wynik po transformacji";
        return txt.substring(0, txt.length() - 1);
    }
}
