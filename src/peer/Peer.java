package peer;

import common.File;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Peer {
    private static final Set<File> files = new HashSet<>();
    private static final String TRACKER_ADDRESS = "127.0.0.1";
    private static final int TRACKER_PORT = 6771;
    private static final int PORT = new Random().nextInt(10000, 65000);
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        connect();
        new Thread(new CLI()).start();
        new Thread(new Listener()).start();
    }

    private static void connect() {
        try {
            Socket socket = new Socket(TRACKER_ADDRESS, TRACKER_PORT);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("port " + PORT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            end();
        }
    }

    static void end() {
        System.exit(0);
    }

    static int getServerPORT() {
        return PORT;
    }

    static String sendRequest(String request) {
        out.println(request);
        try {
            return in.readLine();
        } catch (IOException e) {
            System.out.println("try to reconnect");
            connect();
        }
        return "";
    }

    static boolean containsFile(File file) {
        return files.contains(file);
    }

    static void addFile(File file) {
        files.add(file);
    }

    static Set<File> getMyFiles() {
        return files;
    }

    static Optional<File> getFile(String fileName) {
        return files.stream()
                .filter(f -> f.name().equals(fileName))
                .findFirst();
    }
}