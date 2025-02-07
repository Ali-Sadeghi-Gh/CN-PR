package tracker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static tracker.Tracker.getPORT;

public class Manager implements Runnable {

    @Override
    public void run() {
        try (DatagramSocket udpSocket = new DatagramSocket(getPORT())) {
            Runtime.getRuntime().addShutdownHook(new Thread(udpSocket::close));
            System.out.println("tracker.Tracker is running on UDP port " + getPORT());
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                new Thread(new Handler(udpSocket, message, clientAddress, clientPort)).start();
            }
        } catch (IOException e) {
            System.out.println("Error in UDP server: " + e.getMessage());
        }
    }
}
