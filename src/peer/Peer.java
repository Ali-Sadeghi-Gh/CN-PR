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
    private static DatagramSocket socket;
    private static Integer peerId = -1;

    public static void main(String[] args) {
        connect();
        new Thread(new CLI()).start();
        new Thread(new Listener()).start();
    }

    private static void connect() {
        try {
            socket = new DatagramSocket(PORT);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println(sendRequest("exit"));
                socket.close();
            }));
            getPort();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            end();
        }
    }

    private static void getPort() {
        peerId = Integer.valueOf(sendRequest("port " + PORT));
        System.out.println("peerId is: " + peerId);
    }

    static void end() {
        System.exit(0);
    }

    static int getServerPORT() {
        return PORT;
    }

    static String sendRequest(String request) {
        try {
            String finalRequest = peerId + " " + request;
            byte[] requestBytes = finalRequest.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length,
                    InetAddress.getByName(TRACKER_ADDRESS), TRACKER_PORT);
            socket.send(requestPacket);

            byte[] buffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

            socket.receive(responsePacket);
            String result = new String(responsePacket.getData(), 0, responsePacket.getLength());
            if (result.equals("get-port")) {
                getPort();
                return "try again";
            }
            return result;
        } catch (IOException e) {
            System.out.println("Error sending request: " + e.getMessage());
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