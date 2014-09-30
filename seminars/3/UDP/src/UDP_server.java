import java.net.*;
import java.io.*;

/**
 * Created by arseny on 23.09.14.
 */
public class UDP_server {

    public static void main(String args[]) throws Exception {
        int port =  50000;
        int length = 1000;
        byte[] buffer = new byte[length];
        DatagramPacket packet = new DatagramPacket(buffer, length);
        DatagramSocket socket = new DatagramSocket(port);

        socket.receive(packet);

        String result =
            new String(packet.getData(), packet.getOffset(), packet.getLength());

        System.out.println("received: " + result);
    }
}
