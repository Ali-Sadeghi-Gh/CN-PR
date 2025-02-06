package peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static peer.Peer.getPORT;

public class Listener implements Runnable {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            System.out.println("peer.Listener is running on port " + getPORT());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new Sender(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
