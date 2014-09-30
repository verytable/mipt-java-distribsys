import java.net.*;
import java.io.*;

/**
 * Created by arseny on 23.09.14.
 */
public class UDP_client {

    public static void main(String args[]) throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        int port = 50000;
        byte[] buffer = new byte[1000];
        int length = System.in.read(buffer);
        DatagramPacket packet =
            new DatagramPacket(buffer, 0, length, address, port);
        DatagramSocket socket = new DatagramSocket();

        socket.send(packet);

        System.out.println("send successfully");
    }
}
