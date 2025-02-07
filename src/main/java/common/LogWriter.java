package common;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LogWriter {

    public static void write(String fileName, String log) {
        try (FileWriter fileWriter = new FileWriter("src/resources/" + fileName, true)) {
            fileWriter.write(new Date() + " " + log + "\n");
            fileWriter.flush();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}

