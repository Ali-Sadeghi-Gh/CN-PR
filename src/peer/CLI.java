package peer;

import java.util.Scanner;

import static peer.Peer.end;
import static peer.Peer.sendRequest;

public class CLI implements Runnable {

    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (true) {
            try {
                handle(scanner.nextLine().split(" "));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handle(String[] parts) {
        String command = parts[0];
        switch (command) {
            case "q" -> end();
            case "share" -> shareFile(parts[1], parts[2]);
            case "get" -> getPeersForFile(parts[1]);
            default -> System.out.println("Unexpected command: " + command);
        }
    }

    private static void shareFile(String fileName, String size) {
        String request = "share " + fileName + " " + size;
        System.out.println(sendRequest(request));
    }

    private static void getPeersForFile(String fileName) {
        String request = "get " + fileName;
        System.out.println(sendRequest(request));
    }
}
