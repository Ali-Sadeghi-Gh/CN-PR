package tracker;

import java.util.Scanner;

import static common.LogReader.read;
import static tracker.Tracker.end;

public class CLI implements Runnable {

    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (true) {
            handle(scanner.nextLine());
        }
    }

    private void handle(String command) {
        switch (command) {
            case "q" -> end();
            case "all-logs" -> printAllLogs();
            case "request-logs" -> printRequestLogs();
            case "file-logs" -> printFileLogs(scanner.nextLine());
            default -> System.out.println("Unexpected command: " + command);
        }
    }

    private void printFileLogs(String name) {
        read(name + ".txt");
    }

    private void printAllLogs() {
        System.out.println(read("all.txt"));
    }

    private void printRequestLogs() {
        System.out.println(read("requests.txt"));
    }
}
