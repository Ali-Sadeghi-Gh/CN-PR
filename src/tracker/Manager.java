package tracker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static tracker.Tracker.getId;
import static tracker.Tracker.getPORT;

public class Manager implements Runnable {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(getPORT())) {
            System.out.println("tracker.Tracker is running on port " + getPORT());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new Handler(clientSocket,
                        getId())).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
