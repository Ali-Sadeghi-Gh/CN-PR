package peer;

import common.File;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static peer.Peer.*;

public class CLI implements Runnable {

    private static final String PEER_ADDRESS = "127.0.0.1";
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
            case "add-file" -> addFileAndPrint(parts[1], Integer.parseInt(parts[2]));
            case "my-files" -> printMyFiles();
            case "share" -> shareFile(parts[1], parts[2]);
            case "get" -> getPeersForFile(parts[1]);
            case "get-file" -> getFile(parts[1], parts[2]);
            case "get-id" -> getId();
            case "logs" -> printLogs();
            default -> System.out.println("Unexpected command: " + command);
        }
    }

    private void shareFile(String fileName, String size) {
        File file = File.builder()
                .name(fileName)
                .build();
        if (!containsFile(file)) {
            System.out.println("you don't have this file");
            return;
        }
        String request = "share " + fileName + " " + size;
        System.out.println(sendRequest(request));
    }

    private void getPeersForFile(String fileName) {
        String request = "get " + fileName;
        System.out.println(sendRequest(request));
    }

    private void printMyFiles() {
        for (File file : getMyFiles())
            System.out.println(file.name() + " " + file.size() + " " + file.content());
    }

    private void addFileAndPrint(String fileName, int size) {
        File file = File.builder()
                .name(fileName)
                .size(size)
                .content(scanner.nextLine())
                .build();

        if (containsFile(file)) {
            System.out.println("duplicate file");
            return;
        }
        addFile(file);
        System.out.println("done");
    }

    private void getFile(String address, String fileName) {
        try (Socket socket = new Socket(PEER_ADDRESS, Integer.parseInt(address));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("get " + fileName);
            String[] result = in.readLine().split(" ");
            if ("found".equals(result[0])) {
                File file = File.builder()
                        .name(result[1])
                        .size(Integer.parseInt(result[2]))
                        .content(in.readLine())
                        .build();
                addFile(file);
                shareFile(file.name(), String.valueOf(file.size()));
            } else
                System.out.println(result[0]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
