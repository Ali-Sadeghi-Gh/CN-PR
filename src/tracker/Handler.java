package tracker;

import common.File;
import common.GetFileResult;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static java.lang.String.join;
import static tracker.Tracker.*;

public class Handler implements Runnable {
    private final DatagramSocket socket;
    private final String message;
    private Integer id;
    private final InetAddress clientAddress;
    private final int clientPort;

    public Handler(DatagramSocket socket, String message, InetAddress clientAddress, int clientPort) {
        this.socket = socket;
        this.message = message;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    @Override
    public void run() {
        try {
            String[] parts = message.split(" ");
            id = Integer.valueOf(parts[0]);
            if (!parts[1].equals("port") && !isValid(id)) {
                sendResult("get-port");
                return;
            }
            String command = parts[1];
            String response;
            switch (command) {
                case "share" -> response = handleShare(parts);
                case "get" -> response = handleGet(parts);
                case "port" -> {
                    id = getId();
                    addAddress(id, parts[2]);
                    response = String.valueOf(id);
                }
                case "exit" -> {
                    endPeer(id);
                    response = "exited";
                }
                default -> response = "bad-request";
            }

            sendResult(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendResult(String response) throws IOException {
        byte[] responseBytes = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);
        socket.send(responsePacket);
    }

    private String handleShare(String[] parts) {
        try {
            String fileName = parts[2];
            int fileSize = Integer.parseInt(parts[3]);
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
            String fileName = parts[2];
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