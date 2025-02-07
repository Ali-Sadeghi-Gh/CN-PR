package common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LogReader {

    public static String read(String fileName) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/" + fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading log file: " + e.getMessage());
        }
        return builder.toString();
    }
}
