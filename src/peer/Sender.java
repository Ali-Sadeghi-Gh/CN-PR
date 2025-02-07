package peer;

import common.File;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

import static peer.Peer.getFile;

public record Sender(Socket socket) implements Runnable {

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
                if (request == null)
                    break;
                String[] parts = request.split(" ");
                String command = parts[0];

                if (command.equals("get")) {
                    Optional<File> fileOptional = getFile(parts[1]);
                    if (fileOptional.isPresent()) {
                        File file = fileOptional.get();
                        out.println("found " + file.name() + " " + file.size() + " " + file.content());
                        System.out.println(request + " done");
                    } else
                        out.println("not-found");
                }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
