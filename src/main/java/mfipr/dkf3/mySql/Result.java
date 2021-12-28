package mfipr.dkf3.mySql;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Result extends Query{
    protected static void writeToFileQueryResult(String fileName, String stringLine) throws IOException {
        FileWriter fileWriter = new FileWriter("src/main/java/mfipr/dkf3/querySQL/insert/" + fileName + ".txt", true);
        fileWriter.append(stringLine + "\n");
        fileWriter.close();
    }

    protected static void dropFileIfExist(String fileName) throws IOException {
        Path src = Paths.get("src/main/java/mfipr/dkf3/querySQL/insert/" + fileName + ".txt");
        if(Files.exists(src)) {
            System.out.println("Usuniecie: " + fileName);
            Files.delete(src);
        }
    }
}
