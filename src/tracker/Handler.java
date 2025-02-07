package tracker;

import common.File;
import common.GetFileResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.String.join;
import static tracker.Tracker.*;

public record Handler(Socket socket, Integer id) implements Runnable {

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            while (true) {
                String request = in.readLine();
                if (request == null) {
                    endPeer(id);
                    break;
                }
                String[] parts = request.split(" ");
                String command = parts[0];

                switch (command) {
                    case "share" -> out.println(handleShare(parts));
                    case "get" -> out.println(handleGet(parts));
                    case "port" -> addAddress(id, parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String handleShare(String[] parts) {
        try {
            String fileName = parts[1];
            int fileSize = Integer.parseInt(parts[2]);
            addFile(id, File.builder()
                    .name(fileName)
                    .size(fileSize)
                    .build());

            return "done";
        } catch (Exception e) {
            return "bad-request";
        }
    }

    private String handleGet(String[] parts) {
        try {
            String fileName = parts[1];
            GetFileResult result = getFile(fileName);
            if (result.found()) {
                return "found " + result.file().name() + " " + result.file().size() + " " + join(" ", result.addresses());
            }
            return "not-found";
        } catch (Exception e) {
            return "bad-request";
        }
    }
}