package tracker;

import java.util.Scanner;

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
            case "x" -> System.out.println("x");
            default -> System.out.println("Unexpected command: " + command);
        }
    }
}
