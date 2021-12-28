package mfipr.dkf3.mySql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Query {
    protected static List<String[]> dumpFilesList = new ArrayList<>();

    public Query() {
        try {
            readDFList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Wczytanie listy plików z zapytaniami do pobrania danych z bazy*/
    private void readDFList() throws IOException {
        Path path = Paths.get("src/main/java/mfipr/dkf3/mySql/pliki.txt");
        if(Files.exists(path)) {
            for (String s : Files.readAllLines(path)) {
                dumpFilesList.add(s.split(","));
            }
        }
    }

    protected String readFile(String fileName) throws IOException {
        String resString = "";
        Path path = Paths.get(fileName);
        if(Files.exists(path)) {
            for (String line : Files.readAllLines(path)) {
                resString += " " + line;
            }
        }
        return resString;
    }
}