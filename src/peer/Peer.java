package peer;

import java.io.*;
import java.net.*;

public class Peer {
    private static final String TRACKER_ADDRESS = "127.0.0.1";
    private static final int TRACKER_PORT = 6771;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        connect();
        new Thread(new CLI()).start();
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
            System.out.println(socket.getLocalPort());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            end();
        }
    }

    static void end() {
        System.exit(0);
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
}